package net.swedz.little_big_redstone.gui.microchip.wire;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.gui.microchip.MicrochipWidget;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.wire.Wire;

import java.util.Collections;
import java.util.List;

public final class WireRendering
{
	private final MicrochipWidget widget;
	private final Microchip       microchip;
	
	private final int wireSize;
	private final int wirePadding;
	private final int wirePortPadding;
	
	private final WirePathing pathing;
	
	public WireRendering(MicrochipWidget widget)
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
			if(widget.getHovered().topLayerWires().contains(wire))
			{
				continue;
			}
			this.renderWire(graphics, wire, mouseX, mouseY, partialTicks);
		}
		
		for(var wire : widget.getHovered().topLayerWires())
		{
			this.renderWire(graphics, wire, mouseX, mouseY, partialTicks);
		}
		
		if(widget.hasSelectedPort() &&
		   widget.isMouseOver(mouseX, mouseY) &&
		   microchip.components().findAt(microchip.size().boardX(widget.toLocalX(mouseX)), microchip.size().boardY(widget.toLocalY(mouseY))) == null)
		{
			var selectedPort = widget.getSelectedPort();
			this.renderWire(graphics, selectedPort.entry(), mouseX, mouseY, selectedPort.index());
		}
	}
	
	private void renderWire(GuiGraphics graphics, Wire wire, int mouseX, int mouseY, float partialTicks)
	{
		LogicEntry outputLogic = microchip.components().get(wire.output().slot());
		LogicEntry inputLogic = microchip.components().get(wire.input().slot());
		this.renderWire(graphics, wire, outputLogic, inputLogic, wire.output().index(), wire.input().index());
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
	
	private void renderWire(GuiGraphics graphics, Wire wire, LogicEntry outputLogic, LogicEntry inputLogic, int outputIndex, int inputIndex)
	{
		int startX = this.getWireStartX(outputLogic);
		int startY = this.getWireStartY(outputLogic, outputIndex);
		int endX = this.getWireEndX(inputLogic);
		int endY = this.getWireEndY(inputLogic, inputIndex);
		
		boolean powered = outputLogic.component().output(outputIndex);
		
		int argb = this.getWireColor(outputLogic);
		
		this.renderWire(graphics, wire, startX, startY, endX, endY, powered, argb);
	}
	
	private void renderWire(GuiGraphics graphics, LogicEntry outputLogic, int mouseX, int mouseY, int outputIndex)
	{
		int startX = this.getWireStartX(outputLogic);
		int startY = this.getWireStartY(outputLogic, outputIndex);
		int endX = microchip.size().boardX(widget.toLocalX(mouseX)) + 1;
		int endY = microchip.size().boardY(widget.toLocalY(mouseY)) - 1;
		
		boolean powered = outputLogic.component().output(outputIndex);
		
		int argb = this.getWireColor(outputLogic);
		
		this.renderWire(graphics, null, startX, startY, endX, endY, powered, argb);
	}
	
	private void renderWire(GuiGraphics graphics, Wire wire, int startX, int startY, int endX, int endY, boolean powered, int argb)
	{
		var texture = LBR.id("textures/gui/container/microchip/wire_%s.png".formatted(powered ? "on" : "off"));
		GuiGraphicsHelper.setColor(graphics, argb);
		graphics.blit(texture, startX, startY, startX, startY, wirePortPadding, wireSize, 16, 16);
		graphics.blit(texture, endX - wirePortPadding, endY, endX - wirePortPadding, endY, wirePortPadding, wireSize, 16, 16);
		for(var position : pathing.get(wire, startX + wirePortPadding, startY, endX - wirePortPadding - wireSize, endY))
		{
			graphics.blit(texture, position.x(), position.y(), position.x(), position.y(), wireSize, wireSize, 16, 16);
		}
		GuiGraphicsHelper.resetColor(graphics);
	}
}
