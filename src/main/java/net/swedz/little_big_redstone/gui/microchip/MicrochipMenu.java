package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import net.swedz.little_big_redstone.LBR;
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
import net.swedz.tesseract.neoforge.helper.TransferHelper;
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
	
	private final MicrochipViewPosition viewPosition;
	
	private final MicrochipLogicArrayItemHandler logicArrayItemHandler;
	
	private int        carriedComponentSlot = -1;
	private List<Wire> carriedWires;
	
	public MicrochipMenu(
			int containerId,
			Inventory playerInventory,
			BlockPos blockPos,
			Function<Player, Boolean> validChecker,
			Microchip microchip,
			DyeColor color,
			MicrochipViewPosition viewPosition
	)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.blockPos = blockPos;
		this.validChecker = validChecker;
		this.microchip = microchip;
		this.color = color;
		
		this.viewPosition = viewPosition;
		
		logicArrayItemHandler = new MicrochipLogicArrayItemHandler(this, playerInventory.player);
		this.setupInventory(playerInventory);
		logicArrayItemHandler.pickLogicArrayFromInventory();
	}
	
	public MicrochipMenu(
			int containerId,
			Inventory playerInventory,
			RegistryFriendlyByteBuf buf
	)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.blockPos = buf.readBlockPos();
		this.validChecker = null;
		this.microchip = Microchip.STREAM_CODEC.decode(buf);
		this.color = DyeColor.STREAM_CODEC.decode(buf);
		this.viewPosition = MicrochipViewPosition.STREAM_CODEC.decode(buf);
		
		logicArrayItemHandler = new MicrochipLogicArrayItemHandler(this, playerInventory.player);
		this.setupInventory(playerInventory);
		logicArrayItemHandler.pickLogicArrayFromInventory();
	}
	
	private void setupInventory(Inventory playerInventory)
	{
		LogicArrayMenu.setupLogicArrayInventory(logicArrayItemHandler, this::addSlot, logicArrayItemHandler::shouldDisplay, -75, 10, LogicArrayItem.COLUMNS, LogicArrayItem.ROWS);
		
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
	
	public MicrochipViewPosition viewPosition()
	{
		return viewPosition;
	}
	
	public MicrochipLogicArrayItemHandler getLogicArrayItemHandler()
	{
		return logicArrayItemHandler;
	}
	
	public IItemHandler getDestinationInventoryItemHandler(Player player)
	{
		var playerInventory = new PlayerMainInvWrapper(player.getInventory());
		return logicArrayItemHandler.isCreativeMode() ?
				playerInventory :
				new CombinedInvWrapper(logicArrayItemHandler, playerInventory);
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
	
	@Override
	public void clicked(int slotId, int button, ClickType clickType, Player player)
	{
		this.dropCarriedWires(slotId, button, clickType, player);
		
		if(this.pickLogicArray(slotId, button, clickType, player))
		{
			return;
		}
		
		super.clicked(slotId, button, clickType, player);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotId)
	{
		ItemStack originalStack = ItemStack.EMPTY;
		Slot slot = slots.get(slotId);
		if(slot != null && slot.hasItem())
		{
			var stack = slot.getItem();
			
			// Allow players to grab a full stack of items from the creative array menu
			if(slot instanceof LogicArraySlot logicArraySlot && logicArrayItemHandler.isCreativeMode())
			{
				if(this.getCarried().isEmpty() || slot.mayPlace(this.getCarried()))
				{
					this.setCarried(stack.copyWithCount(64));
				}
				return ItemStack.EMPTY;
			}
			boolean clickedArrayInventory = slotId < LogicArrayItem.MAX_SLOTS;
			// Prevent shift clicking items into the creative array menu to delete them
			if(!clickedArrayInventory && logicArrayItemHandler.isCreativeMode())
			{
				return ItemStack.EMPTY;
			}
			
			originalStack = stack.copy();
			
			var target = clickedArrayInventory ? new PlayerMainInvWrapper(player.getInventory()) : logicArrayItemHandler;
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
				slot.setByPlayer(stack);
			}
		}
		return originalStack;
	}
	
	@Override
	public boolean canDragTo(Slot slot)
	{
		return !(slot instanceof LogicArraySlot) || !logicArrayItemHandler.isCreativeMode();
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return validChecker == null || validChecker.apply(player);
	}
}
