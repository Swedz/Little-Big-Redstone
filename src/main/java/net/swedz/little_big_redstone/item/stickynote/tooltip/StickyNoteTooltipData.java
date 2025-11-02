package net.swedz.little_big_redstone.item.stickynote.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;

public record StickyNoteTooltipData(
		StickyNoteView note
) implements TooltipComponent
{
}
