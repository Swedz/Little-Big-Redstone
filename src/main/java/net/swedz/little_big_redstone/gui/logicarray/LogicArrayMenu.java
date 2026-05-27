package net.swedz.little_big_redstone.gui.logicarray;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.swedz.little_big_redstone.LBRMenus;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArraySlot;
import net.swedz.little_big_redstone.gui.slot.MaybeLockedPlayerSlot;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItem;
import net.swedz.tesseract.neoforge.helper.gui.PlayerInventoryContainerMenu;

import java.util.function.Supplier;

public final class LogicArrayMenu extends PlayerInventoryContainerMenu
{
	private final ResourceHandler<ItemResource> itemHandler;
	private final int                           logicArraySlot;
	
	public LogicArrayMenu(int containerId, Inventory playerInventory, ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> indexModifier, int logicArraySlot)
	{
		super(LBRMenus.LOGIC_ARRAY.get(), containerId);
		
		this.itemHandler = itemHandler;
		this.logicArraySlot = logicArraySlot;
		
		setupLogicArrayInventory(itemHandler, indexModifier, this::addSlot, 26, 18, LogicArrayItem.ROWS, LogicArrayItem.COLUMNS);
		
		this.setupPlayerInventory(
				playerInventory,
				8,
				104,
				(container, slot, x, y) -> new MaybeLockedPlayerSlot(
						container,
						slot,
						x,
						y,
						(stack) ->
								slot == logicArraySlot ||
								stack.is(LBRTags.Items.LOGIC_ARRAYS)
				)
		);
	}
	
	public static LogicArrayMenu read(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf)
	{
		var handler = new ItemStacksResourceHandler(LogicArrayItem.MAX_SLOTS);
		return new LogicArrayMenu(containerId, playerInventory, handler, handler::set, buf.readVarInt());
	}
	
	public static void setupLogicArrayInventory(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> indexModifier, SlotAdder slotAdder, Supplier<Boolean> isActive, int startX, int startY, int rows, int columns)
	{
		for(int row = 0; row < rows; row++)
		{
			for(int column = 0; column < columns; column++)
			{
				slotAdder.addSlot(new LogicArraySlot(itemHandler, indexModifier, column + row * columns, startX + column * 18, row * 18 + startY, isActive));
			}
		}
	}
	
	public static void setupLogicArrayInventory(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> indexModifier, SlotAdder slotAdder, int startX, int startY, int rows, int columns)
	{
		setupLogicArrayInventory(itemHandler, indexModifier, slotAdder, null, startX, startY, rows, columns);
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
			
			var target = index < LogicArrayItem.MAX_SLOTS ? PlayerInventoryWrapper.of(player) : itemHandler;
			int inserted = ResourceHandlerUtil.insertStacking(target, ItemResource.of(stack), stack.getCount(), null);
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
