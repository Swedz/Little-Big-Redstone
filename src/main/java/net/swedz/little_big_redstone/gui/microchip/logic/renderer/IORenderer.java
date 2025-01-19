package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.microchip.logic.io.LogicIO;

public final class IORenderer extends LogicRenderer<LogicIO>
{
	public IORenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, GuiGraphics graphics, LogicIO logic, int x, int y)
	{
		this.renderAllPorts(context, graphics, x, y, logic, 1, 1, 1);
		
		this.renderBackgroundCircle(graphics, x, y, 1, 1, 1);
		
		graphics.blit(LBR.id("textures/logic/%s.png".formatted(logic.config().input ? "io_input" : "io_output")), x, y, 0, 0, 16, 16, 16, 16);
	}
}
