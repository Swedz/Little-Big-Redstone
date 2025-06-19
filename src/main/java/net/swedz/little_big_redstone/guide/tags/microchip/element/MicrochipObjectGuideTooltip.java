package net.swedz.little_big_redstone.guide.tags.microchip.element;

import com.google.common.collect.Lists;
import guideme.document.interaction.GuideTooltip;
import guideme.siteexport.ResourceExporter;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.microchip.object.MicrochipObject;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;

import java.util.List;
import java.util.function.Consumer;

public final class MicrochipObjectGuideTooltip implements GuideTooltip
{
	private final ItemStack                    icon;
	private final List<ClientTooltipComponent> lines;
	
	public MicrochipObjectGuideTooltip(MicrochipObject object)
	{
		icon = object.toStack();
		if(object instanceof LogicEntry logic)
		{
			var component = logic.component();
			List<Component> lines = Lists.newArrayList();
			lines.add(component.type().displayName().withStyle(Style.EMPTY.withUnderlined(true)));
			component.type().tooltip(component, false, true, false).ifPresent((Consumer<List<Component>>) lines::addAll);
			this.lines = lines.stream()
					.<ClientTooltipComponent>map(line -> new ClientTextTooltip(line.getVisualOrderText()))
					.toList();
		}
		else
		{
			lines = List.of();
		}
	}
	
	@Override
	public ItemStack getIcon()
	{
		return icon;
	}
	
	@Override
	public List<ClientTooltipComponent> getLines()
	{
		return lines;
	}
	
	@Override
	public void exportResources(ResourceExporter exporter)
	{
	}
}
