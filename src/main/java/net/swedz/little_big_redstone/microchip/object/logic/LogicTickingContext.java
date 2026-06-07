package net.swedz.little_big_redstone.microchip.object.logic;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class LogicTickingContext implements LogicContextAccess
{
	private final Level    level;
	private final BlockPos blockPos;
	
	private final Microchip microchip;
	
	private final UUID placedBy;
	
	private final List<LogicEntry> dirtyEntries = Lists.newArrayList();
	
	public LogicTickingContext(Level level, BlockPos blockPos, Microchip microchip, UUID placedBy)
	{
		this.level = level;
		this.blockPos = blockPos;
		this.microchip = microchip;
		this.placedBy = placedBy;
	}
	
	public LogicTickingContext(MicrochipBlockEntity blockEntity)
	{
		this(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.microchip(), blockEntity.getPlacedBy());
	}
	
	@Override
	public Level level()
	{
		return level;
	}
	
	@Override
	public BlockPos blockPos()
	{
		return blockPos;
	}
	
	@Override
	public UUID placedBy()
	{
		return placedBy;
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
	
	public boolean checkValid(LogicConfig config)
	{
		return config.checkValid(microchip.components());
	}
}