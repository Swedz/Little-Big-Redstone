package net.swedz.little_big_redstone.gui.microchip.wire;

import net.swedz.tesseract.neoforge.api.Bounds;

import java.util.List;

public record WirePathKey(
		int startX,
		int startY,
		int endX,
		int endY,
		List<Bounds> avoidBounds
)
{
}
