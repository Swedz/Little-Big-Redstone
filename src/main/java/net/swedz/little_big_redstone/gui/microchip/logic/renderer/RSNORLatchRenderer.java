package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.logic.latch.rs.RSNORLatch;

public final class RSNORLatchRenderer extends LogicRenderer<RSNORLatch>
{
	public RSNORLatchRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, GuiGraphics graphics, RSNORLatch component, int x, int y)
	{
		var size = component.size();
		
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		
		var color = component.color().orElse(context.boardColor());
		int foregroundColor = LBRColors.componentForeground(color);
		int backgroundColor = LBRColors.componentBackground(color);
		this.renderBackground(graphics, x, y, size, foregroundColor, backgroundColor);
		
		GuiGraphicsHelper.setColor(graphics, foregroundColor);
		int centerX = x + size.centerX() - 8;
		int centerY = y + size.centerY() - 8;
		graphics.blit(LBR.id("textures/logic/rs_nor_latch_%s.png".formatted(component.output() ? "on" : "off")), centerX, centerY, 0, 0, 16, 16, 16, 16);
		GuiGraphicsHelper.resetColor(graphics);
	}
}
