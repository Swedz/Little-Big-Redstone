package net.swedz.little_big_redstone.block.microchip;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.api.Tickable;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.MicrochipSize;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.network.packet.UpdateComponentsMicrochipPacket;
import net.swedz.little_big_redstone.network.packet.UpdateMicrochipPacket;
import net.swedz.tesseract.neoforge.packet.CustomPacket;

import java.util.function.Function;

public final class MicrochipBlockEntity extends BlockEntity implements MenuProvider, Tickable
{
	private final Microchip microchip;
	
	public MicrochipBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(LBRBlocks.MICROCHIP_ENTITY.get(), pos, blockState);
		
		microchip = new Microchip(MicrochipSize.create(0.75f));
	}
	
	public Microchip microchip()
	{
		return microchip;
	}
	
	@Override
	public Component getDisplayName()
	{
		return Component.translatable(LBRBlocks.MICROCHIP.identifier().location().toLanguageKey("block"));
	}
	
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
	{
		return new MicrochipMenu(containerId, inventory, worldPosition, () -> !this.isRemoved(), microchip);
	}
	
	public void openMenu(Player player)
	{
		player.openMenu(this, (buf) ->
		{
			buf.writeBlockPos(worldPosition);
			Microchip.STREAM_CODEC.encode(buf, microchip);
		});
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
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		
		tag.put("microchip", Microchip.CODEC.encodeStart(NbtOps.INSTANCE, microchip).getOrThrow());
	}
}
