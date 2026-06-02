package net.swedz.little_big_redstone.block.microchip;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.client.model.microchip.MicrochipModelData;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipViewPosition;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.MicrochipSize;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.network.packet.UpdateComponentsMicrochipMenuPacket;
import net.swedz.little_big_redstone.network.packet.UpdateMicrochipMenuPacket;
import net.swedz.little_big_redstone.network.packet.UpdateMicrochipWatcherPacket;
import net.swedz.tesseract.neoforge.api.Bounds;
import net.swedz.tesseract.neoforge.api.Tickable;
import net.swedz.tesseract.neoforge.packet.CustomPacket;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public final class MicrochipBlockEntity extends BlockEntity implements MenuProvider, Tickable
{
	public static final Bounds CIRCUIT_BOUNDS = new Bounds(0, 0, 240, 128);
	public static final float  CIRCUIT_SCALE  = 0.5f;
	
	private final Microchip microchip;
	
	private UUID placedBy;
	
	private MicrochipViewPosition viewPosition;
	
	private boolean            modelDataChanged = true;
	private MicrochipModelData modelData;
	
	public MicrochipBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(LBRBlocks.MICROCHIP_ENTITY.get(), pos, blockState);
		
		microchip = new Microchip(MicrochipSize.create(CIRCUIT_BOUNDS, CIRCUIT_SCALE));
	}
	
	public Microchip microchip()
	{
		return microchip;
	}
	
	public DyeColor color()
	{
		return ((MicrochipBlock) this.getBlockState().getBlock()).color();
	}
	
	@Override
	public Component getDisplayName()
	{
		return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
	}
	
	public void setPlacedBy(UUID uuid)
	{
		placedBy = uuid;
		this.setChanged();
	}
	
	public UUID getPlacedBy()
	{
		return placedBy;
	}
	
	public void setViewPosition(MicrochipViewPosition viewPosition)
	{
		this.viewPosition = viewPosition;
		this.setChanged();
	}
	
	public MicrochipViewPosition getViewPosition()
	{
		if(viewPosition == null)
		{
			viewPosition = new MicrochipViewPosition();
		}
		return viewPosition;
	}
	
	public void sync()
	{
		if(!(level instanceof ServerLevel serverLevel))
		{
			throw new IllegalStateException("Cannot call sync() on the logical client");
		}
		modelDataChanged = true;
		serverLevel.getChunkSource().blockChanged(worldPosition);
	}
	
	private MicrochipModelData createModelData()
	{
		var data = new MicrochipModelData();
		var redstone = microchip.awarenesses().get(AwarenessTypes.REDSTONE);
		if(redstone != null)
		{
			var state = this.getBlockState();
			for(int index = 0; index < redstone.getSides().length; index++)
			{
				if(redstone.getSides()[index])
				{
					var direction = Direction.values()[index];
					var powered = state.getValue(MicrochipBlock.getDirectionalState(direction));
					var side = powered ?
							MicrochipModelData.Side.ON :
							MicrochipModelData.Side.OFF;
					data.side(index, side);
				}
			}
		}
		return data;
	}
	
	@Override
	public ModelData getModelData()
	{
		return ModelData.builder()
				.with(MicrochipModelData.KEY, modelData)
				.build();
	}
	
	private boolean isMenuValid(Player player)
	{
		return !this.isRemoved() &&
			   player.level() == level && worldPosition.getCenter().distanceTo(player.position()) <= 16;
	}
	
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
	{
		return new MicrochipMenu(containerId, inventory, worldPosition, this::isMenuValid, microchip, this.color(), this.getViewPosition());
	}
	
	public boolean openMenu(Player player, MicrochipViewPosition viewPosition)
	{
		if(this.isMenuValid(player))
		{
			if(placedBy == null)
			{
				this.setPlacedBy(player.getUUID());
			}
			player.openMenu(
					this, (buf) ->
					{
						buf.writeBlockPos(worldPosition);
						Microchip.STREAM_CODEC.encode(buf, microchip);
						DyeColor.STREAM_CODEC.encode(buf, this.color());
						MicrochipViewPosition.STREAM_CODEC.encode(buf, viewPosition);
					}
			);
			return true;
		}
		return false;
	}
	
	public boolean openMenu(Player player)
	{
		return this.openMenu(player, this.getViewPosition());
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		var awarenesses = microchip.awarenesses();
		var awarenessContext = new AwarenessContext(this);
		awarenesses.removed(awarenessContext);
		awarenesses.preTick(awarenessContext);
		
		var context = new LogicTickingContext(this);
		microchip.tickLogic(context);
		
		var microchipDirty = microchip.isDirty();
		var contextDirty = context.isDirty();
		if(microchipDirty || contextDirty)
		{
			var rerouteWires = microchip.isWireRouteDirty();
			microchip.markClean();
			
			awarenesses.postTick(awarenessContext, microchipDirty, contextDirty);
			
			this.setChanged();
			
			if(microchipDirty)
			{
				var newModelData = this.createModelData();
				if(!newModelData.equals(modelData))
				{
					modelData = newModelData;
					this.sync();
				}
				this.publishUpdatePacket(
						(container) -> new UpdateMicrochipMenuPacket(container, microchip, rerouteWires),
						() -> new UpdateMicrochipWatcherPacket(microchip)
				);
			}
			else if(contextDirty)
			{
				this.publishUpdatePacket((container) -> new UpdateComponentsMicrochipMenuPacket(container, context.getDirtyEntries()));
			}
		}
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		
		microchip.awarenesses().removedAll(new AwarenessContext(this));
	}
	
	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state)
	{
		if(level != null)
		{
			for(var entry : microchip.objects())
			{
				Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), entry.toStack());
			}
			var redstoneBits = new ItemStack(LBRItems.REDSTONE_BIT, microchip.wires().values().size());
			Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), redstoneBits);
		}
	}
	
	private void publishUpdatePacket(Function<Integer, CustomPacket> containerPacketCreator, Supplier<CustomPacket> watcherPacketCreator)
	{
		for(var player : level.players())
		{
			if(player.containerMenu instanceof MicrochipMenu menu && worldPosition.equals(menu.blockPos()))
			{
				containerPacketCreator.apply(menu.containerId).sendToClient((ServerPlayer) player);
			}
			
			if(worldPosition.equals(player.getWatchedMicrochip()) && watcherPacketCreator != null)
			{
				watcherPacketCreator.get().sendToClient((ServerPlayer) player);
			}
		}
	}
	
	private void publishUpdatePacket(Function<Integer, CustomPacket> containerPacketCreator)
	{
		this.publishUpdatePacket(containerPacketCreator, null);
	}
	
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		var tag = new CompoundTag();
		if(modelDataChanged)
		{
			modelDataChanged = false;
			tag.put("microchip_model_data", MicrochipModelData.CODEC.encodeStart(NbtOps.INSTANCE, this.createModelData()).getOrThrow());
		}
		return tag;
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	protected void loadAdditional(ValueInput input)
	{
		super.loadAdditional(input);
		
		input.read("microchip", Microchip.CODEC).ifPresentOrElse(
				microchip::loadFrom,
				microchip::clear
		);
		
		placedBy = input.read("placed_by", UUIDUtil.CODEC).orElse(null);
		
		input.read("view_position", MicrochipViewPosition.CODEC).ifPresentOrElse(
				(result) -> viewPosition = result,
				() -> viewPosition = null
		);
		
		if(level != null && level.isClientSide() &&
		   input.keySet().contains("microchip_model_data"))
		{
			input.read("microchip_model_data", MicrochipModelData.CODEC).ifPresentOrElse(
					(modelData) -> this.modelData = modelData,
					() -> modelData = null
			);
			level.sendBlockUpdated(worldPosition, null, null, 0);
			this.requestModelDataUpdate();
		}
	}
	
	@Override
	protected void saveAdditional(ValueOutput output)
	{
		super.saveAdditional(output);
		
		output.store("microchip", Microchip.CODEC, microchip);
		
		output.storeNullable("placed_by", UUIDUtil.CODEC, placedBy);
		
		output.storeNullable("view_position", MicrochipViewPosition.CODEC, viewPosition);
	}
}
