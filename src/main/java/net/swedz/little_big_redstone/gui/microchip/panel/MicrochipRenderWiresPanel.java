package net.swedz.little_big_redstone.gui.microchip.panel;

import com.google.common.collect.Lists;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidgetContext;
import net.swedz.little_big_redstone.gui.microchip.wire.WireMetadata;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePath;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathKey;
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
			var key = WirePathKey.placed(microchip, wire);
			var path = pathing.get(key, true);
			var metadata = WireMetadata.of(microchip, color, wire, false);
			this.renderWire(graphics, key, path, metadata);
		}
		
		if(context != null)
		{
			graphics = graphics.inner();
			graphics.enableBatching();
			for(var wire : context.topLayerWires())
			{
				var key = WirePathKey.placed(microchip, wire);
				var path = pathing.get(key, true);
				boolean hovered = !context.widget().hasSelectedPort() && (context.wire() == null || context.wire() == wire);
				var metadata = WireMetadata.of(microchip, color, wire, hovered);
				this.renderWire(graphics, key, path, metadata);
			}
			
			if(context.widget().hasSelectedPort() &&
			   context.isOnBoard())
			{
				var key = WirePathKey.held(context);
				var path = pathing.get(key, false);
				path.block();
				var metadata = WireMetadata.of(microchip, color, context.widget().getSelectedPort(), true);
				this.renderWire(graphics, key, path, metadata);
			}
			graphics.end();
		}
	}
	
	public void renderWire(TesseractGuiGraphics graphics, WirePathKey key, WirePath path, WireMetadata metadata)
	{
		if(!path.isPopulated())
		{
			return;
		}
		
		int maxX = microchip.size().bounds().maxX();
		int maxY = microchip.size().bounds().maxY();
		
		int startX = key.startX();
		int startY = key.startY();
		int endX = key.endX();
		int endY = key.endY();
		
		boolean usePadding = true; // TODO
		int portPadding = usePadding ? wirePortPadding : 0;
		boolean renderStart = startX >= 0 && startX < maxX && startY >= 0 && startY < maxY;
		boolean renderEnd = endX - portPadding >= 0 && endX - portPadding < maxX && endY >= 0 && endY < maxY;
		
		if(renderStart || renderEnd)
		{
			if(metadata.hovered())
			{
				graphics = graphics.inner();
				graphics.enableBatching();
				graphics.setTextureShader(LBRClientShaders::pulsingTextureLightness);
			}
			
			graphics.setColor(metadata.argb());
			graphics.setTexture(LBR.id("textures/gui/container/microchip/wire_%s.png".formatted(metadata.powered() ? "on" : "off")));
			for(var position : path.positions())
			{
				if(position.x() < 0 || position.y() < 0 || position.x() >= maxX || position.y() >= maxY)
				{
					continue;
				}
				graphics.blit(position.x(), position.y(), position.x(), position.y(), wireSize, wireSize, 16, 16);
			}
			graphics.resetColor();
			
			if(metadata.hovered())
			{
				graphics.resetTextureShader();
				graphics.end();
			}
		}
	}
}
