package net.swedz.little_big_redstone.gui.microchip;

import com.google.common.collect.Lists;
import net.swedz.little_big_redstone.gui.microchip.wire.WireRendering;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.LogicSelectedPort;
import net.swedz.little_big_redstone.microchip.wire.Wire;

import java.util.Collections;
import java.util.List;

public final class MicrochipWidgetHovering
{
	public static final MicrochipWidgetHovering NOTHING = new MicrochipWidgetHovering(null, null, true, null, List.of());
	
	public static MicrochipWidgetHovering test(MicrochipWidget widget, WireRendering wireRendering, int mouseX, int mouseY, MicrochipWidgetHovering previous)
	{
		if(!widget.isMouseOver(mouseX, mouseY))
		{
			return NOTHING;
		}
		
		var microchip = widget.microchip();
		
		int boardMouseX = microchip.size().boardX(widget.toLocalX(mouseX));
		int boardMouseY = microchip.size().boardY(widget.toLocalY(mouseY));
		
		List<Wire> topLayerWires = Lists.newArrayList();
		
		// Find the hovered logic component, if any
		LogicEntry logic = microchip.components().findAt(boardMouseX, boardMouseY);
		LogicSelectedPort port = null;
		boolean portInput = true;
		Wire wire = null;
		
		// If no logic component was found, try to find the hovered port, if any
		if(logic == null)
		{
			port = microchip.components().findPortAt(boardMouseX, boardMouseY, true);
			if(port == null)
			{
				port = microchip.components().findPortAt(boardMouseX, boardMouseY, false);
				if(port != null)
				{
					portInput = false;
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
		}
		
		// If no port was found and no port is selected, try to find the hovered wire, if any
		if(logic == null && !widget.hasSelectedPort())
		{
			wire = previous.wire();
			if(wire == null || !wireRendering.isHovering(wire, boardMouseX, boardMouseY))
			{
				wire = wireRendering.findHoveredWire(boardMouseX, boardMouseY);
				if(wire != null)
				{
					logic = microchip.components().get(wire.output().slot());
				}
			}
			else if(wire != null && wireRendering.isHovering(wire, boardMouseX, boardMouseY))
			{
				logic = microchip.components().get(wire.output().slot());
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
		
		return new MicrochipWidgetHovering(logic, port, portInput, wire, topLayerWires);
	}
	
	private final LogicEntry        logic;
	private final LogicSelectedPort port;
	private final boolean           portInput;
	private final Wire              wire;
	
	private final List<Wire> topLayerWires;
	
	private MicrochipWidgetHovering(LogicEntry logic, LogicSelectedPort port, boolean portInput, Wire wire, List<Wire> topLayerWires)
	{
		this.logic = logic;
		this.port = port;
		this.portInput = portInput;
		this.wire = wire;
		this.topLayerWires = Collections.unmodifiableList(topLayerWires);
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
	
	public boolean shouldInteractLogic()
	{
		return this.hasLogic() && !this.hasPort() && !this.hasWire();
	}
	
	public boolean shouldRenderTooltip()
	{
		return this.shouldInteractLogic();
	}
	
	public boolean shouldInteractPort()
	{
		return this.hasLogic() && this.hasPort();
	}
	
	public boolean shouldInteractWire()
	{
		return !this.hasPort() && this.hasWire();
	}
	
	public boolean shouldInteractBoard()
	{
		return !this.hasLogic() && !this.hasPort() && !this.hasWire();
	}
}
