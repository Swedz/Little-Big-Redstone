package net.swedz.little_big_redstone.microchip.awareness.types;

import com.google.common.collect.Sets;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;
import net.swedz.little_big_redstone.microchip.object.logic.tag.LogicTagLabel;
import net.swedz.little_big_redstone.microchip.tag.MicrochipTagSystem;
import net.swedz.little_big_redstone.microchip.tag.TagOwnerKey;

import java.util.Set;

public final class LogicTagAwareness extends MicrochipAwareness<LogicTagAwareness>
{
	private Set<LogicTagLabel> started = Sets.newHashSet();
	private Set<LogicTagLabel> stopped = Sets.newHashSet();
	
	public void emit(LogicTagLabel label)
	{
		if(started.add(label))
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
		stopped = started;
		started = Sets.newHashSet();
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
		
		for(var label : started)
		{
			MicrochipTagSystem.startEmit(level, blockPos, owner, label);
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
		
		for(var label : started)
		{
			MicrochipTagSystem.stopEmit(level, blockPos, owner, label);
		}
	}
}
