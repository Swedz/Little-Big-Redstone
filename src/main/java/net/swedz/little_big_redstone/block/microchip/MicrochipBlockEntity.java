package net.swedz.little_big_redstone.block.microchip;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.client.model.microchip.MicrochipModelData;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipViewPosition;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.MicrochipSize;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
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
	
	private final Microchip microchip;
	
	private UUID placedBy;
	
	private MicrochipViewPosition viewPosition;
	
	private boolean            modelDataChanged = true;
	private MicrochipModelData modelData;
	
	public MicrochipBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(LBRBlocks.MICROCHIP_ENTITY.get(), pos, blockState);
		
		microchip = new Microchip(MicrochipSize.create(CIRCUIT_BOUNDS, 0.5f));
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
			data.sides(redstone.getSides());
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
			player.openMenu(this, (buf) ->
			{
				buf.writeBlockPos(worldPosition);
				Microchip.STREAM_CODEC.encode(buf, microchip);
				DyeColor.STREAM_CODEC.encode(buf, this.color());
				MicrochipViewPosition.STREAM_CODEC.encode(buf, viewPosition);
			});
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
		
		var logicContext = new LogicContext(this);
		microchip.tickLogic(logicContext);
		
		var microchipDirty = microchip.isDirty();
		var contextDirty = logicContext.isDirty();
		if(microchipDirty || contextDirty)
		{
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
						(container) -> new UpdateMicrochipMenuPacket(container, microchip),
						() -> new UpdateMicrochipWatcherPacket(microchip)
				);
			}
			else if(contextDirty)
			{
				this.publishUpdatePacket((container) -> new UpdateComponentsMicrochipMenuPacket(container, logicContext.getDirtyEntries()));
			}
		}
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		
		microchip.awarenesses().removedAll(new AwarenessContext(this));
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
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		
		if(tag.contains("microchip", Tag.TAG_COMPOUND))
		{
			Microchip.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("microchip"))
					.ifSuccess(microchip::loadFrom)
					.ifError((error) ->
							LBR.LOGGER.error("Failed to load microchip data at {}: {}", worldPosition.toShortString(), error.message()));
		}
		else
		{
			microchip.clear();
		}
		
		placedBy = tag.hasUUID("placed_by") ? tag.getUUID("placed_by") : null;
		
		if(tag.contains("view_position", Tag.TAG_COMPOUND))
		{
			MicrochipViewPosition.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("view_position"))
					.ifSuccess((result) -> viewPosition = result)
					.ifError((error) ->
							LBR.LOGGER.error("Failed to load microchip data at {}: {}", worldPosition.toShortString(), error.message()));
		}
		else
		{
			viewPosition = null;
		}
		
		if(level != null && level.isClientSide())
		{
			if(tag.contains("microchip_model_data", Tag.TAG_COMPOUND))
			{
				MicrochipModelData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("microchip_model_data"))
						.ifSuccess((modelData) -> this.modelData = modelData)
						.ifError((error) ->
						{
							modelData = null;
							LBR.LOGGER.error("Failed to load microchip model data at {}: {}", worldPosition.toShortString(), error.message());
						});
				level.sendBlockUpdated(worldPosition, null, null, 0);
				this.requestModelDataUpdate();
			}
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		
		tag.put("microchip", Microchip.CODEC.encodeStart(NbtOps.INSTANCE, microchip).getOrThrow());
		
		if(placedBy != null)
		{
			tag.putUUID("placed_by", placedBy);
		}
		else
		{
			tag.remove("placed_by");
		}
		
		if(viewPosition != null)
		{
			tag.put("view_position", MicrochipViewPosition.CODEC.encodeStart(NbtOps.INSTANCE, viewPosition).getOrThrow());
		}
	}
}
