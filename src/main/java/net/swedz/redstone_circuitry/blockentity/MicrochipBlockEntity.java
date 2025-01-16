package net.swedz.redstone_circuitry.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.redstone_circuitry.RCBlocks;
import net.swedz.redstone_circuitry.gui.microchip.MicrochipMenu;

public final class MicrochipBlockEntity extends BlockEntity implements MenuProvider
{
	public MicrochipBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(RCBlocks.MICROCHIP_ENTITY.get(), pos, blockState);
	}
	
	@Override
	public Component getDisplayName()
	{
		return Component.translatable(RCBlocks.MICROCHIP.identifier().location().toLanguageKey("block"));
	}
	
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
	{
		return new MicrochipMenu(containerId, inventory);
	}
}
