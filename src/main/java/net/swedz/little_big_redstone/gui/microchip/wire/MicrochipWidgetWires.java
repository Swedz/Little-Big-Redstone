package net.swedz.little_big_redstone.gui.microchip.wire;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.microchip.MicrochipWidget;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.wire.Wire;

import java.util.Collections;
import java.util.List;

public final class MicrochipWidgetWires
{
	private final MicrochipWidget widget;
	private final Microchip       microchip;
	
	private final int wireSize;
	private final int wirePadding;
	private final int wirePortPadding;
	
	private final WirePathing pathing;
	
	public MicrochipWidgetWires(MicrochipWidget widget)
	{
		this.widget = widget;
		this.microchip = widget.microchip();
		
		this.wireSize = 2;
		this.wirePadding = 1;
		this.wirePortPadding = 3;
		int componentMargin = wirePortPadding + 1;
		this.pathing = new WirePathing(
				microchip,
				wirePortPadding * 2,
				(b) -> new Bounds(
						b.minX() - componentMargin, b.minY() - componentMargin,
						b.width() + (componentMargin * 2) - 1, b.height() + (componentMargin * 2) - 1
				)
		);
	}
	
	public void rebuildPaths()
	{
		pathing.forgetEverything();
	}
	
	public boolean isHovering(Wire wire, int boardMouseX, int boardMouseY)
	{
		return pathing.contains(wire, boardMouseX, boardMouseY, wireSize, wirePadding);
	}
	
	public Wire findHoveredWire(int boardMouseX, int boardMouseY)
	{
		List<Wire> backwardsWires = Lists.newArrayList(microchip.wires());
		Collections.reverse(backwardsWires);
		for(var wire : backwardsWires)
		{
			if(this.isHovering(wire, boardMouseX, boardMouseY))
			{
				return wire;
			}
		}
		return null;
	}
	
	public void renderWires(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		for(var wire : microchip.wires())
		{
			if(widget.getContext().topLayerWires().contains(wire))
			{
				continue;
			}
			this.renderWire(graphics, wire, false, mouseX, mouseY, partialTicks);
		}
		
		for(var wire : widget.getContext().topLayerWires())
		{
			this.renderWire(graphics, wire, widget.getContext().wire() == wire, mouseX, mouseY, partialTicks);
		}
		
		if(widget.hasSelectedPort() &&
		   widget.isMouseOver(mouseX, mouseY) &&
		   microchip.components().findAt(microchip.size().boardX(widget.toLocalX(mouseX)), microchip.size().boardY(widget.toLocalY(mouseY))) == null)
		{
			var selectedPort = widget.getSelectedPort();
			this.renderWire(graphics, selectedPort.entry(), mouseX, mouseY, selectedPort.index(), partialTicks);
		}
	}
	
	private void renderWire(GuiGraphics graphics, Wire wire, boolean hovered, int mouseX, int mouseY, float partialTicks)
	{
		LogicEntry outputLogic = microchip.components().get(wire.output().slot());
		LogicEntry inputLogic = microchip.components().get(wire.input().slot());
		this.renderWire(graphics, wire, hovered, outputLogic, inputLogic, wire.output().index(), wire.input().index(), partialTicks);
	}
	
	private int getWireStartX(LogicEntry outputLogic)
	{
		return outputLogic.x() + outputLogic.component().size().widthPixels();
	}
	
	private int getWireStartY(LogicEntry outputLogic, int outputIndex)
	{
		return outputLogic.component().size().portTopLeftCornerY(outputLogic.y(), false, outputIndex, outputLogic.component().outputs()) + 8 - 1;
	}
	
	private int getWireEndX(LogicEntry inputLogic)
	{
		return inputLogic.x();
	}
	
	private int getWireEndY(LogicEntry inputLogic, int inputIndex)
	{
		return inputLogic.component().size().portTopLeftCornerY(inputLogic.y(), true, inputIndex, inputLogic.component().inputs()) + 8 - 1;
	}
	
	private int getWireColor(LogicEntry outputLogic)
	{
		return LBRColors.componentForeground(((LogicComponent<?, ?>) outputLogic.component()).color().orElse(widget.color()));
	}
	
	private void renderWire(GuiGraphics graphics, Wire wire, boolean hovered, LogicEntry outputLogic, LogicEntry inputLogic, int outputIndex, int inputIndex, float partialTicks)
	{
		int startX = this.getWireStartX(outputLogic);
		int startY = this.getWireStartY(outputLogic, outputIndex);
		int endX = this.getWireEndX(inputLogic);
		int endY = this.getWireEndY(inputLogic, inputIndex);
		
		boolean powered = outputLogic.component().output(outputIndex);
		
		int argb = this.getWireColor(outputLogic);
		
		this.renderWire(graphics, wire, hovered, startX, startY, endX, endY, true, powered, argb, partialTicks);
	}
	
	private void renderWire(GuiGraphics graphics, LogicEntry outputLogic, int mouseX, int mouseY, int outputIndex, float partialTicks)
	{
		int startX = this.getWireStartX(outputLogic);
		int startY = this.getWireStartY(outputLogic, outputIndex);
		int endX;
		int endY;
		boolean usePadding;
		if(widget.getContext().shouldInteractPort() && widget.getContext().isPortInput())
		{
			var inputLogic = widget.getContext().logic();
			endX = this.getWireEndX(inputLogic);
			endY = this.getWireEndY(inputLogic, widget.getContext().port().index());
			usePadding = true;
		}
		else
		{
			endX = microchip.size().boardX(widget.toLocalX(mouseX)) + 1;
			endY = microchip.size().boardY(widget.toLocalY(mouseY)) - 1;
			usePadding = false;
		}
		
		boolean powered = outputLogic.component().output(outputIndex);
		
		int argb = this.getWireColor(outputLogic);
		
		this.renderWire(graphics, null, true, startX, startY, endX, endY, usePadding, powered, argb, partialTicks);
	}
	
	private void renderWire(GuiGraphics graphics, Wire wire, boolean hovered, int startX, int startY, int endX, int endY, boolean usePadding, boolean powered, int argb, float partialTicks)
	{
		int portPadding = usePadding ? wirePortPadding : 0;
		
		float pulsingAlpha = MicrochipScreen.getPulsingAlpha(partialTicks);
		
		var texture = LBR.id("textures/gui/container/microchip/wire_%s.png".formatted(powered ? "on" : "off"));
		GuiGraphicsHelper.setColor(graphics, argb);
		graphics.blit(texture, startX, startY, startX, startY, portPadding, wireSize, 16, 16);
		graphics.blit(texture, endX - portPadding, endY, endX - portPadding, endY, portPadding, wireSize, 16, 16);
		if(hovered)
		{
			graphics.setColor(1, 1, 1, pulsingAlpha);
			graphics.fill(startX, startY, startX + portPadding, startY + wireSize, 0xFFFFFFFF);
			graphics.fill(endX - portPadding, endY, endX, endY + wireSize, 0xFFFFFFFF);
			GuiGraphicsHelper.setColor(graphics, argb);
		}
		for(var position : pathing.get(wire, startX + portPadding, startY, endX - portPadding - wireSize, endY))
		{
			graphics.blit(texture, position.x(), position.y(), position.x(), position.y(), wireSize, wireSize, 16, 16);
			if(hovered)
			{
				graphics.setColor(1, 1, 1, pulsingAlpha);
				graphics.fill(position.x(), position.y(), position.x() + wireSize, position.y() + wireSize, 0xFFFFFFFF);
				GuiGraphicsHelper.setColor(graphics, argb);
			}
		}
		GuiGraphicsHelper.resetColor(graphics);
	}
}
