package net.swedz.little_big_redstone.gui.microchip.widget;

import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.logic.DyeComponentResult;
import net.swedz.little_big_redstone.gui.microchip.panel.MicrochipRenderBoardPanel;
import net.swedz.little_big_redstone.microchip.object.MicrochipObject;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.logic.LogicSelectedPort;
import net.swedz.little_big_redstone.microchip.object.note.StickyNoteEntry;
import net.swedz.little_big_redstone.microchip.wire.Wire;

import java.util.Collections;
import java.util.List;

public final class MicrochipWidgetContext
{
	public static boolean isWire(ItemStack stack)
	{
		return stack.is(LBRItems.REDSTONE_BIT.asItem());
	}
	
	public static boolean canInteractWire(ItemStack stack)
	{
		return stack.isEmpty() || isWire(stack);
	}
	
	public static boolean canInteractDyeableObject(ItemStack stack)
	{
		return stack.isEmpty() || DyeComponentResult.is(stack);
	}
	
	public static MicrochipWidgetContext test(MicrochipWidget widget, MicrochipRenderBoardPanel panel, int mouseX, int mouseY, int boardMouseX, int boardMouseY, MicrochipWidgetContext previous)
	{
		if(!widget.isMouseOver(mouseX, mouseY))
		{
			return new MicrochipWidgetContext(widget, boardMouseX, boardMouseY);
		}
		
		var microchip = widget.microchip();
		var carriedStack = widget.menu().getCarried();
		
		List<Wire> topLayerWires = Lists.newArrayList();
		
		int padding = isWire(carriedStack) ? 2 : 0;
		MicrochipObject object = microchip.findAt(boardMouseX, boardMouseY, padding);
		
		StickyNoteEntry note = canInteractDyeableObject(carriedStack) && object instanceof StickyNoteEntry o ? o : null;
		
		// Find the hovered logic component, if any
		LogicEntry logic = (canInteractDyeableObject(carriedStack) || isWire(carriedStack)) && object instanceof LogicEntry o ? o : null;
		LogicSelectedPort port = null;
		Wire wire = null;
		
		// Find the nearest port for the hovered logic component
		if(isWire(carriedStack) && logic != null)
		{
			port = microchip.components().findNearestPortAt(logic, boardMouseX, boardMouseY, widget.hasSelectedPort());
		}
		
		// Try to find the hovered wire, if any
		if(canInteractWire(carriedStack) && object == null && !widget.hasSelectedPort())
		{
			wire = previous.wire();
			if(wire == null || !panel.wires().isHovering(wire, boardMouseX, boardMouseY))
			{
				wire = panel.wires().findHoveredWire(boardMouseX, boardMouseY);
				if(wire != null)
				{
					logic = microchip.components().get(wire.output().slot());
				}
			}
			else if(wire != null && panel.wires().isHovering(wire, boardMouseX, boardMouseY))
			{
				logic = microchip.components().get(wire.output().slot());
			}
		}
		
		int wireSignal = 0;
		if(wire != null && logic != null)
		{
			wireSignal = logic.component().output(wire.output().index());
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
		
		return new MicrochipWidgetContext(widget, boardMouseX, boardMouseY, carriedStack, object, note, logic, port, port == null || port.input(), wire, wireSignal, topLayerWires);
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
	private final int               wireSignal;
	
	private final List<Wire> topLayerWires;
	
	private MicrochipWidgetContext(MicrochipWidget widget,
								   int boardMouseX, int boardMouseY,
								   ItemStack carriedStack,
								   MicrochipObject object, StickyNoteEntry note, LogicEntry logic,
								   LogicSelectedPort port, boolean portInput,
								   Wire wire, int wireSignal, List<Wire> topLayerWires)
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
		this.wireSignal = wireSignal;
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
		this.wireSignal = 0;
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
	
	public int wireSignal()
	{
		return wireSignal;
	}
	
	public List<Wire> topLayerWires()
	{
		return topLayerWires;
	}
	
	public boolean shouldRenderTooltip()
	{
		return (this.hasObject() || this.hasWire()) && !this.hasPort() && carriedStack.isEmpty();
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
