package net.swedz.little_big_redstone.microchip.object.logic.sequencer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
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
		lines.add(LBR.text().logicConfigTooltipMode(mode));
		lines.add(LBR.text().logicConfigTooltipSequencerDelay(outputDelay));
		lines.add(LBR.text().logicConfigTooltipSequencerAutoReset(autoReset));
		lines.add(LBR.text().logicConfigTooltipSequencerResetPort(resetPort));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public void buildMenu(LogicConfigMenuBuilder builder, int width, int height)
	{
		var modeButton = new AtomicReference<LogicConfigButtonReference<LogicSequencerMode>>();
		
		modeButton.set(builder.addCycleButton(LBR.text().logicConfigButtonLabelMode(), mode.tooltip(), 0, 0, width, 18, false, mode, Arrays.asList(LogicSequencerMode.values()), LogicSequencerMode::label, (value) ->
		{
			mode = value;
			if(modeButton.get() != null)
			{
				modeButton.get().setTooltip(mode.tooltip());
			}
		}));
		
		builder.addSlider(LBR.text().logicConfigButtonLabelSequencerDelay(), Component.empty(), LBR.text().logicConfigButtonTooltipSequencerDelay(), 0, 22, width, 18, 1, 60 * 20, outputDelay, 1, 0, LBRTooltips.TICKS_AND_SECONDS_SLIDER_PARSER::parse, (value) -> outputDelay = value.intValue());
		
		builder.addCheckbox(LBR.text().logicConfigButtonLabelSequencerAutoReset(), LBR.text().logicConfigButtonTooltipSequencerAutoReset(), 0, 22 * 2, autoReset, (value) -> autoReset = value);
		
		builder.addCheckbox(LBR.text().logicConfigButtonLabelSequencerResetPort(), LBR.text().logicConfigButtonTooltipSequencerResetPort(), 0, 22 * 3, resetPort, (value) -> resetPort = value);
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