package net.swedz.little_big_redstone;

import net.swedz.tesseract.neoforge.config.annotation.ConfigComment;
import net.swedz.tesseract.neoforge.config.annotation.ConfigKey;
import net.swedz.tesseract.neoforge.config.annotation.Range;

public interface LBRClientConfig
{
	@ConfigKey("floppy_disk_view_linger_time")
	@ConfigComment("The amount of time (in ticks) the floppy disk item display remain on the screen before fading away.")
	@Range.Integer(min = 0, max = Integer.MAX_VALUE)
	default int floppyDiskViewLingerTime()
	{
		return 40;
	}
	
	@ConfigKey("sticky_note_in_world_view_scale")
	@ConfigComment({
			"The scale to apply to the in-world sticky note view on the screen.",
			"1 results in the sticky note view being 180x180."
	})
	@Range.Double(min = 0, max = 4)
	default double stickyNoteInWorldViewScale()
	{
		return 0.5;
	}
	
	@ConfigKey("sticky_note_tooltip_view_scale")
	@ConfigComment({
			"The scale to apply to the tooltip sticky note view on the screen.",
			"1 results in the sticky note view being 180x180."
	})
	@Range.Double(min = 0, max = 4)
	default double stickyNoteTooltipViewScale()
	{
		return 0.5;
	}
	
	@ConfigKey("sticky_note_in_world_view_linger_time")
	@ConfigComment("The amount of time (in ticks) sticky notes remain on the screen before fading away.")
	@Range.Integer(min = 0, max = Integer.MAX_VALUE)
	default int stickyNoteInWorldViewLingerTime()
	{
		return 40;
	}
}
