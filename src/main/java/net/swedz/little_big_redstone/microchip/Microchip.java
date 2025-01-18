package net.swedz.little_big_redstone.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.swedz.little_big_redstone.microchip.logic.Logic;
import net.swedz.tesseract.neoforge.api.Assert;

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
	
	private final LogicIndex[] logics = new LogicIndex[MAX_SIZE];
	
	private Microchip(List<LogicIndex> logics)
	{
		Assert.that(logics.size() == this.logics.length);
		System.arraycopy(logics.toArray(LogicIndex[]::new), 0, this.logics, 0, logics.size());
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
	
	private LogicIndex set(int index, Logic logic)
	{
		LogicIndex original = logics[index];
		logics[index] = new LogicIndex(index, logic, new LogicOutputs());
		return original;
	}
	
	public LogicIndex remove(int index)
	{
		return this.set(index, null);
	}
	
	public LogicIndex remove(LogicIndex logic)
	{
		return this.remove(logic.slot());
	}
	
	public boolean add(Logic logic)
	{
		for(int i = 0; i < logics.length; i++)
		{
			if(logics[i] == null)
			{
				this.set(i, logic);
				return true;
			}
		}
		return false;
	}
	
	public int size()
	{
		return (int) Arrays.stream(logics).filter(Objects::nonNull).count();
	}
}
