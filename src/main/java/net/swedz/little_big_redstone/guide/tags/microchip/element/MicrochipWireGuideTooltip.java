package net.swedz.little_big_redstone.guide.tags.microchip.element;

import com.google.common.collect.Lists;
import guideme.document.interaction.GuideTooltip;
import guideme.siteexport.ResourceExporter;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;

import java.util.List;

public final class MicrochipWireGuideTooltip implements GuideTooltip
{
	private final ItemStack                    icon;
	private final List<ClientTooltipComponent> lines;
	
	public MicrochipWireGuideTooltip(int signal)
	{
		this.icon = new ItemStack(LBRItems.REDSTONE_BIT);
		
		List<Component> lines = Lists.newArrayList();
		lines.add(icon.getHoverName().copy().withStyle(Style.EMPTY.withUnderlined(true)));
		lines.add(LBR.text().logicConfigTooltipSignal(signal));
		this.lines = lines.stream()
				.<ClientTooltipComponent>map(line -> new ClientTextTooltip(line.getVisualOrderText()))
				.toList();
	}
	
	@Override
	public ItemStack getIcon()
	{
		return new ItemStack(LBRItems.REDSTONE_BIT);
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
