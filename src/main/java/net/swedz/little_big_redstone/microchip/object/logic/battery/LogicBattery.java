package net.swedz.little_big_redstone.microchip.object.logic.battery;

import com.google.common.base.Objects;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContextAccess;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.Optional;

public final class LogicBattery extends LogicComponent<LogicBattery, LogicBatteryConfig>
{
	public static final MapCodec<LogicBattery> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicBatteryConfig.CODEC.fieldOf("config").forGetter(LogicBattery::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicBattery::color)
			)
			.apply(instance, LogicBattery::new));
	
	public static final StreamCodec<ByteBuf, LogicBattery> STREAM_CODEC = StreamCodec.composite(
			LogicBatteryConfig.STREAM_CODEC, LogicBattery::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicBattery::color,
			LogicBattery::new
	);
	
	private LogicBattery(LogicBatteryConfig config, Optional<DyeColor> color)
	{
		super(config, color);
	}
	
	private LogicBattery(Optional<DyeColor> color)
	{
		super(color);
	}
	
	public LogicBattery()
	{
		this(Optional.empty());
	}
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.BATTERY.get();
	}
	
	@Override
	protected void processTickInternal(LogicContextAccess context, int[] inputs)
	{
	}
	
	@Override
	protected int outputInternal(int index)
	{
		return config.signalStrength();
	}
	
	public int output()
	{
		return this.output(0);
	}
	
	@Override
	protected void internalLoadFrom(LogicBattery other)
	{
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.type(), config, color);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicBattery other && Objects.equal(config, other.config) && Objects.equal(color, other.color));
	}
}