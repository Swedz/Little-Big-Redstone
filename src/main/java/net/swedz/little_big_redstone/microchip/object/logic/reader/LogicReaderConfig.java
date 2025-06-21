package net.swedz.little_big_redstone.microchip.object.logic.reader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfigMenuBuilder;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;
import net.swedz.tesseract.neoforge.tooltip.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static net.swedz.little_big_redstone.LBRTextLine.*;
import static net.swedz.little_big_redstone.LBRTooltips.*;

public final class LogicReaderConfig extends LogicConfig<LogicReaderConfig>
{
	public static final MapCodec<LogicReaderConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicReaderMode.class).optionalFieldOf("mode", LogicReaderMode.ITEM).forGetter((config) -> config.mode),
					Direction.CODEC.optionalFieldOf("direction", Direction.NORTH).forGetter((config) -> config.direction),
					Codec.FLOAT.optionalFieldOf("fill_threshold", 0.5f).forGetter((config) -> config.fillThreshold)
			)
			.apply(instance, LogicReaderConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicReaderConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forLowercaseEnumStream(LogicReaderMode.class), (config) -> config.mode,
			Direction.STREAM_CODEC, (config) -> config.direction,
			ByteBufCodecs.FLOAT, (config) -> config.fillThreshold,
			LogicReaderConfig::new
	);
	
	public LogicReaderMode mode;
	
	public Direction direction;
	
	public float fillThreshold;
	
	private LogicReaderConfig(LogicReaderMode mode, Direction direction, float fillThreshold)
	{
		this.mode = mode;
		this.direction = direction;
		this.fillThreshold = fillThreshold;
	}
	
	public LogicReaderConfig()
	{
		this(LogicReaderMode.ITEM, Direction.NORTH, 0.5f);
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
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_MODE).arg(mode, LBRTooltips.READER_MODE_PARSER));
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_DIRECTION).arg(direction, LBRTooltips.DIRECTION_PARSER));
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_READER_FILL_MIN).arg(fillThreshold, 0, Parser.FLOAT_PERCENTAGE.withStyle(HIGHLIGHT_STYLE)));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
		builder.addCycleButton(LBRText.LOGIC_CONFIG_BUTTON_LABEL_MODE.text(), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_READER_MODE.text(), 0, 0, 160, 18, false, mode, Arrays.asList(LogicReaderMode.values()), (value) -> LBRTooltips.READER_MODE_PARSER.parse(value).plainCopy(), (value) -> mode = value);
		
		builder.addCycleButton(LBRText.LOGIC_CONFIG_BUTTON_LABEL_DIRECTION.text(), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_READER_DIRECTION.text(), 0, 23, 160, 18, false, direction, Arrays.asList(Direction.values()), LBRTooltips.DIRECTION_PARSER::parse, (value) -> direction = value);
		
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_LABEL_READER_FILL_THRESHOLD.text(), Component.literal("%"), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_READER_FILL_THRESHOLD.text(), 0, 23 * 2, 160, 18, 0, 100, fillThreshold * 100, 1, 0, (value) -> fillThreshold = (float) (value / 100f));
	}
	
	@Override
	protected void internalLoadFrom(LogicReaderConfig other)
	{
		mode = other.mode;
		direction = other.direction;
		fillThreshold = other.fillThreshold;
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(mode, direction, fillThreshold);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicReaderConfig other && mode == other.mode && direction == other.direction && fillThreshold == other.fillThreshold);
	}
}
