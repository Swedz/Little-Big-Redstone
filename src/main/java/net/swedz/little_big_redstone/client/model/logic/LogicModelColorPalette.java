package net.swedz.little_big_redstone.client.model.logic;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.tesseract.api.Assert;

import java.util.Collections;
import java.util.Map;

public final class LogicModelColorPalette
{
	public static final Codec<LogicModelColorPalette> CODEC = Codec
			.unboundedMap(DyeColor.CODEC, LogicModelColorSet.CODEC)
			.xmap(
					LogicModelColorPalette::new,
					(palette) -> palette.map
			);
	
	private final Map<DyeColor, LogicModelColorSet> map;
	
	private LogicModelColorPalette(Map<DyeColor, LogicModelColorSet> map)
	{
		this.map = Collections.unmodifiableMap(map);
	}
	
	public LogicModelColorSet getColorSet(DyeColor color)
	{
		return map.getOrDefault(color, LogicModelColorSet.DEFAULT);
	}
	
	public LogicModelColorSet getColorSet(LogicComponent<?, ?> component, DyeColor fallback)
	{
		return this.getColorSet(component.color().orElse(fallback));
	}
	
	public static Builder builder()
	{
		return new Builder();
	}
	
	public static final class Builder
	{
		private final Map<DyeColor, LogicModelColorSet> map = Maps.newHashMap();
		
		private Builder()
		{
		}
		
		public Builder foregroundColor(DyeColor dyeColor, int color)
		{
			Assert.noneNull(dyeColor);
			map.compute(dyeColor, (__, colorSet) ->
			{
				if(colorSet == null)
				{
					colorSet = LogicModelColorSet.DEFAULT;
				}
				return new LogicModelColorSet(color, colorSet.background());
			});
			return this;
		}
		
		public Builder backgroundColor(DyeColor dyeColor, int color)
		{
			Assert.noneNull(dyeColor);
			map.compute(dyeColor, (__, colorSet) ->
			{
				if(colorSet == null)
				{
					colorSet = LogicModelColorSet.DEFAULT;
				}
				return new LogicModelColorSet(colorSet.foreground(), color);
			});
			return this;
		}
		
		public LogicModelColorPalette build()
		{
			return new LogicModelColorPalette(map);
		}
	}
}
