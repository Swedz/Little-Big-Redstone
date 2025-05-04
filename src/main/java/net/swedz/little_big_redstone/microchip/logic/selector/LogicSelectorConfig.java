package net.swedz.little_big_redstone.microchip.logic.selector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.api.IntRange;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfigMenuBuilder;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class LogicSelectorConfig extends LogicConfig<LogicSelectorConfig>
{
	public static final MapCodec<LogicSelectorConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicSelectorMode.class).fieldOf("mode").forGetter((config) -> config.mode),
					Codec.intRange(2, 10).fieldOf("outputs").forGetter((config) -> config.outputs)
			)
			.apply(instance, LogicSelectorConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicSelectorConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forLowercaseEnumStream(LogicSelectorMode.class), (config) -> config.mode,
			ByteBufCodecs.INT, (config) -> config.outputs,
			LogicSelectorConfig::new
	);
	
	public LogicSelectorMode mode;
	
	public int outputs;
	
	private LogicSelectorConfig(LogicSelectorMode mode, int outputs)
	{
		this.mode = mode;
		this.outputs = outputs;
	}
	
	public LogicSelectorConfig()
	{
		this(LogicSelectorMode.COUNTER, 2);
	}
	
	@Override
	public void appendHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_MODE).arg(mode, LBRTooltips.SELECTOR_MODE_PARSER));
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_OUTPUTS).arg(outputs));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
		var modeButton = new AtomicReference<LogicConfigButtonReference<LogicSelectorMode>>();
		
		modeButton.set(builder.addCycleButton(LBRText.LOGIC_CONFIG_BUTTON_LABEL_MODE.text(), mode.tooltip().text(), 0, 0, 160, 18, false, mode, Arrays.asList(LogicSelectorMode.values()), (value) -> LBRTooltips.SELECTOR_MODE_PARSER.parse(value).plainCopy(), (value) ->
		{
			mode = value;
			modeButton.get().setTooltip(mode.tooltip().text());
		}));
		
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_LABEL_OUTPUTS.text(), Component.empty(), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_OUTPUTS.text(), 0, 23, 160, 18, this.outputsAllowed().min(), this.outputsAllowed().max(), outputs, 1, 0, true, (value) -> outputs = value.intValue());
	}
	
	@Override
	public void loadFrom(LogicSelectorConfig other)
	{
		mode = other.mode;
		outputs = other.outputs;
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public LogicSelectorConfig copy()
	{
		return new LogicSelectorConfig(mode, outputs);
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(2, 10);
	}
	
	@Override
	public int inputs()
	{
		return mode == LogicSelectorMode.COUNTER ? 2 : outputs;
	}
	
	@Override
	public IntRange outputsAllowed()
	{
		return new IntRange(2, 10);
	}
	
	@Override
	public int outputs()
	{
		return outputs;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(mode, outputs);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicSelectorConfig other && mode == other.mode && outputs == other.outputs);
	}
}
