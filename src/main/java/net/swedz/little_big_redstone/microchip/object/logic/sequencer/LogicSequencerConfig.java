package net.swedz.little_big_redstone.microchip.object.logic.sequencer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.LBRTooltips;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfigMenuBuilder;
import net.swedz.tesseract.neoforge.api.range.IntRange;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class LogicSequencerConfig extends LogicConfig<LogicSequencerConfig>
{
	public static final MapCodec<LogicSequencerConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(LogicSequencerMode.class).optionalFieldOf("mode", LogicSequencerMode.WEAK).forGetter((config) -> config.mode),
					Codec.LONG.optionalFieldOf("delay", 20L).forGetter((config) -> config.outputDelay),
					Codec.BOOL.optionalFieldOf("auto_reset", false).forGetter((config) -> config.autoReset),
					Codec.BOOL.optionalFieldOf("reset_port", false).forGetter((config) -> config.resetPort)
			)
			.apply(instance, LogicSequencerConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicSequencerConfig> STREAM_CODEC = StreamCodec.composite(
			CodecHelper.forLowercaseEnumStream(LogicSequencerMode.class), (config) -> config.mode,
			ByteBufCodecs.VAR_LONG, (config) -> config.outputDelay,
			ByteBufCodecs.BOOL, (config) -> config.autoReset,
			ByteBufCodecs.BOOL, (config) -> config.resetPort,
			LogicSequencerConfig::new
	);
	
	public LogicSequencerMode mode;
	
	public long outputDelay;
	
	public boolean autoReset;
	public boolean resetPort;
	
	private LogicSequencerConfig(LogicSequencerMode mode, long outputDelay, boolean autoReset, boolean resetPort)
	{
		this.mode = mode;
		this.outputDelay = outputDelay;
		this.autoReset = autoReset;
		this.resetPort = resetPort;
	}
	
	public LogicSequencerConfig()
	{
		this(LogicSequencerMode.WEAK, 20, false, false);
	}
	
	@Override
	public IntRange inputsAllowed()
	{
		return new IntRange(1, 2);
	}
	
	@Override
	public int inputs()
	{
		return resetPort ? 2 : 1;
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
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_MODE).arg(mode, LBRTooltips.SEQUENCER_MODE_PARSER));
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_SEQUENCER_DELAY).arg(outputDelay, LBRTooltips.TICKS_AND_SECONDS_PARSER));
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_SEQUENCER_AUTO_RESET).arg(autoReset, LBRTooltips.BOOLEAN_YES_NO_PARSER));
		lines.add(line(LBRText.LOGIC_CONFIG_TOOLTIP_SEQUENCER_RESET_PORT).arg(resetPort, LBRTooltips.BOOLEAN_YES_NO_PARSER));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder)
	{
		var modeButton = new AtomicReference<LogicConfigButtonReference<LogicSequencerMode>>();
		
		modeButton.set(builder.addCycleButton(LBRText.LOGIC_CONFIG_BUTTON_LABEL_MODE.text(), mode.tooltip().text(), 0, 0, 160, 18, false, mode, Arrays.asList(LogicSequencerMode.values()), (value) -> LBRTooltips.SEQUENCER_MODE_PARSER.parse(value).plainCopy(), (value) ->
		{
			mode = value;
			modeButton.get().setTooltip(mode.tooltip().text());
		}));
		
		builder.addSlider(LBRText.LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_DELAY.text(), Component.empty(), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_DELAY.text(), 0, 23, 160, 18, 1, 60 * 20, outputDelay, 1, 0, LBRTooltips.TICKS_AND_SECONDS_SLIDER_PARSER::parse, (value) -> outputDelay = value.intValue());
		
		builder.addCheckbox(LBRText.LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_AUTO_RESET.text().withColor(0x3E3E3E), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_AUTO_RESET.text(), 0, 23 * 2, autoReset, (value) -> autoReset = value);
		
		builder.addCheckbox(LBRText.LOGIC_CONFIG_BUTTON_LABEL_SEQUENCER_RESET_PORT.text().withColor(0x3E3E3E), LBRText.LOGIC_CONFIG_BUTTON_TOOLTIP_SEQUENCER_RESET_PORT.text(), 0, 23 * 3, resetPort, (value) -> resetPort = value);
	}
	
	@Override
	protected void internalLoadFrom(LogicSequencerConfig other)
	{
		mode = other.mode;
		outputDelay = other.outputDelay;
		autoReset = other.autoReset;
		resetPort = other.resetPort;
	}
	
	@Override
	public void resetForPickup()
	{
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(mode, outputDelay, autoReset, resetPort);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicSequencerConfig other && mode == other.mode && outputDelay == other.outputDelay && autoReset == other.autoReset && resetPort == other.resetPort);
	}
}