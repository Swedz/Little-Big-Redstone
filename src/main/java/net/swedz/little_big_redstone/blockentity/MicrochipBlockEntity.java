package net.swedz.little_big_redstone.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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

public final class MicrochipBlockEntity extends BlockEntity implements MenuProvider, Tickable
{
	private final Microchip microchip;
	
	public MicrochipBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(LBRBlocks.MICROCHIP_ENTITY.get(), pos, blockState);
		
		microchip = new Microchip();
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
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		// TODO tick logic and mark dirty if needed
		
		if(microchip.isDirty())
		{
			microchip.markClean();
			this.setChanged();
		}
	}
	
	@Override
	public void setChanged()
	{
		super.setChanged();
		
		// TODO update listening clients
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
							LBR.LOGGER.error("Failed to load microchip data at {} in {}: {}", worldPosition.toShortString(), level.dimension().location(), error.message()));
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
