package net.swedz.little_big_redstone.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.swedz.little_big_redstone.LBRClientRenderPipelines;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.microchip.object.logic.sequencer.LogicSequencer;
import net.swedz.little_big_redstone.microchip.object.logic.sequencer.LogicSequencerConfig;

public final class SequencerRenderer extends LogicRenderer<LogicSequencer, LogicSequencerConfig>
{
	@Override
	public void render(Context context, GuiGraphicsExtractor graphics, LogicSequencer component, int x, int y)
	{
		var size = component.size();
		
		this.renderAllPorts(context, graphics, x, y, component);
		this.renderBackground(context, graphics, x, y, component);
		
		int fillWidth = (int) ((size.widthPixels() - 4) * component.processedPercentage());
		int fillHeight = size.heightPixels() - 4;
		
		graphics.blitSprite(
				LBRClientRenderPipelines.LOGIC_SCANLINE,
				context.getTexture("progress"),
				x + 2,
				y + 2,
				fillWidth,
				fillHeight,
				context.foregroundColor()
		);
	}
}
