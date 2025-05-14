package net.swedz.little_big_redstone.gui.microchip.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItemDisplayContext;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.client.model.logic.LogicBakingModelData;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.note.StickyNoteEntry;

import java.util.List;
import java.util.function.Consumer;

public final class MicrochipWidgetRenderer
{
	private static final ResourceLocation CIRCUIT_BACKGROUND = LBR.id("textures/gui/container/microchip/circuit_background.png");
	
	private final MicrochipWidget widget;
	
	public MicrochipWidgetRenderer(MicrochipWidget widget)
	{
		this.widget = widget;
	}
	
	private void renderTooltip(TesseractGuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		if(widget.context().shouldRenderTooltip())
		{
			int x = widget.x + widget.microchip().size().scale(widget.microchip().size().bounds().width()) + 10 + 4;
			int y = widget.y + 4;
			if(widget.context().hasNote())
			{
				var entry = widget.context().note();
				var note = entry.note();
				if(!note.isEmpty())
				{
					int minWidth = graphics.guiWidth() - x - 6;
					graphics.setColor(entry.textColor().getTextColor());
					graphics.setStringDropShadow(false);
					graphics.setTooltipFirstLinePadded(false);
					graphics.setTooltipBackgroundPadding(4, 21, 4, 4);
					graphics.renderTooltipBounded(
							List.of(note.parsed()),
							x, y,
							minWidth, minWidth / 2,
							graphics.guiWidth(), graphics.guiHeight(),
							LBR.id("textures/gui/tooltip/sticky_note/%s.png".formatted(entry.noteColor().getName())),
							64, 64, 21
					);
					graphics.resetTooltipBackgroundPadding();
					graphics.setTooltipFirstLinePadded(true);
					graphics.setStringDropShadow(true);
					graphics.resetColor();
				}
			}
			else if(widget.context().hasLogic())
			{
				var component = widget.context().logic().component();
				List<Component> lines = Lists.newArrayList();
				lines.add(component.type().displayName().withStyle(Style.EMPTY.withUnderlined(true)));
				component.type().tooltip(component, false, true, false).ifPresent((Consumer<List<Component>>) lines::addAll);
				if(component.config().hasMenu())
				{
					lines.add(Component.empty());
					lines.add(LBRText.LOGIC_CONFIG_TOOLTIP_CLICK_TO_OPEN.text().withStyle(LBRTooltips.DEFAULT_STYLE));
				}
				
				var colorSet = LogicBakingModelData.get(component).getColorSet(component, widget.color());
				int backgroundColor = colorSet.background();
				int borderColor = colorSet.foreground();
				graphics.renderTooltip(
						lines,
						widget.x + widget.microchip().size().scale(widget.microchip().size().bounds().width()) + 10 + 4,
						widget.y + 4,
						backgroundColor, backgroundColor, borderColor, borderColor
				);
				
				if(widget.microchip().isDebug())
				{
					graphics.pose().pushPose();
					graphics.pose().translate(widget.x + 211, widget.y + 139, 0);
					graphics.pose().scale(2, 2, 2);
					graphics.enableBatching();
					var context = LogicRenderer.Context.create(widget.color(), component, widget.menu().getCarriedWires() != null, widget.hasSelectedPort(), false);
					LogicRenderers.render(context, graphics, component, 0, 0);
					graphics.drawBatches();
					graphics.pose().popPose();
				}
			}
		}
	}
	
	private void renderLogic(TesseractGuiGraphics graphics, LogicEntry entry)
	{
		var context = LogicRenderer.Context.create(widget.menu().color(), entry.component(), true, widget.hasSelectedPort(), widget.menu().getCarried().is(LBRItems.REDSTONE_BIT.asItem()));
		LogicRenderers.render(context, graphics, entry.component(), entry.x(), entry.y());
		
		if(widget.microchip().isDebug())
		{
			graphics.resetColor();
			graphics.drawString("#" + entry.slot(), entry.x(), entry.y() - 8);
			
			List<Integer> indexes = Lists.newArrayList();
			int index = 0;
			for(var other : widget.microchip().components().traversal())
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
	
	private void renderLogic(TesseractGuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		for(var entry : widget.microchip().components())
		{
			if(entry == widget.context().logic())
			{
				continue;
			}
			this.renderLogic(graphics, entry);
		}
		
		if(widget.context().hasLogic())
		{
			graphics = graphics.inner();
			graphics.enableBatching();
			this.renderLogic(graphics, widget.context().logic());
			graphics.end();
		}
	}
	
	private void renderNote(TesseractGuiGraphics graphics, StickyNoteEntry entry)
	{
		graphics.pose().pushPose();
		graphics.renderItem(entry.toStack(), LBRItemDisplayContext.MICROCHIP_GUI, entry.x(), entry.y());
		graphics.pose().popPose();
	}
	
	private void renderNotes(TesseractGuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		for(var entry : widget.microchip().stickyNotes())
		{
			if(entry == widget.context().note())
			{
				continue;
			}
			this.renderNote(graphics, entry);
		}
		
		if(widget.context().hasNote())
		{
			graphics = graphics.inner();
			graphics.enableBatching();
			this.renderNote(graphics, widget.context().note());
			graphics.end();
		}
	}
	
	private void renderLogicGridSnappingOverlay(TesseractGuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		var carried = widget.menu().getCarried();
		var size = widget.microchip().size();
		
		if(Screen.hasControlDown() && widget.isMouseOver(mouseX, mouseY))
		{
			int boardX = size.boardX(widget.toLocalX(mouseX));
			int boardY = size.boardY(widget.toLocalY(mouseY));
			
			boolean isStickyNote = carried.getItem() instanceof StickyNoteItem;
			boolean isLogic = carried.has(LBRComponents.LOGIC);
			if(isStickyNote || isLogic)
			{
				int x, y;
				int width, height;
				if(isStickyNote)
				{
					x = MicrochipScreen.getGridSnappedCoord(boardX);
					y = MicrochipScreen.getGridSnappedCoord(boardY);
					width = 16;
					height = 16;
				}
				else
				{
					var component = carried.get(LBRComponents.LOGIC);
					x = MicrochipScreen.getGridSnappedCoord(boardX - component.size().centerX() + 8);
					y = MicrochipScreen.getGridSnappedCoord(boardY - component.size().centerY() + 8);
					width = component.size().widthPixels();
					height = component.size().heightPixels();
				}
				graphics.enableBatching();
				graphics.resetColor();
				graphics.setTexture(LBR.id("textures/gui/container/microchip/grid_snapping_overlay.png"));
				graphics.setTextureShader(LBRClientShaders::microchipGridSnappingOverlay, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
				graphics.blit(0, y, 0, y, size.bounds().width(), 1, 16, 16);
				graphics.blit(0, y + height - 1, 0, y + height - 1, size.bounds().width(), 1, 16, 16);
				graphics.blit(x, 0, x, 0, 1, size.bounds().height(), 16, 16);
				graphics.blit(x + width - 1, 0, x + width - 1, 0, 1, size.bounds().height(), 16, 16);
				graphics.resetTextureShader();
				graphics.drawBatches();
			}
		}
	}
	
	private void renderCircuitBg(TesseractGuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		graphics.setColor(LBRColors.circuitboard(widget.menu().color()));
		graphics.setTexture(CIRCUIT_BACKGROUND);
		graphics.blit(0, 0, 0, 0, widget.width, widget.height, 64, 64);
		graphics.resetColor();
	}
	
	public void render(GuiGraphics vanilla, int mouseX, int mouseY, float partialTicks)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.pose().pushPose();
		graphics.pose().translate(widget.x, widget.y, 0);
		graphics.pose().scale(widget.microchip().size().scale(), widget.microchip().size().scale(), widget.microchip().size().scale());
		
		this.renderCircuitBg(graphics, mouseX, mouseY, partialTicks);
		this.renderLogicGridSnappingOverlay(graphics, mouseX, mouseY, partialTicks);
		
		graphics.enableBatching();
		widget.wireRenderer().renderWires(graphics, mouseX, mouseY, partialTicks);
		this.renderLogic(graphics, mouseX, mouseY, partialTicks);
		graphics.drawBatches();
		
		this.renderNotes(graphics, mouseX, mouseY, partialTicks);
		
		graphics.pose().popPose();
		
		this.renderTooltip(graphics, mouseX, mouseY, partialTicks);
	}
}
