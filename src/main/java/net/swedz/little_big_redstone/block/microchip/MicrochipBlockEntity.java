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
import net.swedz.little_big_redstone.api.Tickable;
import net.swedz.little_big_redstone.client.model.microchip.MicrochipModelData;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.MicrochipSize;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.network.packet.UpdateComponentsMicrochipPacket;
import net.swedz.little_big_redstone.network.packet.UpdateMicrochipPacket;
import net.swedz.tesseract.neoforge.packet.CustomPacket;

import java.util.function.Function;

public final class MicrochipBlockEntity extends BlockEntity implements MenuProvider, Tickable
{
	private final Microchip microchip;
	
	private boolean            modelDataChanged = true;
	private MicrochipModelData modelData;
	
	public MicrochipBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(LBRBlocks.MICROCHIP_ENTITY.get(), pos, blockState);
		
		microchip = new Microchip(MicrochipSize.create(0.5f));
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
		return new MicrochipMenu(containerId, inventory, worldPosition, this::isMenuValid, microchip, this.color());
	}
	
	public boolean openMenu(Player player)
	{
		if(this.isMenuValid(player))
		{
			player.openMenu(this, (buf) ->
			{
				buf.writeBlockPos(worldPosition);
				Microchip.STREAM_CODEC.encode(buf, microchip);
				DyeColor.STREAM_CODEC.encode(buf, this.color());
			});
			return true;
		}
		return false;
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		microchip.awarenesses().preTick(new AwarenessContext(this));
		
		LogicContext context = new LogicContext(this);
		microchip.tickLogic(context);
		
		boolean microchipDirty = microchip.isDirty();
		boolean contextDirty = context.isDirty();
		if(microchipDirty || contextDirty)
		{
			microchip.markClean();
			
			microchip.awarenesses().postTick(new AwarenessContext(this), microchipDirty, contextDirty);
			
			this.setChanged();
			
			if(microchipDirty)
			{
				var newModelData = this.createModelData();
				if(!newModelData.equals(modelData))
				{
					modelData = newModelData;
					this.sync();
				}
				this.publishUpdatePacket((container) -> new UpdateMicrochipPacket(container, microchip));
			}
			else if(contextDirty)
			{
				this.publishUpdatePacket((container) -> new UpdateComponentsMicrochipPacket(container, context.getDirtyEntries()));
			}
		}
	}
	
	private void publishUpdatePacket(Function<Integer, CustomPacket> packetCreator)
	{
		for(var player : level.players())
		{
			if(player.containerMenu instanceof MicrochipMenu menu && worldPosition.equals(menu.blockPos()))
			{
				packetCreator.apply(menu.containerId).sendToClient((ServerPlayer) player);
			}
		}
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
	}
}
