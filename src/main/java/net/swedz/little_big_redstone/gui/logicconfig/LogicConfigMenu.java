package net.swedz.little_big_redstone.gui.logicconfig;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRMenus;
import net.swedz.little_big_redstone.gui.microchip.MicrochipViewPosition;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.tesseract.neoforge.helper.gui.PlayerInventoryContainerMenu;

import java.util.function.Supplier;

public final class LogicConfigMenu extends PlayerInventoryContainerMenu
{
	private final BlockPos          blockPos;
	private final Supplier<Boolean> validChecker;
	
	private final LogicEntry logicEntry;
	
	private final MicrochipViewPosition returnViewPosition;
	
	public LogicConfigMenu(int containerId, Inventory playerInventory,
						   BlockPos blockPos, Supplier<Boolean> validChecker,
						   LogicEntry logicEntry,
						   MicrochipViewPosition returnViewPosition)
	{
		super(LBRMenus.LOGIC_CONFIG.get(), containerId);
		
		this.blockPos = blockPos;
		this.validChecker = validChecker;
		this.logicEntry = logicEntry;
		this.returnViewPosition = returnViewPosition;
		
		this.setupPlayerInventory(playerInventory, 8, 150);
	}
	
	public LogicConfigMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf)
	{
		super(LBRMenus.LOGIC_CONFIG.get(), containerId);
		
		this.blockPos = buf.readBlockPos();
		this.validChecker = null;
		this.logicEntry = LogicEntry.STREAM_CODEC.decode(buf);
		this.returnViewPosition = MicrochipViewPosition.STREAM_CODEC.decode(buf);
		
		this.setupPlayerInventory(playerInventory, 8, 150);
	}
	
	public BlockPos blockPos()
	{
		return blockPos;
	}
	
	public LogicEntry logicEntry()
	{
		return logicEntry;
	}
	
	public MicrochipViewPosition returnViewPosition()
	{
		return returnViewPosition;
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
