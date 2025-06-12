package net.swedz.little_big_redstone.gui.microchip.panel;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
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
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.List;
import java.util.function.Supplier;

public final class MicrochipRenderBoardPanel extends MicrochipRenderPanel
{
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
	public void render(TesseractGuiGraphics graphics)
	{
		graphics.pose().pushPose();
		
		this.renderCircuitboardBackground(graphics);
		this.renderGridSnappingOverlay(graphics);
		
		graphics.enableBatching();
		wires.render(graphics);
		this.renderAllLogic(graphics);
		graphics.drawBatches();
		
		this.renderAllNotes(graphics);
		
		graphics.pose().popPose();
	}
	
	private void renderLogic(TesseractGuiGraphics graphics, LogicEntry entry, boolean hasSelectedPort, boolean isCarryingWire)
	{
		var context = LogicRenderer.Context.create(color, entry.component(), true, hasSelectedPort, isCarryingWire);
		LogicRenderers.render(context, graphics, entry.component(), entry.x(), entry.y());
		
		if(microchip.isDebug())
		{
			graphics.resetColor();
			graphics.drawString("#" + entry.slot(), entry.x(), entry.y() - 8);
			
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
				graphics.drawString(Integer.toString(ind), entry.x(), entry.y() + (i * 8));
			}
		}
	}
	
	public void renderLogic(TesseractGuiGraphics graphics, LogicEntry entry)
	{
		var context = this.context();
		this.renderLogic(graphics, entry, context != null && context.widget().hasSelectedPort(), context != null && context.widget().menu().getCarried().is(LBRItems.REDSTONE_BIT.asItem()));
	}
	
	public void renderAllLogic(TesseractGuiGraphics graphics)
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
			graphics = graphics.inner();
			graphics.enableBatching();
			this.renderLogic(graphics, context.logic());
			graphics.end();
		}
	}
	
	public void renderNote(TesseractGuiGraphics graphics, StickyNoteEntry entry)
	{
		graphics.pose().pushPose();
		graphics.renderItem(entry.toStack(), LBRItemDisplayContext.MICROCHIP_GUI, entry.x(), entry.y());
		graphics.pose().popPose();
	}
	
	public void renderAllNotes(TesseractGuiGraphics graphics)
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
			graphics = graphics.inner();
			graphics.enableBatching();
			this.renderNote(graphics, context.note());
			graphics.end();
		}
	}
	
	private void renderGridSnappingOverlay(TesseractGuiGraphics graphics)
	{
		var context = this.context();
		
		if(context != null && Screen.hasControlDown() && context.isOnBoard())
		{
			var carried = context.widget().menu().getCarried();
			var size = microchip.size();
			int boardMouseX = context.boardMouseX();
			int boardMouseY = context.boardMouseY();
			
			boolean isStickyNote = carried.getItem() instanceof StickyNoteItem;
			boolean isLogic = carried.has(LBRComponents.LOGIC);
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
					var component = carried.get(LBRComponents.LOGIC);
					x = MicrochipScreen.getGridSnappedCoord(component.size().topLeftCornerX(boardMouseX) + 8);
					y = MicrochipScreen.getGridSnappedCoord(component.size().topLeftCornerY(boardMouseY) + 8);
					width = component.size().widthPixels();
					height = component.size().heightPixels();
				}
				graphics.enableBatching();
				graphics.resetColor();
				graphics.setTexture(LBR.id("textures/gui/container/microchip/grid_snapping_overlay.png"));
				graphics.setTextureShader(LBRClientShaders::pulsingAlpha, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
				if(y >= size.bounds().minY())
				{
					graphics.blit(0, y, 0, y, size.bounds().width(), 1, 16, 16);
				}
				if(y + height - 1 <= size.bounds().maxY())
				{
					graphics.blit(0, y + height - 1, 0, y + height - 1, size.bounds().width(), 1, 16, 16);
				}
				if(x >= size.bounds().minX())
				{
					graphics.blit(x, 0, x, 0, 1, size.bounds().height(), 16, 16);
				}
				if(x + width - 1 <= size.bounds().maxX())
				{
					graphics.blit(x + width - 1, 0, x + width - 1, 0, 1, size.bounds().height(), 16, 16);
				}
				graphics.resetTextureShader();
				graphics.drawBatches();
			}
		}
	}
	
	public void renderCircuitboardBackground(TesseractGuiGraphics graphics)
	{
		graphics.setColor(LBRColors.circuitboard(color));
		graphics.setTexture(LBR.id("textures/gui/container/microchip/circuit_background.png"));
		graphics.blit(0, 0, 0, 0, microchip.size().bounds().width(), microchip.size().bounds().height(), 64, 64);
		graphics.resetColor();
	}
}
