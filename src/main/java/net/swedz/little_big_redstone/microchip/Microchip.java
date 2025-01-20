package net.swedz.little_big_redstone.microchip;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.io.LogicIO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Microchip
{
	public static final int MAX_SIZE = 16 * 8;
	
	public static final Bounds BOUNDS = new Bounds(0, 0, 256, 138);
	
	public static final Codec<Microchip> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.list(LogicIndex.CODEC, 0, MAX_SIZE).fieldOf("logics").forGetter(Microchip::values)
			)
			.apply(instance, Microchip::new));
	
	public static final StreamCodec<ByteBuf, Microchip> STREAM_CODEC = StreamCodec.composite(
			LogicIndex.STREAM_CODEC.apply(ByteBufCodecs.list(MAX_SIZE)),
			Microchip::values,
			Microchip::new
	);
	
	private final LogicIndex[] logics = new LogicIndex[MAX_SIZE];
	
	private Set<Direction> redstoneInputs  = Sets.newHashSet();
	private Set<Direction> redstoneOutputs = Sets.newHashSet();
	
	private boolean dirty;
	
	private Microchip(List<LogicIndex> logics)
	{
		for(LogicIndex logic : logics)
		{
			int slot = logic.slot();
			if(this.logics[slot] != null)
			{
				LBR.LOGGER.error("Duplicate slot id found in logic index element, skipping!");
				continue;
			}
			
			this.logics[slot] = logic;
		}
		this.rebuildRedstoneInputOutputCache();
	}
	
	public Microchip()
	{
	}
	
	public List<LogicIndex> values()
	{
		return Arrays.stream(logics).filter(Objects::nonNull).toList();
	}
	
	public LogicIndex get(int index)
	{
		return logics[index];
	}
	
	public boolean has(int index)
	{
		return this.get(index) != null;
	}
	
	private LogicIndex set(int index, int x, int y, LogicComponent logic)
	{
		LogicIndex original = logics[index];
		logics[index] = new LogicIndex(index, x, y, logic, new LogicOutputPorts());
		this.rebuildRedstoneInputOutputCache();
		this.markDirty();
		return original;
	}
	
	public LogicIndex remove(int index)
	{
		LogicIndex original = logics[index];
		logics[index] = null;
		this.rebuildRedstoneInputOutputCache();
		this.markDirty();
		return original;
	}
	
	public LogicIndex remove(LogicIndex logic)
	{
		return this.remove(logic.slot());
	}
	
	public boolean canFit(int x, int y, LogicComponent logic)
	{
		if(!BOUNDS.contains(logic.size().toBounds(x, y)))
		{
			return false;
		}
		for(LogicIndex entry : logics)
		{
			if(entry != null && entry.contains(x, y, logic.size()))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean add(int x, int y, LogicComponent logic)
	{
		if(!this.canFit(x, y, logic))
		{
			return false;
		}
		for(int i = 0; i < logics.length; i++)
		{
			if(logics[i] == null)
			{
				this.set(i, x, y, logic);
				return true;
			}
		}
		return false;
	}
	
	public LogicIndex findAt(int x, int y)
	{
		for(LogicIndex entry : logics)
		{
			if(entry != null && entry.contains(x, y))
			{
				return entry;
			}
		}
		return null;
	}
	
	public LogicSelectedPort findAtPort(int x, int y, boolean input)
	{
		for(LogicIndex entry : logics)
		{
			if(entry != null)
			{
				var size = entry.logic().size();
				int totalPorts = input ? entry.logic().inputs() : entry.logic().outputs();
				for(int index = 0; index < totalPorts; index++)
				{
					if(size.portBounds(entry.x(), entry.y(), input, index, totalPorts).contains(x, y))
					{
						return new LogicSelectedPort(entry, index);
					}
				}
			}
		}
		return null;
	}
	
	public int size()
	{
		return (int) Arrays.stream(logics).filter(Objects::nonNull).count();
	}
	
	public void loadFrom(Microchip other)
	{
		System.arraycopy(other.logics, 0, logics, 0, logics.length);
		this.rebuildRedstoneInputOutputCache();
		this.markDirty();
	}
	
	public void clear()
	{
		Arrays.fill(logics, null);
		this.rebuildRedstoneInputOutputCache();
		this.markDirty();
	}
	
	private void rebuildRedstoneInputOutputCache()
	{
		Set<Direction> inputs = Sets.newHashSet();
		Set<Direction> outputs = Sets.newHashSet();
		for(LogicIndex entry : logics)
		{
			if(entry != null && entry.logic() instanceof LogicIO io)
			{
				var direction = io.config().direction;
				if(inputs.contains(direction) || outputs.contains(direction))
				{
					continue;
				}
				if(io.config().input)
				{
					inputs.add(direction);
				}
				else
				{
					outputs.add(direction);
				}
			}
		}
		redstoneInputs = inputs;
		redstoneOutputs = outputs;
	}
	
	public boolean isFaceListeningForRedstoneInput(Direction direction)
	{
		return redstoneInputs.contains(direction);
	}
	
	public boolean isFaceCapableForRedstoneOutput(Direction direction)
	{
		return redstoneOutputs.contains(direction);
	}
	
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void markDirty()
	{
		dirty = true;
	}
	
	public void markClean()
	{
		dirty = false;
	}
	
	public void tickLogic(LogicContext context)
	{
		for(LogicIndex entry : logics)
		{
			if(entry != null)
			{
				// TODO pull input values from linked outputs
				//entry.logic().processTick(context, new boolean[0]);
			}
		}
	}
}
