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

public final class LogicOutputPorts
{
	public static final Codec<LogicOutputPorts> CODEC = Codec.list(Targets.CODEC).comapFlatMap(
			(list) -> DataResult.success(new LogicOutputPorts(list.toArray(Targets[]::new))),
			(targets) -> Arrays.asList(targets.targets)
	);
	
	public static final StreamCodec<ByteBuf, LogicOutputPorts> STREAM_CODEC = Targets.STREAM_CODEC.apply(ByteBufCodecs.list()).map(
			(list) -> new LogicOutputPorts(list.toArray(Targets[]::new)),
			(targets) -> Arrays.asList(targets.targets)
	);
	
	private Targets[] targets = new Targets[0];
	
	private LogicOutputPorts(Targets[] targets)
	{
		this.targets = targets;
	}
	
	public LogicOutputPorts()
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
	
	public boolean add(int index, int targetSlot, int targetPortIndex)
	{
		// TODO cannot insert target because array is too small (out of bounds exception...)
		Targets value = targets[index];
		if(value == null)
		{
			value = new Targets();
			targets[index] = value;
		}
		return value.add(targetSlot, targetPortIndex);
	}
	
	public boolean remove(int index, int targetSlot, int targetPortIndex)
	{
		Targets value = targets[index];
		if(value != null)
		{
			return value.remove(targetSlot, targetPortIndex);
		}
		return false;
	}
	
	public boolean removeAll(int index, int targetSlot)
	{
		Targets value = targets[index];
		if(value != null)
		{
			return value.removeAll(targetSlot);
		}
		return false;
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
		public static final Codec<Targets> CODEC = Codec.list(Target.CODEC).comapFlatMap(
				(list) -> DataResult.success(new Targets(Sets.newHashSet(list))),
				(targets) -> Lists.newArrayList(targets.targets)
		);
		
		public static final StreamCodec<ByteBuf, Targets> STREAM_CODEC = Target.STREAM_CODEC.apply(ByteBufCodecs.list()).map(
				(list) -> new Targets(Sets.newHashSet(list)),
				(targets) -> Lists.newArrayList(targets.targets)
		);
		
		private final Set<Target> targets;
		
		private Targets(Set<Target> targets)
		{
			this.targets = targets;
		}
		
		private Targets()
		{
			targets = Sets.newHashSet();
		}
		
		public Set<Target> values()
		{
			return Collections.unmodifiableSet(targets);
		}
		
		public boolean add(int logicSlot, int portIndex)
		{
			return targets.add(new Target(logicSlot, portIndex));
		}
		
		public boolean remove(int logicSlot, int portIndex)
		{
			return targets.remove(new Target(logicSlot, portIndex));
		}
		
		public boolean removeAll(int logicSlot)
		{
			return targets.removeIf((target) -> target.logicSlot() == logicSlot);
		}
	}
	
	public record Target(int logicSlot, int portIndex)
	{
		public static final Codec<Target> CODEC = Codec.list(Codec.INT, 2, 2).xmap(
				(list) -> new Target(list.getFirst(), list.get(1)),
				(target) -> List.of(target.logicSlot(), target.portIndex())
		);
		
		public static final StreamCodec<ByteBuf, Target> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, Target::logicSlot,
				ByteBufCodecs.VAR_INT, Target::portIndex,
				Target::new
		);
	}
}
