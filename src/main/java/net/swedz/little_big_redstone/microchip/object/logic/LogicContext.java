package net.swedz.little_big_redstone.microchip.object.logic;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;

import java.util.Collections;
import java.util.List;

public final class LogicContext
{
	private final Level    level;
	private final BlockPos pos;
	
	private final Microchip microchip;
	
	private final List<LogicEntry> dirtyEntries = Lists.newArrayList();
	
	public LogicContext(Level level, BlockPos pos, Microchip microchip)
	{
		this.level = level;
		this.pos = pos;
		this.microchip = microchip;
	}
	
	public LogicContext(MicrochipBlockEntity blockEntity)
	{
		this(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.microchip());
	}
	
	public Level level()
	{
		return level;
	}
	
	public BlockPos pos()
	{
		return pos;
	}
	
	public <A extends MicrochipAwareness<A>> A awareness(AwarenessType<A> type)
	{
		return microchip.awarenesses().get(type);
	}
	
	public boolean isDirty()
	{
		return !dirtyEntries.isEmpty();
	}
	
	public void markDirty(LogicComponent component)
	{
		for(var entry : microchip.components())
		{
			if(entry.component() == component && !dirtyEntries.contains(entry))
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
