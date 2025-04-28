package net.swedz.little_big_redstone.gui.microchip.wire;

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

public final class WireRendering
{
	private final MicrochipWidget widget;
	private final Microchip       microchip;
	
	private final int wireMargin;
	
	private final WirePathing pathing;
	
	public WireRendering(MicrochipWidget widget)
	{
		this.widget = widget;
		this.microchip = widget.microchip();
		
		this.wireMargin = 3;
		int componentMargin = wireMargin + 1;
		this.pathing = new WirePathing(
				microchip,
				wireMargin * 2,
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
	
	public void renderWires(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		for(var wire : microchip.wires())
		{
			LogicEntry outputLogic = microchip.components().get(wire.output().slot());
			LogicEntry inputLogic = microchip.components().get(wire.input().slot());
			this.renderWire(graphics, wire, outputLogic, inputLogic, wire.output().index(), wire.input().index());
		}
		
		if(widget.hasSelectedPort() &&
		   widget.isMouseOver(mouseX, mouseY) &&
		   microchip.components().findAt(microchip.size().boardX(widget.toLocalX(mouseX)), microchip.size().boardY(widget.toLocalY(mouseY))) == null)
		{
			var selectedPort = widget.getSelectedPort();
			this.renderWire(graphics, selectedPort.entry(), mouseX, mouseY, selectedPort.index());
		}
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
		graphics.blit(texture, startX, startY, startX, startY, wireMargin, 2, 16, 16);
		graphics.blit(texture, endX - wireMargin, endY, endX - wireMargin, endY, wireMargin, 2, 16, 16);
		for(var position : pathing.get(wire, startX + wireMargin, startY, endX - wireMargin - 2, endY))
		{
			graphics.blit(texture, position.x(), position.y(), position.x(), position.y(), 2, 2, 16, 16);
		}
		GuiGraphicsHelper.resetColor(graphics);
	}
}
