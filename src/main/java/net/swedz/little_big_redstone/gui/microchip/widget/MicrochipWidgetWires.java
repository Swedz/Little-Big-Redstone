package net.swedz.little_big_redstone.gui.microchip.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Either;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.client.model.logic.LogicBakingModelData;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathing;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.wire.Wire;

import java.util.Collections;
import java.util.List;

public final class MicrochipWidgetWires
{
	private final MicrochipWidget widget;
	
	private final int wireSize;
	private final int wirePadding;
	private final int wirePortPadding;
	
	private final WirePathing pathing;
	
	MicrochipWidgetWires(MicrochipWidget widget)
	{
		this.widget = widget;
		
		this.wireSize = 2;
		this.wirePadding = 1;
		this.wirePortPadding = 3;
		int boardPadding = wirePortPadding * 2;
		int componentPadding = wirePortPadding + 1;
		this.pathing = new WirePathing(
				widget.microchip(),
				boardPadding,
				(b) -> new Bounds(
						b.minX() - componentPadding, b.minY() - componentPadding,
						b.width() + (componentPadding * 2) - 1, b.height() + (componentPadding * 2) - 1
				)
		);
	}
	
	public WirePathing pathing()
	{
		return pathing;
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
		List<Wire> backwardsWires = Lists.newArrayList(widget.microchip().wires());
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
	
	public void renderWires(TesseractGuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		for(var wire : widget.microchip().wires())
		{
			if(widget.context().topLayerWires().contains(wire))
			{
				continue;
			}
			this.renderWire(graphics, wire, false, mouseX, mouseY, partialTicks);
		}
		
		graphics = graphics.inner();
		graphics.enableBatching();
		for(var wire : widget.context().topLayerWires())
		{
			this.renderWire(graphics, wire, !widget.hasSelectedPort() && (widget.context().wire() == null || widget.context().wire() == wire), mouseX, mouseY, partialTicks);
		}
		
		if(widget.hasSelectedPort() &&
		   widget.isMouseOver(mouseX, mouseY) &&
		   widget.microchip().findAt(widget.microchip().size().boardX(widget.toLocalX(mouseX)), widget.microchip().size().boardY(widget.toLocalY(mouseY))) == null)
		{
			var selectedPort = widget.getSelectedPort();
			this.renderWire(graphics, selectedPort.entry(), mouseX, mouseY, selectedPort.index(), partialTicks);
		}
		graphics.end();
	}
	
	private void renderWire(TesseractGuiGraphics graphics, Wire wire, boolean hovered, int mouseX, int mouseY, float partialTicks)
	{
		LogicEntry outputLogic = widget.microchip().components().get(wire.output().slot());
		LogicEntry inputLogic = widget.microchip().components().get(wire.input().slot());
		this.renderWire(graphics, wire, hovered, outputLogic, inputLogic, wire.output().index(), wire.input().index(), partialTicks);
	}
	
	public static int getWireStartX(int x, LogicComponent<?, ?> component)
	{
		return x + component.size().widthPixels();
	}
	
	public static int getWireStartX(LogicEntry outputLogic)
	{
		return getWireStartX(outputLogic.x(), outputLogic.component());
	}
	
	public static int getWireStartY(int y, LogicComponent<?, ?> component, int outputIndex)
	{
		return component.size().portTopLeftCornerY(y, false, outputIndex, component.outputs()) + 8 - 1;
	}
	
	public static int getWireStartY(LogicEntry outputLogic, int outputIndex)
	{
		return getWireStartY(outputLogic.y(), outputLogic.component(), outputIndex);
	}
	
	public static int getWireEndX(int x)
	{
		return x;
	}
	
	public static int getWireEndX(LogicEntry inputLogic)
	{
		return getWireEndX(inputLogic.x());
	}
	
	public static int getWireEndY(int y, LogicComponent<?, ?> component, int inputIndex)
	{
		return component.size().portTopLeftCornerY(y, true, inputIndex, component.inputs()) + 8 - 1;
	}
	
	public static int getWireEndY(LogicEntry inputLogic, int inputIndex)
	{
		return getWireEndY(inputLogic.y(), inputLogic.component(), inputIndex);
	}
	
	public int getWireColor(LogicEntry outputLogic)
	{
		return LogicBakingModelData.get(outputLogic.component()).getColorSet(((LogicComponent<?, ?>) outputLogic.component()).color().orElse(widget.color())).foreground();
	}
	
	private void renderWire(TesseractGuiGraphics graphics, Wire wire, boolean hovered, LogicEntry outputLogic, LogicEntry inputLogic, int outputIndex, int inputIndex, float partialTicks)
	{
		int startX = getWireStartX(outputLogic);
		int startY = getWireStartY(outputLogic, outputIndex);
		int endX = getWireEndX(inputLogic);
		int endY = getWireEndY(inputLogic, inputIndex);
		
		boolean powered = outputLogic.component().output(outputIndex);
		
		int argb = this.getWireColor(outputLogic);
		
		this.renderWire(graphics, wire, hovered, startX, startY, endX, endY, true, powered, argb, partialTicks);
	}
	
	private void renderWire(TesseractGuiGraphics graphics, LogicEntry outputLogic, int mouseX, int mouseY, int outputIndex, float partialTicks)
	{
		int startX = getWireStartX(outputLogic);
		int startY = getWireStartY(outputLogic, outputIndex);
		int endX;
		int endY;
		boolean usePadding;
		if(widget.context().shouldInteractPort() && widget.context().isPortInput() && widget.context().isPortEmpty())
		{
			var inputLogic = widget.context().logic();
			endX = getWireEndX(inputLogic);
			endY = getWireEndY(inputLogic, widget.context().port().index());
			usePadding = true;
		}
		else
		{
			endX = widget.microchip().size().boardX(widget.toLocalX(mouseX)) + 1;
			endY = widget.microchip().size().boardY(widget.toLocalY(mouseY)) - 1;
			usePadding = false;
		}
		
		boolean powered = outputLogic.component().output(outputIndex);
		
		int argb = this.getWireColor(outputLogic);
		
		this.renderWire(graphics, Either.left(null), true, startX, startY, endX, endY, usePadding, powered, argb, partialTicks);
	}
	
	private void renderWire(TesseractGuiGraphics graphics, Wire wire, boolean hovered, int startX, int startY, int endX, int endY, boolean usePadding, boolean powered, int argb, float partialTicks)
	{
		this.renderWire(graphics, Either.left(wire), hovered, startX, startY, endX, endY, usePadding, powered, argb, partialTicks);
	}
	
	public void renderWire(TesseractGuiGraphics graphics, Either<Wire, List<Bounds>> eitherWireOrBounds, boolean hovered, int startX, int startY, int endX, int endY, boolean usePadding, boolean powered, int argb, float partialTicks)
	{
		int portPadding = usePadding ? wirePortPadding : 0;
		
		var path = eitherWireOrBounds.map(
				(wire) -> pathing.get(wire, startX + portPadding, startY, endX - portPadding - wireSize, endY),
				(avoidBounds) -> pathing.build(startX + portPadding, startY, endX - portPadding - wireSize, endY, avoidBounds)
		);
		
		if(hovered)
		{
			graphics = graphics.inner();
			graphics.enableBatching();
			graphics.setTextureShader(LBRClientShaders::microchipWireHovered, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		}
		
		graphics.setColor(argb);
		graphics.setTexture(LBR.id("textures/gui/container/microchip/wire_%s.png".formatted(powered ? "on" : "off")));
		graphics.blit(startX, startY, startX, startY, portPadding, wireSize, 16, 16);
		graphics.blit(endX - portPadding, endY, endX - portPadding, endY, portPadding, wireSize, 16, 16);
		for(var position : path)
		{
			graphics.blit(position.x(), position.y(), position.x(), position.y(), wireSize, wireSize, 16, 16);
		}
		graphics.resetColor();
		
		if(hovered)
		{
			graphics.resetTextureShader();
			graphics.end();
		}
	}
}
