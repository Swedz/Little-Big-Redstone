package net.swedz.little_big_redstone.gui.microchip.panel;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientRenderPipelines;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItemDisplayContext;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidgetContext;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.note.StickyNoteEntry;

import java.util.List;
import java.util.function.Supplier;

public final class MicrochipRenderBoardPanel extends MicrochipRenderPanel
{
	private static final Identifier CIRCUITBOARD_BACKGROUND = LBR.id("textures/gui/container/microchip/circuit_background.png");
	
	private final MicrochipRenderWiresPanel wires;
	
	public MicrochipRenderBoardPanel(DyeColor color, Microchip microchip,
									 Supplier<MicrochipWidgetContext> context)
	{
		super(color, microchip, context);
		wires = new MicrochipRenderWiresPanel(color, microchip, context);
	}
	
	public MicrochipRenderBoardPanel(DyeColor color, Microchip microchip)
	{
		this(color, microchip, null);
	}
	
	public MicrochipRenderWiresPanel wires()
	{
		return wires;
	}
	
	@Override
	public void render(GuiGraphicsExtractor graphics)
	{
		graphics.pose().pushMatrix();
		
		this.renderCircuitboardBackground(graphics);
		this.renderGridSnappingOverlay(graphics);
		
		wires.render(graphics);
		this.renderAllLogic(graphics);
		
		this.renderAllNotes(graphics);
		
		graphics.pose().popMatrix();
	}
	
	private void renderLogic(GuiGraphicsExtractor graphics, LogicEntry entry, boolean hasSelectedPort, boolean isCarryingWire)
	{
		if(entry.visible())
		{
			var context = LogicRenderer.Context.create(entry.color(), color, entry.type(), true, hasSelectedPort, isCarryingWire);
			LogicRenderers.render(context, graphics, entry.component(), entry.x(), entry.y());
			
			if(microchip.isDebug())
			{
				graphics.text(Minecraft.getInstance().font, "#" + entry.slot(), entry.x(), entry.y() - 8, 0xFFFFFFFF);
				
				List<Integer> indexes = Lists.newArrayList();
				int index = 0;
				for(var other : microchip.components().traversal())
				{
					if(entry.slot() == other.slot())
					{
						indexes.add(index);
					}
					index++;
				}
				for(int i = 0; i < indexes.size(); i++)
				{
					int ind = indexes.get(i);
					graphics.text(Minecraft.getInstance().font, Integer.toString(ind), entry.x(), entry.y() + (i * 8), 0xFFFFFFFF);
				}
			}
		}
	}
	
	public void renderLogic(GuiGraphicsExtractor graphics, LogicEntry entry)
	{
		var context = this.context();
		this.renderLogic(graphics, entry, context != null && context.widget().hasSelectedPort(), context != null && context.widget().menu().getCarried().is(LBRItems.REDSTONE_BIT.asItem()));
	}
	
	public void renderAllLogic(GuiGraphicsExtractor graphics)
	{
		var context = this.context();
		
		for(var entry : microchip.components())
		{
			if(context != null && entry == context.logic())
			{
				continue;
			}
			this.renderLogic(graphics, entry);
		}
		
		if(context != null && context.hasLogic())
		{
			this.renderLogic(graphics, context.logic());
		}
	}
	
	public void renderNote(GuiGraphicsExtractor graphics, StickyNoteEntry entry)
	{
		graphics.item(entry.toStack(), LBRItemDisplayContext.MICROCHIP_GUI, entry.x(), entry.y());
	}
	
	public void renderAllNotes(GuiGraphicsExtractor graphics)
	{
		var context = this.context();
		
		for(var entry : microchip.stickyNotes())
		{
			if(context != null && entry == context.note())
			{
				continue;
			}
			this.renderNote(graphics, entry);
		}
		
		if(context != null && context.hasNote())
		{
			this.renderNote(graphics, context.note());
		}
	}
	
	private void renderGridSnappingOverlay(GuiGraphicsExtractor graphics)
	{
		var context = this.context();
		
		if(context != null && Minecraft.getInstance().hasControlDown() && context.isOnBoard())
		{
			var carried = context.widget().menu().getCarried();
			var size = microchip.size();
			int boardMouseX = context.boardMouseX();
			int boardMouseY = context.boardMouseY();
			
			boolean isStickyNote = StickyNoteItem.hasRelevantComponents(carried);
			boolean isLogic = carried.has(LBRComponents.LOGIC_CONFIG);
			if(isStickyNote || isLogic)
			{
				int x, y;
				int width, height;
				if(isStickyNote)
				{
					x = MicrochipScreen.getGridSnappedCoord(boardMouseX);
					y = MicrochipScreen.getGridSnappedCoord(boardMouseY);
					width = 16;
					height = 16;
				}
				else
				{
					var logicConfig = carried.get(LBRComponents.LOGIC_CONFIG);
					x = MicrochipScreen.getGridSnappedCoord(logicConfig.size().topLeftCornerX(boardMouseX) + 8);
					y = MicrochipScreen.getGridSnappedCoord(logicConfig.size().topLeftCornerY(boardMouseY) + 8);
					width = logicConfig.size().widthPixels();
					height = logicConfig.size().heightPixels();
				}
				var pipeline = LBRClientRenderPipelines.PULSING_ALPHA;
				var texture = LBR.id("textures/gui/container/microchip/grid_snapping_overlay.png");
				if(y >= size.bounds().minY())
				{
					graphics.blit(pipeline, texture, 0, y, 0, y, size.bounds().width(), 1, 16, 16);
				}
				if(y + height - 1 <= size.bounds().maxY())
				{
					graphics.blit(pipeline, texture, 0, y + height - 1, 0, y + height - 1, size.bounds().width(), 1, 16, 16);
				}
				if(x >= size.bounds().minX())
				{
					graphics.blit(pipeline, texture, x, 0, x, 0, 1, size.bounds().height(), 16, 16);
				}
				if(x + width - 1 <= size.bounds().maxX())
				{
					graphics.blit(pipeline, texture, x + width - 1, 0, x + width - 1, 0, 1, size.bounds().height(), 16, 16);
				}
			}
		}
	}
	
	public void renderCircuitboardBackground(GuiGraphicsExtractor graphics)
	{
		int argb = LBRColors.circuitboard(color);
		graphics.blit(CIRCUITBOARD_BACKGROUND, 0, 0, 0, 0, microchip.size().bounds().width(), microchip.size().bounds().height(), 64, 64, argb);
	}
}
