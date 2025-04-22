package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.ColorConversions;
import net.swedz.little_big_redstone.microchip.logic.io.LogicIO;

public final class IORenderer extends LogicRenderer<LogicIO>
{
	public IORenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, GuiGraphics graphics, LogicIO component, int x, int y)
	{
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		
		int argb = LBRColors.component(component.color().orElse(context.boardColor()));
		float red = ColorConversions.redFloat(argb);
		float green = ColorConversions.greenFloat(argb);
		float blue = ColorConversions.blueFloat(argb);
		this.renderBackgroundCircle(graphics, x, y, red, green, blue);
		
		graphics.setColor(red, green, blue, 1);
		graphics.blit(LBR.id("textures/logic/%s.png".formatted(component.config().input ? "io_input" : "io_output")), x, y, 0, 0, 16, 16, 16, 16);
		graphics.setColor(1, 1, 1, 1);
		
		if(!component.config().isValid())
		{
			this.renderInvalidOverlay(graphics, x, y, component.size());
		}
	}
}
