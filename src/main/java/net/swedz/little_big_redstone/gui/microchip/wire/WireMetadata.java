package net.swedz.little_big_redstone.gui.microchip.wire;

import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.client.model.logic.LogicBakingModelData;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.wire.PortReference;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.tesseract.api.Assert;

public record WireMetadata(
		boolean hovered,
		boolean powered,
		int argb
)
{
	private static int getColor(LogicComponent<?, ?> output, DyeColor fallback)
	{
		return LogicBakingModelData.get(output).getColorSet(output.color().orElse(fallback)).foreground();
	}
	
	public static WireMetadata of(Microchip microchip, DyeColor color, PortReference outputPort, boolean hovered)
	{
		var entry = microchip.components().get(outputPort.slot());
		Assert.notNull(entry);
		var component = entry.component();
		
		return new WireMetadata(
				hovered,
				component.output(outputPort.index()) > 0,
				getColor(component, color)
		);
	}
	
	public static WireMetadata of(Microchip microchip, DyeColor color, Wire wire, boolean hovered)
	{
		return of(microchip, color, wire.output(), hovered);
	}
	
	public static WireMetadata carried(Microchip microchip, DyeColor color, int carriedComponentSlot, LogicComponent<?, ?> component, Wire wire)
	{
		boolean isOutput = wire.output().slot() == carriedComponentSlot;
		var outputLogic = isOutput ? null : microchip.components().get(wire.output().slot());
		var outputLogicComponent = isOutput ? component : outputLogic.component();
		
		return new WireMetadata(
				true,
				outputLogicComponent.output(wire.output().index()) > 0,
				getColor(outputLogicComponent, color)
		);
	}
}
