package net.swedz.little_big_redstone.microchip.awareness.types;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;
import net.swedz.little_big_redstone.microchip.object.logic.tag.LogicTagLabel;
import net.swedz.little_big_redstone.microchip.tag.MicrochipTagSystem;
import net.swedz.little_big_redstone.microchip.tag.TagOwnerKey;

import java.util.Map;
import java.util.Set;

public final class LogicTagAwareness extends MicrochipAwareness<LogicTagAwareness>
{
	private Map<LogicTagLabel, Integer> started = Maps.newHashMap();
	private Set<LogicTagLabel>          stopped = Sets.newHashSet();
	
	public void emit(LogicTagLabel label, int signal)
	{
		if(started.put(label, signal) == null)
		{
			stopped.remove(label);
		}
	}
	
	@Override
	public AwarenessType<LogicTagAwareness> type()
	{
		return AwarenessTypes.LOGIC_TAG;
	}
	
	@Override
	public void preTick(AwarenessContext context)
	{
		stopped = started.keySet();
		started = Maps.newHashMap();
	}
	
	@Override
	public void postTick(AwarenessContext context, boolean microchipDirty, boolean contextDirty)
	{
		if(started.isEmpty() && stopped.isEmpty())
		{
			return;
		}
		
		var level = context.level();
		var blockPos = context.blockPos();
		var owner = new TagOwnerKey(context.blockEntity().getPlacedBy());
		
		for(var label : stopped)
		{
			MicrochipTagSystem.stopEmit(level, blockPos, owner, label);
		}
		
		for(var entry : started.entrySet())
		{
			var label = entry.getKey();
			var signal = entry.getValue();
			MicrochipTagSystem.startEmit(level, blockPos, owner, label, signal);
		}
	}
	
	@Override
	public void removed(AwarenessContext context)
	{
		if(started.isEmpty() && stopped.isEmpty())
		{
			return;
		}
		
		var level = context.level();
		var blockPos = context.blockPos();
		var owner = new TagOwnerKey(context.blockEntity().getPlacedBy());
		
		for(var label : stopped)
		{
			MicrochipTagSystem.stopEmit(level, blockPos, owner, label);
		}
		
		for(var label : started.keySet())
		{
			MicrochipTagSystem.stopEmit(level, blockPos, owner, label);
		}
	}
}
