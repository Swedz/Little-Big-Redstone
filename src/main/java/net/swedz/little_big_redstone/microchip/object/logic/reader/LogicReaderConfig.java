package net.swedz.little_big_redstone.microchip.object.logic.reader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicComparisonMode;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfigMenuBuilder;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class LogicReaderConfig extends LogicConfig<LogicReaderConfig>
{
	public static final MapCodec<LogicReaderConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicReaderMode.class).optionalFieldOf("mode", LogicReaderMode.ITEM).forGetter((config) -> config.mode),
					Direction.CODEC.optionalFieldOf("direction", Direction.NORTH).forGetter((config) -> config.direction),
					Codec.FLOAT.optionalFieldOf("fill_threshold", 0.5f).forGetter((config) -> config.fillThreshold),
					CodecHelper.forLowercaseEnum(LogicComparisonMode.class).optionalFieldOf("comparison", LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO).forGetter((config) -> config.comparison)
			)
			.apply(instance, LogicReaderConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicReaderConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forLowercaseEnumStream(LogicReaderMode.class), (config) -> config.mode,
			Direction.STREAM_CODEC, (config) -> config.direction,
			ByteBufCodecs.FLOAT, (config) -> config.fillThreshold,
			CodecHelper.forEnumStream(LogicComparisonMode.class), (config) -> config.comparison,
			LogicReaderConfig::new
	);
	
	public LogicReaderMode mode;
	
	public Direction direction;
	
	public float fillThreshold;
	public LogicComparisonMode comparison;
	
	private LogicReaderConfig(LogicReaderMode mode, Direction direction, float fillThreshold, LogicComparisonMode comparison)
	{
		this.mode = mode;
		this.direction = direction;
		this.fillThreshold = fillThreshold;
		this.comparison = comparison;
	}
	
	public LogicReaderConfig()
	{
		this(LogicReaderMode.ITEM, Direction.NORTH, 0.5f, LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO);
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(0, 0);
	}
	
	@Override
	public int inputs()
	{
		return 0;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int outputs()
	{
		return 1;
	}
	
	@Override
	public void appendHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipMode(mode));
		lines.add(LBR.text().logicConfigTooltipDirection(direction));
		lines.add(LBR.text().logicConfigTooltipReaderFill(comparison, fillThreshold));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	private MutableComponent comparisonTooltip(float threshold)
	{
		return switch (comparison)
		{
			case LESS_THAN_OR_EQUAL_TO ->
					LBR.text().logicConfigButtonTooltipReaderThresholdComparisonModeLessThanOrEqualTo(threshold);
			case EQUAL_TO -> LBR.text().logicConfigButtonTooltipReaderThresholdComparisonModeEqualTo(threshold);
			case GREATER_THAN_OR_EQUAL_TO ->
					LBR.text().logicConfigButtonTooltipReaderThresholdComparisonModeGreaterThanOrEqualTo(threshold);
		};
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder, int width, int height)
	{
		var comparisonButton = new AtomicReference<LogicConfigButtonReference<LogicComparisonMode>>();
		
		Runnable updateComparisonButtonTooltip = () ->
		{
			var button = comparisonButton.get();
			if(button != null)
			{
				button.setTooltip(this.comparisonTooltip(fillThreshold));
			}
		};
		
		builder.addCycleButton(LBR.text().logicConfigButtonLabelMode(), LBR.text().logicConfigButtonTooltipReaderMode(), 0, 0, width, 18, false, mode, Arrays.asList(LogicReaderMode.values()), LogicReaderMode::label, (value) -> mode = value);
		
		builder.addCycleButton(LBR.text().logicConfigButtonLabelDirection(), LBR.text().logicConfigButtonTooltipReaderDirection(), 0, 22, width, 18, false, direction, Arrays.asList(Direction.values()), LBRTooltips.DIRECTION_PARSER::parse, (value) -> direction = value);
		
		builder.addSlider(LBR.text().logicConfigButtonLabelReaderFillThreshold(), Component.literal("%"), LBR.text().logicConfigButtonTooltipReaderFillThreshold(), 18 + 4, 22 * 2, width - 18 - 4, 18, 0, 100, fillThreshold * 100, 1, 0, (value) ->
		{
			fillThreshold = (float) (value / 100f);
			updateComparisonButtonTooltip.run();
		});
		
		comparisonButton.set(builder.addCycleButton(this.comparisonTooltip(fillThreshold), 0, 22 * 2, LBR.id("textures/gui/slot_atlas.png"), comparison, Arrays.asList(LogicComparisonMode.values()), (value) ->
		{
			comparison = value;
			updateComparisonButtonTooltip.run();
		}));
		updateComparisonButtonTooltip.run();
	}
	
	@Override
	protected void internalLoadFrom(LogicReaderConfig other)
	{
		mode = other.mode;
		direction = other.direction;
		fillThreshold = other.fillThreshold;
		comparison = other.comparison;
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(mode, direction, fillThreshold, comparison);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicReaderConfig other && mode == other.mode && direction == other.direction && fillThreshold == other.fillThreshold && comparison == other.comparison);
	}
}