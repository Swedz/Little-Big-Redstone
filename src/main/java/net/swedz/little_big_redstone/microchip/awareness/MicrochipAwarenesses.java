package net.swedz.little_big_redstone.microchip.awareness;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.swedz.little_big_redstone.microchip.Microchip;

import java.util.Map;
import java.util.Set;

public final class MicrochipAwarenesses implements AwarenessListener
{
	private final Map<AwarenessType<?>, MicrochipAwareness<?>> awarenesses = Maps.newHashMap();
	
	private final Set<MicrochipAwareness<?>> removed = Sets.newHashSet();
	
	public <A extends MicrochipAwareness<A>> A get(AwarenessType<A> type)
	{
		return (A) awarenesses.get(type);
	}
	
	public void rebuild(Microchip microchip)
	{
		Set<AwarenessType<?>> irrelevantTypes = Sets.newHashSet(awarenesses.keySet());
		for(var entry : microchip.components())
		{
			if(entry.component() instanceof MicrochipAware aware)
			{
				for(var type : aware.awarenessTypes())
				{
					if(!awarenesses.containsKey(type))
					{
						awarenesses.put(type, type.create());
					}
					irrelevantTypes.remove(type);
				}
			}
		}
		for(var type : irrelevantTypes)
		{
			var awareness = awarenesses.remove(type);
			removed.add(awareness);
		}
	}
	
	@Override
	public void load(Microchip microchip)
	{
		for(var awareness : awarenesses.values())
		{
			awareness.load(microchip);
		}
	}
	
	@Override
	public void neighborChanged(AwarenessContext context, Block neighborBlock, BlockPos neighborPos, Direction neighborDirection, boolean movedByPiston)
	{
		for(var awareness : awarenesses.values())
		{
			awareness.neighborChanged(context, neighborBlock, neighborPos, neighborDirection, movedByPiston);
		}
	}
	
	@Override
	public void preTick(AwarenessContext context)
	{
		for(var awareness : awarenesses.values())
		{
			awareness.preTick(context);
		}
	}
	
	@Override
	public void postTick(AwarenessContext context, boolean microchipDirty, boolean contextDirty)
	{
		for(var awareness : awarenesses.values())
		{
			awareness.postTick(context, microchipDirty, contextDirty);
		}
	}
	
	@Override
	public void removed(AwarenessContext context)
	{
		for(var awareness : removed)
		{
			awareness.removed(context);
		}
		removed.clear();
	}
	
	public void removedAll(AwarenessContext context)
	{
		for(var awareness : awarenesses.values())
		{
			awareness.removed(context);
		}
	}
}
