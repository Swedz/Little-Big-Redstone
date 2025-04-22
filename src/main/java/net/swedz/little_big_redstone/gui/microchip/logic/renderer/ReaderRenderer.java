package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.ColorConversions;
import net.swedz.little_big_redstone.microchip.logic.reader.LogicReader;

public final class ReaderRenderer extends LogicRenderer<LogicReader>
{
	public ReaderRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, GuiGraphics graphics, LogicReader component, int x, int y)
	{
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		
		int argb = LBRColors.component(component.color().orElse(context.boardColor()));
		float red = ColorConversions.redFloat(argb);
		float green = ColorConversions.greenFloat(argb);
		float blue = ColorConversions.blueFloat(argb);
		this.renderBackgroundCircle(graphics, x, y, red, green, blue);
		
		graphics.setColor(red, green, blue, 1);
		graphics.blit(LBR.id("textures/logic/reader.png"), x, y, 0, 0, 16, 16, 16, 16);
		graphics.setColor(1, 1, 1, 1);
	}
}
