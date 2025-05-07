package net.swedz.little_big_redstone.item.logicarray.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.ItemContainerContents;

public record LogicArrayTooltipData(ItemContainerContents storage) implements TooltipComponent
{
}
