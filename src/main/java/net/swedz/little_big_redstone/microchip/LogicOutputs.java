package net.swedz.little_big_redstone.microchip;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class LogicOutputs
{
	public static final Codec<LogicOutputs> CODEC = Codec.list(Targets.CODEC).comapFlatMap(
			(list) -> DataResult.success(new LogicOutputs(list.toArray(Targets[]::new))),
			(targets) -> Arrays.asList(targets.targets)
	);
	
	public static final StreamCodec<ByteBuf, LogicOutputs> STREAM_CODEC = Targets.STREAM_CODEC.apply(ByteBufCodecs.list()).map(
			(list) -> new LogicOutputs(list.toArray(Targets[]::new)),
			(targets) -> Arrays.asList(targets.targets)
	);
	
	private Targets[] targets = new Targets[0];
	
	private LogicOutputs(Targets[] targets)
	{
		this.targets = targets;
	}
	
	public LogicOutputs()
	{
	}
	
	public List<Targets> values()
	{
		return Arrays.stream(targets).filter(Objects::nonNull).toList();
	}
	
	public Targets get(int index)
	{
		return targets[index];
	}
	
	public boolean has(int index)
	{
		return this.get(index) != null;
	}
	
	public void add(int index, int target)
	{
		Targets value = targets[index];
		if(value == null)
		{
			value = new Targets();
			targets[index] = value;
		}
		value.add(target);
	}
	
	public void remove(int index, int target)
	{
		Targets value = targets[index];
		if(value != null)
		{
			value.remove(target);
		}
	}
	
	public void removeAll(int index)
	{
		targets[index] = null;
	}
	
	public void setSize(int size)
	{
		targets = Arrays.copyOf(targets, size);
	}
	
	public static final class Targets
	{
		public static final Codec<Targets> CODEC = Codec.list(Codec.INT).comapFlatMap(
				(list) -> DataResult.success(new Targets(Sets.newHashSet(list))),
				(targets) -> Lists.newArrayList(targets.targets)
		);
		
		public static final StreamCodec<ByteBuf, Targets> STREAM_CODEC = ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()).map(
				(list) -> new Targets(Sets.newHashSet(list)),
				(targets) -> Lists.newArrayList(targets.targets)
		);
		
		private final Set<Integer> targets;
		
		private Targets(Set<Integer> targets)
		{
			this.targets = targets;
		}
		
		private Targets()
		{
			targets = Sets.newHashSet();
		}
		
		public Set<Integer> values()
		{
			return Collections.unmodifiableSet(targets);
		}
		
		public boolean add(int target)
		{
			return targets.add(target);
		}
		
		public boolean remove(int target)
		{
			return targets.remove(target);
		}
	}
}
