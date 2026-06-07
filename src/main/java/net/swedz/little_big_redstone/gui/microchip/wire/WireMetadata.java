package net.swedz.little_big_redstone.gui.microchip.wire;

import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.client.model.logic.LogicItemModel;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.wire.PortReference;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.tesseract.api.Assert;

import java.util.Optional;

public record WireMetadata(
		boolean hovered,
		boolean powered,
		int argb
)
{
	private static int getColor(LogicType output, Optional<DyeColor> color, DyeColor fallback)
	{
		return LogicItemModel.get(output).colorPalette().getColorSet(color, fallback).foreground();
	}
	
	public static WireMetadata of(Microchip microchip, DyeColor color, PortReference outputPort, boolean hovered)
	{
		var entry = microchip.components().get(outputPort.slot());
		Assert.notNull(entry);
		var component = entry.component();
		
		return new WireMetadata(
				hovered,
				component.output(outputPort.index()) > 0,
				getColor(component.type(), component.color(), color)
		);
	}
	
	public static WireMetadata of(Microchip microchip, DyeColor color, Wire wire, boolean hovered)
	{
		return of(microchip, color, wire.output(), hovered);
	}
	
	public static WireMetadata carried(Microchip microchip, DyeColor fallbackColor, int carriedComponentSlot, LogicType type, Optional<DyeColor> logicColor, Wire wire)
	{
		boolean isOutput = wire.output().slot() == carriedComponentSlot;
		var outputLogic = isOutput ? null : microchip.components().get(wire.output().slot());
		var wireColor = isOutput ? logicColor : outputLogic.color();
		
		return new WireMetadata(
				true,
				outputLogic != null && outputLogic.component().output(wire.output().index()) > 0,
				getColor(type, wireColor, fallbackColor)
		);
	}
}
