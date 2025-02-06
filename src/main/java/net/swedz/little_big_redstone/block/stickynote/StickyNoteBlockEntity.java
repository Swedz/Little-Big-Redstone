package net.swedz.little_big_redstone.block.stickynote;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.LBRText;

public final class StickyNoteBlockEntity extends BlockEntity implements MenuProvider
{
	private final DyeColor color;
	
	public StickyNoteBlockEntity(BlockPos pos, BlockState state, DyeColor color)
	{
		super(LBRBlocks.STICKY_NOTE_ENTITY.get(), pos, state);
		
		this.color = color;
	}
	
	public DyeColor color()
	{
		return color;
	}
	
	@Override
	public Component getDisplayName()
	{
		return LBRText.STICKY_NOTE.text();
	}
	
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
	{
		// TODO
		return null;
	}
	
	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		
		// TODO
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		
		// TODO
	}
}
