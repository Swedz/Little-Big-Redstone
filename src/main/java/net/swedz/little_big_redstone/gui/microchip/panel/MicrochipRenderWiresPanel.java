package net.swedz.little_big_redstone.gui.microchip.panel;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidgetContext;
import net.swedz.little_big_redstone.gui.microchip.wire.WireMetadata;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePath;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathKey;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathing;
import net.swedz.little_big_redstone.gui.microchip.wire.render.WireRenderState;
import net.swedz.little_big_redstone.gui.microchip.wire.render.WiresGuiElementRenderState;
import net.swedz.little_big_redstone.gui.microchip.wire.render.WiresRenderState;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.tesseract.neoforge.api.Bounds;
import org.joml.Matrix3x2f;

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
	public void render(GuiGraphicsExtractor graphics)
	{
		this.renderAllWires(graphics);
	}
	
	private void renderAllWires(GuiGraphicsExtractor graphics)
	{
		var context = this.context();
		
		var state = new WiresRenderState();
		
		for(var wire : microchip.wires())
		{
			if(context != null && context.topLayerWires().contains(wire))
			{
				continue;
			}
			var key = WirePathKey.placed(microchip, wire);
			var path = pathing.get(key, true);
			var metadata = WireMetadata.of(microchip, color, wire, false);
			state.add(this.renderWire(key, path, metadata));
		}
		
		if(context != null)
		{
			for(var wire : context.topLayerWires())
			{
				var key = WirePathKey.placed(microchip, wire);
				var path = pathing.get(key, true);
				boolean hovered = !context.widget().hasSelectedPort() && (context.wire() == null || context.wire() == wire);
				var metadata = WireMetadata.of(microchip, color, wire, hovered);
				state.add(this.renderWire(key, path, metadata));
			}
			
			if(context.widget().hasSelectedPort() &&
			   context.isOnBoard())
			{
				var key = WirePathKey.held(context);
				var path = pathing.get(key, false);
				path.block();
				var metadata = WireMetadata.of(microchip, color, context.widget().getSelectedPort(), true);
				state.add(this.renderWire(key, path, metadata));
			}
		}
		
		var pose = new Matrix3x2f(graphics.pose());
		var boardBounds = microchip.size().bounds();
		var scissorArea = graphics.peekScissorStack();
		graphics.submitGuiElementRenderState(new WiresGuiElementRenderState(pose, boardBounds, state, false, false, scissorArea));
		graphics.submitGuiElementRenderState(new WiresGuiElementRenderState(pose, boardBounds, state, true, false, scissorArea));
		graphics.submitGuiElementRenderState(new WiresGuiElementRenderState(pose, boardBounds, state, false, true, scissorArea));
		graphics.submitGuiElementRenderState(new WiresGuiElementRenderState(pose, boardBounds, state, true, true, scissorArea));
	}
	
	public WireRenderState renderWire(WirePathKey key, WirePath path, WireMetadata metadata)
	{
		if(!path.isPopulated())
		{
			return null;
		}
		
		int maxX = microchip.size().bounds().maxX();
		int maxY = microchip.size().bounds().maxY();
		
		var state = new WireRenderState();
		state.wireSize = wireSize;
		state.powered = metadata.powered();
		state.hovered = metadata.hovered();
		state.color = metadata.argb();
		
		for(var position : path.positions())
		{
			if(position.x() < 0 || position.y() < 0 || position.x() >= maxX || position.y() >= maxY)
			{
				continue;
			}
			state.positions.add(position);
		}
		
		return state;
	}
}
