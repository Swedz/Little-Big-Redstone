package net.swedz.little_big_redstone.gui.microchip.widget;

import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.logic.DyeComponentResult;
import net.swedz.little_big_redstone.microchip.object.MicrochipObject;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.logic.LogicSelectedPort;
import net.swedz.little_big_redstone.microchip.object.note.StickyNoteEntry;
import net.swedz.little_big_redstone.microchip.wire.Wire;

import java.util.Collections;
import java.util.List;

public final class MicrochipWidgetContext
{
	public static boolean canInteractWire(ItemStack stack)
	{
		return stack.isEmpty() || stack.is(LBRItems.REDSTONE_BIT.asItem());
	}
	
	public static boolean canInteractDyeableObject(ItemStack stack)
	{
		return stack.isEmpty() || DyeComponentResult.is(stack);
	}
	
	public static MicrochipWidgetContext test(MicrochipWidget widget, MicrochipWidgetWires widgetWires, int mouseX, int mouseY, int boardMouseX, int boardMouseY, MicrochipWidgetContext previous)
	{
		if(!widget.isMouseOver(mouseX, mouseY))
		{
			return new MicrochipWidgetContext(widget, boardMouseX, boardMouseY);
		}
		
		var microchip = widget.microchip();
		var carriedStack = widget.menu().getCarried();
		
		List<Wire> topLayerWires = Lists.newArrayList();
		
		MicrochipObject object = microchip.findAt(boardMouseX, boardMouseY);
		
		StickyNoteEntry note = canInteractDyeableObject(carriedStack) && object instanceof StickyNoteEntry o ? o : null;
		
		// Find the hovered logic component, if any
		LogicEntry logic = canInteractDyeableObject(carriedStack) && object instanceof LogicEntry o ? o : null;
		LogicSelectedPort port = null;
		boolean portInput = true;
		Wire wire = null;
		
		if(canInteractWire(carriedStack) && object == null)
		{
			// Try to find the hovered port, if any
			port = microchip.components().findPortAt(boardMouseX, boardMouseY, true);
			if(port == null)
			{
				port = microchip.components().findPortAt(boardMouseX, boardMouseY, false);
				if(port != null)
				{
					portInput = false;
					// Only allow selecting the wire in the output port if no item is held in the cursor
					if(carriedStack.isEmpty())
					{
						var wires = microchip.wires().getByOutputSlot(port);
						if(!wires.isEmpty())
						{
							wire = wires.getLast();
						}
					}
				}
			}
			else
			{
				wire = microchip.wires().getByInputSlot(port);
			}
			if(port != null)
			{
				logic = microchip.components().get(port.slot());
			}
			
			// If no port was found and no port is selected, try to find the hovered wire, if any
			if(logic == null && !widget.hasSelectedPort())
			{
				wire = previous.wire();
				if(wire == null || !widgetWires.isHovering(wire, boardMouseX, boardMouseY))
				{
					wire = widgetWires.findHoveredWire(boardMouseX, boardMouseY);
					if(wire != null)
					{
						logic = microchip.components().get(wire.output().slot());
					}
				}
				else if(wire != null && widgetWires.isHovering(wire, boardMouseX, boardMouseY))
				{
					logic = microchip.components().get(wire.output().slot());
				}
			}
		}
		
		// If something was found involving a logic component, set the component's output wires to be rendered last
		if(logic != null)
		{
			int outputSlot = logic.slot();
			if(wire != null)
			{
				outputSlot = wire.output().slot();
			}
			topLayerWires.addAll(microchip.wires().getByOutputSlot(outputSlot));
			if(wire != null)
			{
				topLayerWires.remove(wire);
				topLayerWires.add(wire);
			}
		}
		
		return new MicrochipWidgetContext(widget, boardMouseX, boardMouseY, carriedStack, object, note, logic, port, portInput, wire, topLayerWires);
	}
	
	private final boolean onBoard;
	
	private final MicrochipWidget widget;
	private final int             boardMouseX, boardMouseY;
	
	private final ItemStack carriedStack;
	
	private final MicrochipObject   object;
	private final StickyNoteEntry   note;
	private final LogicEntry        logic;
	private final LogicSelectedPort port;
	private final boolean           portInput;
	private final Wire              wire;
	
	private final List<Wire> topLayerWires;
	
	private MicrochipWidgetContext(MicrochipWidget widget,
								   int boardMouseX, int boardMouseY,
								   ItemStack carriedStack,
								   MicrochipObject object, StickyNoteEntry note, LogicEntry logic,
								   LogicSelectedPort port, boolean portInput,
								   Wire wire, List<Wire> topLayerWires)
	{
		this.onBoard = true;
		this.widget = widget;
		this.boardMouseX = boardMouseX;
		this.boardMouseY = boardMouseY;
		this.carriedStack = carriedStack;
		this.object = object;
		this.note = note;
		this.logic = logic;
		this.port = port;
		this.portInput = portInput;
		this.wire = wire;
		this.topLayerWires = Collections.unmodifiableList(topLayerWires);
	}
	
	public MicrochipWidgetContext(MicrochipWidget widget, int boardMouseX, int boardMouseY)
	{
		this.onBoard = false;
		this.widget = widget;
		this.boardMouseX = boardMouseX;
		this.boardMouseY = boardMouseY;
		this.carriedStack = ItemStack.EMPTY;
		this.object = null;
		this.note = null;
		this.logic = null;
		this.port = null;
		this.portInput = true;
		this.wire = null;
		this.topLayerWires = List.of();
	}
	
	public boolean isOnBoard()
	{
		return onBoard;
	}
	
	public MicrochipWidget widget()
	{
		return widget;
	}
	
	public int boardMouseX()
	{
		return boardMouseX;
	}
	
	public int boardMouseY()
	{
		return boardMouseY;
	}
	
	public ItemStack getCarriedStack()
	{
		return carriedStack;
	}
	
	public MicrochipObject object()
	{
		return object;
	}
	
	public boolean hasObject()
	{
		return object != null;
	}
	
	public StickyNoteEntry note()
	{
		return note;
	}
	
	public boolean hasNote()
	{
		return note != null;
	}
	
	public LogicEntry logic()
	{
		return logic;
	}
	
	public boolean hasLogic()
	{
		return logic != null;
	}
	
	public LogicSelectedPort port()
	{
		return port;
	}
	
	public boolean hasPort()
	{
		return port != null;
	}
	
	public boolean isPortInput()
	{
		return portInput;
	}
	
	public boolean isPortOutput()
	{
		return !portInput;
	}
	
	public boolean isPortEmpty()
	{
		return !this.hasWire();
	}
	
	public Wire wire()
	{
		return wire;
	}
	
	public boolean hasWire()
	{
		return wire != null;
	}
	
	public List<Wire> topLayerWires()
	{
		return topLayerWires;
	}
	
	public boolean shouldRenderTooltip()
	{
		return this.hasObject() && !this.hasPort() && !this.hasWire() && carriedStack.isEmpty();
	}
	
	public boolean shouldDyeObject()
	{
		return this.hasObject() && !this.hasPort() && !this.hasWire() && canInteractDyeableObject(carriedStack);
	}
	
	public boolean shouldInteractNote()
	{
		return this.hasNote() && !this.hasPort() && !this.hasWire() && canInteractDyeableObject(carriedStack);
	}
	
	public boolean shouldInteractLogic()
	{
		return this.hasLogic() && !this.hasPort() && !this.hasWire() && canInteractDyeableObject(carriedStack);
	}
	
	public boolean shouldInteractPort()
	{
		return this.hasLogic() && this.hasPort() && canInteractWire(carriedStack);
	}
	
	public boolean shouldInteractWire()
	{
		return this.hasWire() && canInteractWire(carriedStack);
	}
	
	public boolean shouldInteractBoard()
	{
		return !this.hasLogic() && !this.hasPort() && !this.hasWire();
	}
	
	public boolean shouldInsertWireToPort()
	{
		return this.shouldInteractPort() && this.isPortInput() && this.isPortEmpty();
	}
}
