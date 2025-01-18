package net.swedz.little_big_redstone.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.logic.Logic;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Microchip
{
	private static final int MAX_SIZE = 16 * 8;
	
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
	
	private LogicIndex set(int index, int x, int y, Logic logic)
	{
		LogicIndex original = logics[index];
		logics[index] = new LogicIndex(index, x, y, logic, new LogicOutputPorts());
		dirty = true;
		return original;
	}
	
	public LogicIndex remove(int index)
	{
		LogicIndex original = logics[index];
		logics[index] = null;
		dirty = true;
		return original;
	}
	
	public LogicIndex remove(LogicIndex logic)
	{
		return this.remove(logic.slot());
	}
	
	public boolean canFit(int x, int y, Logic logic)
	{
		// TODO check that this logic piece fits entirely in the board at this position (just check top left and bottom right corners in the bounds)
		for(LogicIndex entry : logics)
		{
			if(entry != null && entry.contains(x, y, logic.size()))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean add(int x, int y, Logic logic)
	{
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
	
	public int size()
	{
		return (int) Arrays.stream(logics).filter(Objects::nonNull).count();
	}
	
	public void loadFrom(Microchip other)
	{
		System.arraycopy(other.logics, 0, logics, 0, logics.length);
		dirty = true;
	}
	
	public void clear()
	{
		Arrays.fill(logics, null);
		dirty = true;
	}
	
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void markClean()
	{
		dirty = false;
	}
}
