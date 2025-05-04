package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRMenus;
import net.swedz.little_big_redstone.gui.BaseContainerMenu;
import net.swedz.little_big_redstone.microchip.Microchip;

import java.util.function.Function;

public final class MicrochipMenu extends BaseContainerMenu
{
	private final BlockPos                  blockPos;
	private final Function<Player, Boolean> validChecker;
	private final Microchip                 microchip;
	private final DyeColor                  color;
	
	public MicrochipMenu(int containerId, Inventory playerInventory,
						 BlockPos blockPos, Function<Player, Boolean> validChecker, Microchip microchip, DyeColor color)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.blockPos = blockPos;
		this.validChecker = validChecker;
		this.microchip = microchip;
		this.color = color;
		
		this.setupPlayerInventory(playerInventory, 48, 145);
	}
	
	public MicrochipMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.blockPos = buf.readBlockPos();
		this.validChecker = null;
		this.microchip = Microchip.STREAM_CODEC.decode(buf);
		this.color = DyeColor.STREAM_CODEC.decode(buf);
		
		this.setupPlayerInventory(playerInventory, 48, 145);
	}
	
	public BlockPos blockPos()
	{
		return blockPos;
	}
	
	public Microchip microchip()
	{
		return microchip;
	}
	
	public DyeColor color()
	{
		return color;
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotId)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
	{
		return super.moveItemStackTo(stack, startIndex, endIndex, reverseDirection);
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return validChecker == null || validChecker.apply(player);
	}
}
