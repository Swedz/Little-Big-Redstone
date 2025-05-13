package net.swedz.little_big_redstone.gui.logicarray;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;
import net.swedz.little_big_redstone.LBRMenus;
import net.swedz.little_big_redstone.gui.BaseContainerMenu;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArrayPlayerSlot;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArraySlot;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItem;
import net.swedz.tesseract.neoforge.helper.TransferHelper;

import java.util.function.Supplier;

public final class LogicArrayMenu extends BaseContainerMenu
{
	private final IItemHandler itemHandler;
	private final int          logicArraySlot;
	
	public LogicArrayMenu(int containerId, Inventory playerInventory, IItemHandler itemHandler, int logicArraySlot)
	{
		super(LBRMenus.LOGIC_ARRAY.get(), containerId);
		
		this.itemHandler = itemHandler;
		this.logicArraySlot = logicArraySlot;
		
		setupLogicArrayInventory(itemHandler, this::addSlot, 26, 18, LogicArrayItem.ROWS, LogicArrayItem.COLUMNS);
		
		this.setupPlayerInventory(playerInventory, 8, 104, (container, slot, x, y) -> new LogicArrayPlayerSlot(container, slot, x, y, () -> slot == logicArraySlot));
	}
	
	public LogicArrayMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf)
	{
		this(containerId, playerInventory, new ItemStackHandler(LogicArrayItem.MAX_SLOTS), buf.readVarInt());
	}
	
	public static void setupLogicArrayInventory(IItemHandler itemHandler, SlotAdder slotAdder, Supplier<Boolean> isActive, Supplier<Boolean> isCreative, int startX, int startY, int rows, int columns)
	{
		for(int row = 0; row < rows; row++)
		{
			for(int column = 0; column < columns; column++)
			{
				slotAdder.addSlot(new LogicArraySlot(itemHandler, column + row * columns, startX + column * 18, row * 18 + startY, isActive, isCreative));
			}
		}
	}
	
	public static void setupLogicArrayInventory(IItemHandler itemHandler, SlotAdder slotAdder, int startX, int startY, int rows, int columns)
	{
		setupLogicArrayInventory(itemHandler, slotAdder, null, null, startX, startY, rows, columns);
	}
	
	public interface SlotAdder
	{
		void addSlot(Slot slot);
	}
	
	public int getLogicArraySlot()
	{
		return logicArraySlot;
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		ItemStack originalStack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if(slot != null && slot.hasItem())
		{
			var stack = slot.getItem();
			originalStack = stack.copy();
			
			var target = index < LogicArrayItem.MAX_SLOTS ? new PlayerInvWrapper(player.getInventory()) : itemHandler;
			int inserted = TransferHelper.insert(target, stack);
			if(inserted > 0)
			{
				stack.shrink(inserted);
			}
			else
			{
				return ItemStack.EMPTY;
			}
			
			if(stack.isEmpty())
			{
				slot.setByPlayer(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}
		}
		return originalStack;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return true;
	}
}
