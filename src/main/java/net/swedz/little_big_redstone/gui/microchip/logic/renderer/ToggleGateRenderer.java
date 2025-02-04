package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.microchip.logic.toggle.ToggleGate;

public final class ToggleGateRenderer extends LogicRenderer<ToggleGate>
{
	public ToggleGateRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, GuiGraphics graphics, ToggleGate component, int x, int y)
	{
		var size = component.size();
		
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		
		this.renderBackground(graphics, x, y, size, 1, 1, 1);
		
		int centerX = x + size.centerX() - 8;
		int centerY = y + size.centerY() - 8;
		graphics.blit(LBR.id("textures/logic/toggle_gate_%s.png".formatted(component.output() ? "on" : "off")), centerX, centerY, 0, 0, 16, 16, 16, 16);
	}
}
