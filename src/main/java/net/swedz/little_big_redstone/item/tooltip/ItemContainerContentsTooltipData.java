package net.swedz.little_big_redstone.item.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.ItemContainerContents;

public record ItemContainerContentsTooltipData(
		ItemContainerContents storage,
		int maxColumns, int maxRows,
		boolean showExtraSlot
) implements TooltipComponent
{
	public ItemContainerContentsTooltipData(ItemContainerContents storage, int maxColumns, boolean showExtraSlot)
	{
		this(storage, maxColumns, 0, showExtraSlot);
	}
}
