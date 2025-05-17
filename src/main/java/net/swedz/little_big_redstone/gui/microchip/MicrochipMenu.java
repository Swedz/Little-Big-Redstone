package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRCreativeTabs;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRMenus;
import net.swedz.little_big_redstone.gui.logicarray.LogicArrayMenu;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArrayPlayerSlot;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArraySlot;
import net.swedz.little_big_redstone.gui.microchip.logicarray.MicrochipLogicArrayItemHandler;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItem;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.little_big_redstone.microchip.wire.WirePort;
import net.swedz.tesseract.neoforge.helper.gui.PlayerInventoryContainerMenu;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class MicrochipMenu extends PlayerInventoryContainerMenu
{
	private final BlockPos                  blockPos;
	private final Function<Player, Boolean> validChecker;
	private final Microchip                 microchip;
	private final DyeColor                  color;
	
	private final MicrochipLogicArrayItemHandler logicArrayItemHandler;
	
	private int        carriedComponentSlot = -1;
	private List<Wire> carriedWires;
	
	public MicrochipMenu(int containerId, Inventory playerInventory,
						 BlockPos blockPos, Function<Player, Boolean> validChecker, Microchip microchip, DyeColor color)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.blockPos = blockPos;
		this.validChecker = validChecker;
		this.microchip = microchip;
		this.color = color;
		
		logicArrayItemHandler = new MicrochipLogicArrayItemHandler(this, playerInventory.player);
		this.setupInventory(playerInventory);
		logicArrayItemHandler.pickLogicArrayFromInventory();
	}
	
	public MicrochipMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.blockPos = buf.readBlockPos();
		this.validChecker = null;
		this.microchip = Microchip.STREAM_CODEC.decode(buf);
		this.color = DyeColor.STREAM_CODEC.decode(buf);
		
		logicArrayItemHandler = new MicrochipLogicArrayItemHandler(this, playerInventory.player);
		this.setupInventory(playerInventory);
		logicArrayItemHandler.pickLogicArrayFromInventory();
	}
	
	private void setupInventory(Inventory playerInventory)
	{
		LogicArrayMenu.setupLogicArrayInventory(logicArrayItemHandler, this::addSlot, logicArrayItemHandler::shouldDisplay, logicArrayItemHandler::isCreativeMode, -75, 10, LogicArrayItem.COLUMNS, LogicArrayItem.ROWS);
		
		this.setupPlayerInventory(playerInventory, 48, 145, LogicArrayPlayerSlot::new);
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
	
	public MicrochipLogicArrayItemHandler getLogicArrayItemHandler()
	{
		return logicArrayItemHandler;
	}
	
	public int getCarriedComponentSlot()
	{
		return carriedComponentSlot;
	}
	
	public void setCarriedWires(int carriedComponentSlot, List<Wire> wires)
	{
		this.carriedComponentSlot = carriedComponentSlot;
		this.carriedWires = wires.isEmpty() ? null : Collections.unmodifiableList(wires);
	}
	
	public List<Wire> getCarriedWires()
	{
		return carriedWires;
	}
	
	public void placeCarriedWires(int newCarriedComponentSlot)
	{
		if(carriedComponentSlot != -1 && carriedWires != null)
		{
			for(var wire : carriedWires)
			{
				WirePort newOutput = wire.output();
				WirePort newInput = wire.input();
				if(wire.output().slot() == carriedComponentSlot)
				{
					newOutput = new WirePort(newCarriedComponentSlot, wire.output().index());
				}
				else if(wire.input().slot() == carriedComponentSlot)
				{
					newInput = new WirePort(newCarriedComponentSlot, wire.input().index());
				}
				else
				{
					LBR.LOGGER.warn("MicrochipMenu failed to convert wire {}", wire);
					continue;
				}
				Wire newWire = new Wire(newOutput, newInput);
				if(!microchip.wires().add(newWire))
				{
					LBR.LOGGER.warn("MicrochipMenu failed to place wire {}", newWire);
				}
			}
			carriedComponentSlot = -1;
			carriedWires = null;
		}
	}
	
	private void dropCarriedWires(int slotId, int button, ClickType clickType, Player player)
	{
		if(carriedComponentSlot != -1 && carriedWires != null)
		{
			if(slotId >= 0 || slotId == -999)
			{
				int wiresPopped = carriedWires.size();
				
				if(!player.level().isClientSide() && !player.hasInfiniteMaterials())
				{
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(LBRItems.REDSTONE_BIT, wiresPopped));
				}
				
				carriedComponentSlot = -1;
				carriedWires = null;
			}
		}
	}
	
	private boolean pickLogicArray(int slotId, int button, ClickType clickType, Player player)
	{
		if(slotId >= 0)
		{
			var slot = slots.get(slotId);
			if(slot instanceof LogicArrayPlayerSlot playerSlot && playerSlot.containsLogicArray())
			{
				if(slotId == logicArrayItemHandler.getSelectedSlot())
				{
					logicArrayItemHandler.deselectPickedLogicArray();
				}
				else
				{
					logicArrayItemHandler.setPickedLogicArray(slotId);
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean creativeClickLogicArraySlot(int slotId, int button, ClickType clickType, Player player)
	{
		var carried = this.getCarried();
		
		if(slotId >= 0 && logicArrayItemHandler.isCreativeMode() && slots.get(slotId) instanceof LogicArraySlot slot &&
		   clickType != ClickType.QUICK_CRAFT)
		{
			boolean shift = clickType == ClickType.QUICK_MOVE;
			var items = LBRCreativeTabs.getLogicArrayItems();
			if(slotId < items.size())
			{
				var stack = items.get(slotId);
				if(!carried.isEmpty() && !stack.isEmpty() && ItemStack.isSameItemSameComponents(carried, stack))
				{
					if(button == 0)
					{
						if(shift)
						{
							carried.setCount(stack.getMaxStackSize());
						}
						else if(carried.getCount() < carried.getMaxStackSize())
						{
							carried.grow(1);
						}
					}
					else
					{
						carried.shrink(1);
					}
				}
				else if(carried.isEmpty() && !stack.isEmpty())
				{
					this.setCarried(stack.copyWithCount(shift ? stack.getMaxStackSize() : 1));
				}
				else if(button == 0)
				{
					this.setCarried(ItemStack.EMPTY);
				}
				else if(!carried.isEmpty())
				{
					carried.shrink(1);
				}
			}
			else
			{
				this.setCarried(ItemStack.EMPTY);
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public void clicked(int slotId, int button, ClickType clickType, Player player)
	{
		this.dropCarriedWires(slotId, button, clickType, player);
		
		if(this.pickLogicArray(slotId, button, clickType, player))
		{
			return;
		}
		else if(this.creativeClickLogicArraySlot(slotId, button, clickType, player))
		{
			return;
		}
		
		super.clicked(slotId, button, clickType, player);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotId)
	{
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = slots.get(slotId);
		if(slot != null && slot.hasItem())
		{
			stack = slot.getItem().copy();
			if(slotId < LogicArrayItem.MAX_SLOTS)
			{
				if(!this.moveItemStackTo(stack, LogicArrayItem.MAX_SLOTS, slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if(!this.moveItemStackTo(stack, 0, LogicArrayItem.MAX_SLOTS, false))
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
		return stack;
	}
	
	/**
	 * <p>So, because of how vanilla's
	 * {@link net.minecraft.world.inventory.AbstractContainerMenu#moveItemStackTo(ItemStack, int, int, boolean)}
	 * implementation works, it causes items to get voided (and possibly duped?) when moving stacks into a
	 * {@link SlotItemHandler} slot. The vanilla method would mutate the stacks returned by the handler (which is VERY
	 * bad for us) and then just assume the slot is okay with this and call {@link Slot#setChanged()}. Instead of this,
	 * because we are using an item handler, we must not mutate the returned stacks and then apply changes using
	 * {@link Slot#set(ItemStack)}.</p>
	 *
	 * <p>All changes made from the vanilla implementation are noted below in comments.</p>
	 */
	@Override
	public boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
	{
		boolean itemMoved = false;
		int index = startIndex;
		if(reverseDirection)
		{
			index = endIndex - 1;
		}
		
		Slot slot;
		ItemStack slotStack;
		if(stack.isStackable())
		{
			while(!stack.isEmpty())
			{
				if(reverseDirection)
				{
					if(index < startIndex)
					{
						break;
					}
				}
				else if(index >= endIndex)
				{
					break;
				}
				slot = slots.get(index);
				// Change made here - we use a copy to avoid modifying the actual stack
				slotStack = slot.getItem().copy();
				if(!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(stack, slotStack))
				{
					int stackCount = slotStack.getCount() + stack.getCount();
					int slotMaxSize = slot.getMaxStackSize(slotStack);
					if(stackCount <= slotMaxSize)
					{
						stack.setCount(0);
						slotStack.setCount(stackCount);
						// Change made here - this used to just call slot.setChanged() instead of slot.set(...)
						slot.set(slotStack);
						itemMoved = true;
					}
					else if(slotStack.getCount() < slotMaxSize)
					{
						stack.shrink(slotMaxSize - slotStack.getCount());
						slotStack.setCount(slotMaxSize);
						// Change made here - this used to just call slot.setChanged() instead of slot.set(...)
						slot.set(slotStack);
						itemMoved = true;
					}
				}
				if(reverseDirection)
				{
					--index;
				}
				else
				{
					++index;
				}
			}
		}
		
		if(!stack.isEmpty())
		{
			if(reverseDirection)
			{
				index = endIndex - 1;
			}
			else
			{
				index = startIndex;
			}
			while(true)
			{
				if(reverseDirection)
				{
					if(index < startIndex)
					{
						break;
					}
				}
				else if(index >= endIndex)
				{
					break;
				}
				slot = slots.get(index);
				slotStack = slot.getItem();
				if(slotStack.isEmpty() && slot.mayPlace(stack))
				{
					int slotMaxSize = slot.getMaxStackSize(stack);
					slot.setByPlayer(stack.split(Math.min(stack.getCount(), slotMaxSize)));
					// Change made here - this used to call slot.setChanged() which is not necessary because
					// slot.setbyPlayer(...) already calls slot.setChanged()
					itemMoved = true;
					break;
				}
				if(reverseDirection)
				{
					--index;
				}
				else
				{
					++index;
				}
			}
		}
		
		return itemMoved;
	}
	
	@Override
	public boolean canDragTo(Slot slot)
	{
		return !(slot instanceof LogicArraySlot logicArraySlot) || !logicArraySlot.isCreative();
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return validChecker == null || validChecker.apply(player);
	}
}
