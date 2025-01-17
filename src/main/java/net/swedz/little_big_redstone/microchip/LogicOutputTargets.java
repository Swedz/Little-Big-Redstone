package net.swedz.little_big_redstone.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class LogicOutputTargets
{
	public static final Codec<LogicOutputTargets> CODEC = Codec.list(Codec.INT).comapFlatMap(
			(list) -> DataResult.success(new LogicOutputTargets(list.toArray(Integer[]::new))),
			(targets) -> Arrays.asList(targets.targets)
	);
	
	public static final StreamCodec<ByteBuf, LogicOutputTargets> STREAM_CODEC = ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()).map(
			(list) -> new LogicOutputTargets(list.toArray(Integer[]::new)),
			(targets) -> Arrays.asList(targets.targets)
	);
	
	private Integer[] targets = new Integer[0];
	
	private LogicOutputTargets(Integer[] targets)
	{
		this.targets = targets;
	}
	
	public LogicOutputTargets()
	{
	}
	
	public List<Integer> values()
	{
		return Arrays.stream(targets).filter(Objects::nonNull).toList();
	}
	
	public Integer get(int index)
	{
		return targets[index];
	}
	
	public boolean has(int index)
	{
		return this.get(index) != null;
	}
	
	public void set(int index, Integer target)
	{
		targets[index] = target;
	}
	
	public void unset(int index)
	{
		this.set(index, null);
	}
	
	public void setSize(int size)
	{
		targets = Arrays.copyOf(targets, size);
	}
}
