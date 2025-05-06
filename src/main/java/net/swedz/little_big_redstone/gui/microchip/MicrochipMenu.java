package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRMenus;
import net.swedz.little_big_redstone.gui.BaseContainerMenu;
import net.swedz.little_big_redstone.gui.logicarray.LogicArrayMenu;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArrayPlayerSlot;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.little_big_redstone.microchip.wire.WirePort;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class MicrochipMenu extends BaseContainerMenu
{
	private final BlockPos                  blockPos;
	private final Function<Player, Boolean> validChecker;
	private final Microchip                 microchip;
	private final DyeColor                  color;
	
	private int        carriedComponentSlot = -1;
	private List<Wire> carriedWires;
	
	private final Container logicArrayContainer;
	
	public MicrochipMenu(int containerId, Inventory playerInventory,
						 BlockPos blockPos, Function<Player, Boolean> validChecker, Microchip microchip, DyeColor color)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.blockPos = blockPos;
		this.validChecker = validChecker;
		this.microchip = microchip;
		this.color = color;
		
		this.logicArrayContainer = this.createLogicArrayInventory();
		
		this.setupInventory(playerInventory);
	}
	
	public MicrochipMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf)
	{
		super(LBRMenus.MICROCHIP.get(), containerId);
		
		this.blockPos = buf.readBlockPos();
		this.validChecker = null;
		this.microchip = Microchip.STREAM_CODEC.decode(buf);
		this.color = DyeColor.STREAM_CODEC.decode(buf);
		
		this.logicArrayContainer = this.createLogicArrayInventory();
		
		this.setupInventory(playerInventory);
	}
	
	private Container createLogicArrayInventory()
	{
		// TODO Logic Array: pull the inventory from the item...
		return new SimpleContainer(4 * 7);
	}
	
	private void setupInventory(Inventory playerInventory)
	{
		LogicArrayMenu.setupLogicArrayInventory(logicArrayContainer, this::addSlot, -75, 10, LogicArrayMenu.COLUMNS, LogicArrayMenu.ROWS);
		
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
	
	@Override
	public void clicked(int slotId, int button, ClickType clickType, Player player)
	{
		super.clicked(slotId, button, clickType, player);
		
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
