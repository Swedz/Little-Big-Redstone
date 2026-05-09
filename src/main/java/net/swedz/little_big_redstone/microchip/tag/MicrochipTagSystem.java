package net.swedz.little_big_redstone.microchip.tag;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.microchip.object.logic.tag.LogicTagLabel;
import net.swedz.tesseract.neoforge.api.WorldPos;

import java.util.Map;

public final class MicrochipTagSystem
{
	private static final Map<LogicTagLabel, MicrochipTagSection> TICKETS = Maps.newConcurrentMap();
	
	public static int sense(TagOwnerKey owner, LogicTagLabel label, int threshold)
	{
		var section = TICKETS.get(label);
		return section != null ? section.getSignal(owner, threshold) : 0;
	}
	
	public static void startEmit(Level level, BlockPos blockPos, TagOwnerKey owner, LogicTagLabel label, int signal)
	{
		var section = TICKETS.computeIfAbsent(label, (__) -> new MicrochipTagSection());
		section.add(owner, new WorldPos(level, blockPos), signal);
	}
	
	public static void stopEmit(Level level, BlockPos blockPos, TagOwnerKey owner, LogicTagLabel label)
	{
		var section = TICKETS.get(label);
		if(section == null)
		{
			return;
		}
		section.remove(owner, new WorldPos(level, blockPos));
		if(section.isEmpty())
		{
			TICKETS.remove(label);
		}
	}
}
