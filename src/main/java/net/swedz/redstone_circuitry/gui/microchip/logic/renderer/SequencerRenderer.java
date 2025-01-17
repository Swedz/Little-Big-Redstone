package net.swedz.redstone_circuitry.gui.microchip.logic.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.swedz.redstone_circuitry.gui.microchip.logic.LogicRenderer;
import net.swedz.redstone_circuitry.gui.microchip.logic.LogicRendererProvider;
import net.swedz.redstone_circuitry.microchip.logic.sequencer.LogicSequencer;

public final class SequencerRenderer extends LogicRenderer<LogicSequencer>
{
	public SequencerRenderer(LogicRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(GuiGraphics graphics, LogicSequencer logic, int x, int y)
	{
		// TODO
	}
}
