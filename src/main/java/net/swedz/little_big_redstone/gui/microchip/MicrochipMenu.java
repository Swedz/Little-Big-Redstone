package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRMenus;
import net.swedz.little_big_redstone.microchip.Microchip;

import java.util.function.Supplier;

public final class MicrochipMenu extends AbstractContainerMenu
{
	private final BlockPos          blockPos;
	private final Supplier<Boolean> validChecker;
	private final Microchip         microchip;
	
	public MicrochipMenu(int containerId, Inventory playerInventory,
						 BlockPos blockPos, Supplier<Boolean> validChecker, Microchip microchip)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.blockPos = blockPos;
		this.validChecker = validChecker;
		this.microchip = microchip;
		
		this.setupPlayerInventory(playerInventory, 256 - 90 - 12 - 12);
	}
	
	public MicrochipMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.blockPos = buf.readBlockPos();
		this.validChecker = null;
		this.microchip = Microchip.STREAM_CODEC.decode(buf);
		
		this.setupPlayerInventory(playerInventory, 256 - 90 - 12 - 12);
	}
	
	private void setupPlayerInventory(Inventory playerInventory, int startY)
	{
		for(int row = 0; row < 3; ++row)
		{
			for(int column = 0; column < 9; ++column)
			{
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 48 + column * 18, 8 + row * 18 + startY));
			}
		}
		for(int column = 0; column < 9; ++column)
		{
			this.addSlot(new Slot(playerInventory, column, 48 + column * 18, 66 + startY));
		}
	}
	
	public BlockPos blockPos()
	{
		return blockPos;
	}
	
	public Microchip microchip()
	{
		return microchip;
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotId)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return (validChecker == null || validChecker.get()) &&
			   player.blockPosition().closerThan(blockPos, 10);
	}
}
