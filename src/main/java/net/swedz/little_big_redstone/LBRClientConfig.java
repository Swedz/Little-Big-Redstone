package net.swedz.little_big_redstone;

import net.swedz.tesseract.neoforge.config.annotation.ConfigComment;
import net.swedz.tesseract.neoforge.config.annotation.ConfigKey;
import net.swedz.tesseract.neoforge.config.annotation.Range;

public interface LBRClientConfig
{
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
}
