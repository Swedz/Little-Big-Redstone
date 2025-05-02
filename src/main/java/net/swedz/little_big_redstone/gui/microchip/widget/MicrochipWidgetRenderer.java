package net.swedz.little_big_redstone.gui.microchip.widget;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.LogicEntry;

import java.util.List;
import java.util.function.Consumer;

public final class MicrochipWidgetRenderer
{
	private static final ResourceLocation SHADOW_HOVER_OVERLAY = LBR.id("textures/gui/container/microchip/shadow_hover_overlay.png");
	private static final ResourceLocation CIRCUIT_BACKGROUND   = LBR.id("textures/gui/container/microchip/circuit_background.png");
	
	private final MicrochipWidget widget;
	
	public MicrochipWidgetRenderer(MicrochipWidget widget)
	{
		this.widget = widget;
	}
	
	private void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		if(widget.context().shouldRenderTooltip())
		{
			var carried = widget.menu().getCarried();
			if(carried.isEmpty())
			{
				var component = widget.context().logic().component();
				List<Component> lines = Lists.newArrayList();
				lines.add(component.type().displayName().withStyle(Style.EMPTY.withUnderlined(true)));
				component.type().tooltip(component, false, true, false).ifPresent((Consumer<List<Component>>) lines::addAll);
				graphics.renderComponentTooltip(Minecraft.getInstance().font, lines, mouseX, mouseY);
			}
		}
	}
	
	private void renderLogic(GuiGraphics graphics, LogicEntry entry)
	{
		var context = LogicRenderer.Context.create(widget.menu().color(), entry.component(), false, widget.hasSelectedPort(), widget.menu().getCarried().is(LBRItems.REDSTONE_BIT.asItem()));
		LogicRenderers.render(context, graphics, entry.component(), entry.x(), entry.y());
		
		if(widget.microchip().isDebug())
		{
			graphics.drawString(Minecraft.getInstance().font, "#" + entry.slot(), entry.x(), entry.y() - 8, 0xFFFFFF);
			
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
				graphics.drawString(Minecraft.getInstance().font, Integer.toString(ind), entry.x(), entry.y() + (i * 8), 0xFFFFFF);
			}
		}
	}
	
	private void renderLogic(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
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
			this.renderLogic(graphics, widget.context().logic());
		}
	}
	
	private void renderLogicGridSnappingOverlay(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		var carried = widget.menu().getCarried();
		var size = widget.microchip().size();
		
		if(Screen.hasControlDown() && carried.has(LBRComponents.LOGIC) && widget.isMouseOver(mouseX, mouseY))
		{
			var component = carried.get(LBRComponents.LOGIC);
			int boardX = size.boardX(widget.toLocalX(mouseX));
			int boardY = size.boardY(widget.toLocalY(mouseY));
			int logicX = MicrochipScreen.getGridSnappedCoord(boardX - component.size().centerX() + 8);
			int logicY = MicrochipScreen.getGridSnappedCoord(boardY - component.size().centerY() + 8);
			
			graphics.setColor(1, 1, 1, MicrochipScreen.getPulsingAlpha(partialTicks));
			graphics.fill(0, logicY, size.bounds().width(), logicY + 1, 0xFFFFFFFF);
			graphics.fill(0, logicY + component.size().heightPixels() - 1, size.bounds().width(), logicY + component.size().heightPixels(), 0xFFFFFFFF);
			graphics.fill(logicX, 0, logicX + 1, size.bounds().height(), 0xFFFFFFFF);
			graphics.fill(logicX + component.size().widthPixels() - 1, 0, logicX + component.size().widthPixels(), size.bounds().height(), 0xFFFFFFFF);
			GuiGraphicsHelper.resetColor(graphics);
		}
	}
	
	private void renderCircuitBg(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		GuiGraphicsHelper.setColor(graphics, LBRColors.circuitboard(widget.menu().color()));
		graphics.blit(CIRCUIT_BACKGROUND, 0, 0, 0, 0, widget.width, widget.height, 64, 64);
		GuiGraphicsHelper.resetColor(graphics);
	}
	
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(widget.x, widget.y, 0);
		graphics.pose().scale(widget.microchip().size().scale(), widget.microchip().size().scale(), widget.microchip().size().scale());
		
		this.renderCircuitBg(graphics, mouseX, mouseY, partialTicks);
		this.renderLogicGridSnappingOverlay(graphics, mouseX, mouseY, partialTicks);
		this.renderLogic(graphics, mouseX, mouseY, partialTicks);
		widget.wires.renderWires(graphics, mouseX, mouseY, partialTicks);
		
		graphics.pose().popPose();
		
		this.renderTooltip(graphics, mouseX, mouseY, partialTicks);
	}
}
