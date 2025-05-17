package net.swedz.little_big_redstone.gui.microchip.widget;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
import net.swedz.little_big_redstone.gui.microchip.wire.WireEndpoints;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathing;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.tesseract.neoforge.api.Bounds;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

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
	
	public void renderWires(TesseractGuiGraphics graphics, int boardMouseX, int boardMouseY, float partialTicks)
	{
		for(var wire : widget.microchip().wires())
		{
			if(widget.context().topLayerWires().contains(wire))
			{
				continue;
			}
			this.renderWire(graphics, wire, false, partialTicks);
		}
		
		graphics = graphics.inner();
		graphics.enableBatching();
		for(var wire : widget.context().topLayerWires())
		{
			this.renderWire(graphics, wire, !widget.hasSelectedPort() && (widget.context().wire() == null || widget.context().wire() == wire), partialTicks);
		}
		
		if(widget.hasSelectedPort() &&
		   widget.context().isOnBoard() &&
		   widget.microchip().findAt(boardMouseX, boardMouseY) == null)
		{
			var endpoints = WireEndpoints.heldWire(widget.context());
			this.renderWire(graphics, endpoints, true, partialTicks);
		}
		graphics.end();
	}
	
	private void renderWire(TesseractGuiGraphics graphics, Wire wire, boolean hovered, float partialTicks)
	{
		this.renderWire(graphics, wire, WireEndpoints.of(widget.context(), wire), hovered, partialTicks);
	}
	
	public void renderWire(TesseractGuiGraphics graphics, WireEndpoints endpoints, boolean hovered, float partialTicks)
	{
		this.renderWire(graphics, Either.left(null), endpoints, hovered, partialTicks);
	}
	
	public void renderWire(TesseractGuiGraphics graphics, Wire wire, WireEndpoints endpoints, boolean hovered, float partialTicks)
	{
		this.renderWire(graphics, Either.left(wire), endpoints, hovered, partialTicks);
	}
	
	public void renderWire(TesseractGuiGraphics graphics, List<Bounds> avoidBounds, WireEndpoints endpoints, boolean hovered, float partialTicks)
	{
		this.renderWire(graphics, Either.right(avoidBounds), endpoints, hovered, partialTicks);
	}
	
	private void renderWire(TesseractGuiGraphics graphics, Either<Wire, List<Bounds>> eitherWireOrBounds, WireEndpoints endpoints, boolean hovered, float partialTicks)
	{
		this.renderWire(graphics, eitherWireOrBounds, endpoints.startX(), endpoints.startY(), endpoints.endX(), endpoints.endY(), hovered, endpoints.usePadding(), endpoints.powered(), endpoints.argb(), partialTicks);
	}
	
	private void renderWire(TesseractGuiGraphics graphics, Either<Wire, List<Bounds>> eitherWireOrBounds, int startX, int startY, int endX, int endY, boolean hovered, boolean usePadding, boolean powered, int argb, float partialTicks)
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
			graphics.setTextureShader(LBRClientShaders::pulsingTextureLightness);
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
