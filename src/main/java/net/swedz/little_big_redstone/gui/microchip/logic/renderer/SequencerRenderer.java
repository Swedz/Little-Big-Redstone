package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRendererProvider;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.logic.sequencer.LogicSequencer;

public final class SequencerRenderer extends LogicRenderer<LogicSequencer>
{
	public SequencerRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(Context context, GuiGraphics graphics, LogicSequencer component, int x, int y)
	{
		var size = component.size();
		
		this.renderAllPorts(context, graphics, x, y, component, 1, 1, 1);
		this.renderBackground(context, graphics, x, y, component);
		
		GuiGraphicsHelper.setColor(graphics, context.foregroundColor());
		int fillWidth = size.widthPixels() - 4;
		fillWidth = (int) (fillWidth * component.processedPercentage());
		graphics.blit(context.getTexture("progress"), x + 2, y + 2, 0, 0, fillWidth, size.heightPixels() - 4, 12, 12);
		GuiGraphicsHelper.resetColor(graphics);
	}
}
