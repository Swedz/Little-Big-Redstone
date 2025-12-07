package net.swedz.little_big_redstone.microchip.object.logic.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponents;
import net.swedz.little_big_redstone.microchip.object.logic.LogicMode;
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

public final class LogicIOConfig extends LogicConfig<LogicIOConfig>
{
	public static final MapCodec<LogicIOConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.BOOL.optionalFieldOf("input", true).forGetter((config) -> config.input),
					Direction.CODEC.optionalFieldOf("direction", Direction.NORTH).forGetter((config) -> config.direction),
					Codec.intRange(1, 15).optionalFieldOf("signal_strength", 1).forGetter((config) -> config.signalStrength),
					CodecHelper.forLowercaseEnum(LogicComparisonMode.class).optionalFieldOf("signal_comparison", LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO).forGetter((config) -> config.signalComparison)
			)
			.apply(instance, (input, direction, signalStrength, precise) -> new LogicIOConfig(true, input, direction, signalStrength, precise)));
	
	public static final StreamCodec<ByteBuf, LogicIOConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, (config) -> config.valid,
			ByteBufCodecs.BOOL, (config) -> config.input,
			Direction.STREAM_CODEC, (config) -> config.direction,
			ByteBufCodecs.INT, (config) -> config.signalStrength,
			CodecHelper.forEnumStream(LogicComparisonMode.class), (config) -> config.signalComparison,
			LogicIOConfig::new
	);
	
	public boolean input;
	
	public Direction direction;
	
	public int signalStrength;
	public LogicComparisonMode signalComparison;
	
	private LogicIOConfig(boolean valid, boolean input, Direction direction, int signalStrength, LogicComparisonMode signalComparison)
	{
		this.valid = valid;
		this.input = input;
		this.direction = direction;
		this.signalStrength = Mth.clamp(signalStrength, 1, 15);
		this.signalComparison = signalComparison;
	}
	
	public LogicIOConfig()
	{
		this(true, true, Direction.NORTH, 1, LogicComparisonMode.GREATER_THAN_OR_EQUAL_TO);
	}
	
	@Override
	protected boolean calculateValidity(LogicComponents components)
	{
		for(var entry : components)
		{
			if(entry.component().config() != this && entry.component().config() instanceof LogicIOConfig entryConfig &&
			   input != entryConfig.input && direction == entryConfig.direction)
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return input ? new IntRange(0, 0) : new IntRange(1, 1);
	}
	
	@Override
	public int inputs()
	{
		return input ? 0 : 1;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return input ? new IntRange(1, 1) : new IntRange(0, 0);
	}
	
	@Override
	public int outputs()
	{
		return input ? 1 : 0;
	}
	
	@Override
	public void appendHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipMode(input ? LogicMode.input() : LogicMode.output()));
		lines.add(LBR.text().logicConfigTooltipDirection(direction));
		if(input)
		{
			lines.add(LBR.text().logicConfigTooltipIoSignalComparison(signalComparison, signalStrength));
		}
		else
		{
			lines.add(LBR.text().logicConfigTooltipIoSignal(signalStrength));
		}
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	private Component signalComparisonTooltip(int signal)
	{
		return switch (signalComparison)
		{
			case LESS_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipIoSignalComparisonModeLessThanOrEqualTo(signal);
			case EQUAL_TO -> LBR.text().logicConfigButtonTooltipIoSignalComparisonModeEqualTo(signal);
			case GREATER_THAN_OR_EQUAL_TO -> LBR.text().logicConfigButtonTooltipIoSignalComparisonModeGreaterThanOrEqualTo(signal);
		};
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder, int width, int height)
	{
		var signalStrengthSlider = new AtomicReference<LogicConfigButtonReference<Double>>();
		var comparisonButton = new AtomicReference<LogicConfigButtonReference<LogicComparisonMode>>();
		
		Runnable updateComparisonButtonTooltip = () ->
		{
			var button = comparisonButton.get();
			if(button != null)
			{
				button.setTooltip(button.isActive() ?
						this.signalComparisonTooltip(signalStrength) :
						LBR.text().logicConfigButtonTooltipIoSignalComparisonOutput(signalStrength));
			}
		};
		
		builder.addCycleButton(LBR.text().logicConfigButtonLabelMode(), LBR.text().logicConfigButtonTooltipIoMode(), 0, 0, width, 18, false, input, List.of(true, false), (value) -> LBRTooltips.INPUT_OUTPUT_PARSER.parse(value).plainCopy(), (value) ->
		{
			input = value;
			signalStrength = input ? 1 : 15;
			if(signalStrengthSlider.get() != null)
			{
				signalStrengthSlider.get().setValue((double) signalStrength);
				signalStrengthSlider.get().setTooltip(input ? LBR.text().logicConfigButtonTooltipIoSignalStrengthInput() : LBR.text().logicConfigButtonTooltipIoSignalStrengthOutput());
			}
			if(comparisonButton.get() != null)
			{
				comparisonButton.get().setActive(input);
			}
			updateComparisonButtonTooltip.run();
		});
		
		builder.addCycleButton(LBR.text().logicConfigButtonLabelDirection(), LBR.text().logicConfigButtonTooltipIoDirection(), 0, 22, width, 18, false, direction, Arrays.asList(Direction.values()), LBRTooltips.DIRECTION_PARSER::parse, (value) -> direction = value);
		
		signalStrengthSlider.set(builder.addSlider(LBR.text().logicConfigButtonLabelIoSignalStrength(), Component.empty(), input ? LBR.text().logicConfigButtonTooltipIoSignalStrengthInput() : LBR.text().logicConfigButtonTooltipIoSignalStrengthOutput(), 18 + 4, 22 * 2, width - 18 - 4, 18, 1, 15, signalStrength, 1, 0, (value) ->
		{
			signalStrength = value.intValue();
			updateComparisonButtonTooltip.run();
		}));
		
		comparisonButton.set(builder.addCycleButton(this.signalComparisonTooltip(signalStrength), 0, 22 * 2, LBR.id("textures/gui/slot_atlas.png"), signalComparison, Arrays.asList(LogicComparisonMode.values()), (value) ->
		{
			signalComparison = value;
			updateComparisonButtonTooltip.run();
		}));
		comparisonButton.get().setActive(input);
		updateComparisonButtonTooltip.run();
	}
	
	@Override
	protected void internalLoadFrom(LogicIOConfig other)
	{
		input = other.input;
		direction = other.direction;
		signalStrength = other.signalStrength;
		signalComparison = other.signalComparison;
	}
	
	@Override
	public void resetForPickup()
	{
		valid = true;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(input, direction, signalStrength, signalComparison);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicIOConfig other && input == other.input && direction == other.direction && signalStrength == other.signalStrength && signalComparison == other.signalComparison);
	}
}