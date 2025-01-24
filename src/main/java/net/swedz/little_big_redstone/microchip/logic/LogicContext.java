package net.swedz.little_big_redstone.microchip.logic;

import com.google.common.collect.Lists;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;

import java.util.Collections;
import java.util.List;

public final class LogicContext
{
	private final MicrochipBlockEntity blockEntity;
	
	private final List<LogicEntry> dirtyEntries = Lists.newArrayList();
	
	public LogicContext(MicrochipBlockEntity blockEntity)
	{
		this.blockEntity = blockEntity;
	}
	
	public <A extends MicrochipAwareness<A>> A awareness(AwarenessType<A> type)
	{
		return blockEntity.microchip().awarenesses().get(type);
	}
	
	public boolean isDirty()
	{
		return !dirtyEntries.isEmpty();
	}
	
	public void markDirty(LogicComponent component)
	{
		for(var entry : blockEntity.microchip().components())
		{
			if(entry.component() == component)
			{
				dirtyEntries.add(entry);
			}
		}
	}
	
	public List<LogicEntry> getDirtyEntries()
	{
		return Collections.unmodifiableList(dirtyEntries);
	}
}
