package net.swedz.little_big_redstone.gui.microchip.panel;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidgetContext;
import net.swedz.little_big_redstone.gui.microchip.wire.WireEndpoints;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathing;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.tesseract.neoforge.api.Bounds;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class MicrochipRenderWiresPanel extends MicrochipRenderPanel
{
	private final int wireSize;
	private final int wirePadding;
	private final int wirePortPadding;
	
	private final WirePathing pathing;
	
	public MicrochipRenderWiresPanel(DyeColor color, Microchip microchip, Supplier<MicrochipWidgetContext> context)
	{
		super(color, microchip, context);
		
		this.wireSize = 2;
		this.wirePadding = 1;
		this.wirePortPadding = 3;
		int componentPadding = wirePortPadding + 1;
		pathing = new WirePathing(
				microchip,
				0,
				(b) -> new Bounds(
						b.minX() - componentPadding, b.minY() - componentPadding,
						b.width() + (componentPadding * 2) - 1, b.height() + (componentPadding * 2) - 1
				)
		);
	}
	
	public MicrochipRenderWiresPanel(DyeColor color, Microchip microchip)
	{
		this(color, microchip, null);
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
	
	@Override
	public void render(TesseractGuiGraphics graphics)
	{
		this.renderAllWires(graphics);
	}
	
	private void renderAllWires(TesseractGuiGraphics graphics)
	{
		var context = this.context();
		
		for(var wire : microchip.wires())
		{
			if(context != null && context.topLayerWires().contains(wire))
			{
				continue;
			}
			this.renderWire(graphics, wire, false);
		}
		
		if(context != null)
		{
			graphics = graphics.inner();
			graphics.enableBatching();
			for(var wire : context.topLayerWires())
			{
				this.renderWire(graphics, wire, !context.widget().hasSelectedPort() && (context.wire() == null || context.wire() == wire));
			}
			
			if(context.widget().hasSelectedPort() &&
			   context.isOnBoard() &&
			   microchip.findAt(context.boardMouseX(), context.boardMouseY()) == null)
			{
				var endpoints = WireEndpoints.heldWire(context);
				this.renderWire(graphics, endpoints, true);
			}
			graphics.end();
		}
	}
	
	private void renderWire(TesseractGuiGraphics graphics, Wire wire, boolean hovered)
	{
		this.renderWire(graphics, wire, WireEndpoints.of(color, microchip, wire), hovered);
	}
	
	public void renderWire(TesseractGuiGraphics graphics, WireEndpoints endpoints, boolean hovered)
	{
		this.renderWire(graphics, Either.left(null), endpoints, hovered);
	}
	
	public void renderWire(TesseractGuiGraphics graphics, Wire wire, WireEndpoints endpoints, boolean hovered)
	{
		this.renderWire(graphics, Either.left(wire), endpoints, hovered);
	}
	
	public void renderWire(TesseractGuiGraphics graphics, List<Bounds> avoidBounds, WireEndpoints endpoints, boolean hovered)
	{
		this.renderWire(graphics, Either.right(avoidBounds), endpoints, hovered);
	}
	
	private void renderWire(TesseractGuiGraphics graphics, Either<Wire, List<Bounds>> eitherWireOrBounds, WireEndpoints endpoints, boolean hovered)
	{
		if(endpoints.valid())
		{
			this.renderWire(graphics, eitherWireOrBounds, endpoints.startX(), endpoints.startY(), endpoints.endX(), endpoints.endY(), hovered, endpoints.usePadding(), endpoints.powered(), endpoints.argb());
		}
	}
	
	private void renderWire(TesseractGuiGraphics graphics, Either<Wire, List<Bounds>> eitherWireOrBounds, int startX, int startY, int endX, int endY, boolean hovered, boolean usePadding, boolean powered, int argb)
	{
		int maxX = microchip.size().bounds().maxX();
		int maxY = microchip.size().bounds().maxY();
		
		int portPadding = usePadding ? wirePortPadding : 0;
		boolean renderStart = startX >= 0 && startX < maxX && startY >= 0 && startY < maxY;
		boolean renderEnd = endX - portPadding >= 0 && endX - portPadding < maxX && endY >= 0 && endY < maxY;
		
		if(renderStart || renderEnd)
		{
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
			if(renderStart)
			{
				graphics.blit(startX, startY, startX, startY, portPadding, wireSize, 16, 16);
			}
			if(renderEnd)
			{
				graphics.blit(endX - portPadding, endY, endX - portPadding, endY, portPadding, wireSize, 16, 16);
			}
			for(var position : path)
			{
				if(position.x() < 0 || position.y() < 0 || position.x() >= maxX || position.y() >= maxY)
				{
					continue;
				}
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
}
