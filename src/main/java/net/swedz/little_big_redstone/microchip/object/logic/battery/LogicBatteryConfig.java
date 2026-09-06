package net.swedz.little_big_redstone.microchip.object.logic.battery;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuProvider;
import net.swedz.tesseract.neoforge.api.range.IntRange;

import java.util.List;

public record LogicBatteryConfig(
		int signalStrength
) implements LogicConfig
{
	public static final LogicBatteryConfig DEFAULT = new LogicBatteryConfig(
			1
	);
	
	public static final MapCodec<LogicBatteryConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Codec.intRange(0, 15).optionalFieldOf("signal_strength", DEFAULT.signalStrength()).forGetter(LogicBatteryConfig::signalStrength)
			)
			.apply(instance, LogicBatteryConfig::new));
	
	public static final StreamCodec<ByteBuf, LogicBatteryConfig> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, LogicBatteryConfig::signalStrength,
			LogicBatteryConfig::new
	);
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.BATTERY.get();
	}
	
	@Override
	public IntRange inputPortsAllowed()
	{
		return new IntRange(0, 0);
	}
	
	@Override
	public int inputPorts()
	{
		return 0;
	}
	
	@Override
	public IntRange outputPortsAllowed()
	{
		return new IntRange(1, 1);
	}
	
	@Override
	public int outputPorts()
	{
		return 1;
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpBattery());
	}
	
	@Override
	public void appendConfigHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicConfigTooltipSignal(signalStrength));
	}
	
	@Override
	public boolean hasMenu()
	{
		return true;
	}
	
	@Override
	public LogicConfigMenuProvider getMenuProvider()
	{
		return new LogicBatteryConfigMenuProvider(this);
	}
}