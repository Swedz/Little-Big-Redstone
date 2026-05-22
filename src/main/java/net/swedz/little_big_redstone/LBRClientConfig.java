package net.swedz.little_big_redstone;

import net.swedz.little_big_redstone.gui.noteboard.contents.NoteBoardStickyNote;
import net.swedz.tesseract.neoforge.config.annotation.ConfigComment;
import net.swedz.tesseract.neoforge.config.annotation.ConfigKey;
import net.swedz.tesseract.neoforge.config.annotation.Range;
import net.swedz.tesseract.neoforge.config.annotation.SubSection;

public interface LBRClientConfig
{
	@ConfigKey
	@ConfigComment("The amount of time (in ticks) the floppy disk item display remain on the screen before fading away.")
	@Range.Integer(min = 0, max = Integer.MAX_VALUE)
	default int floppyDiskViewLingerTime()
	{
		return 40;
	}
	
	@ConfigKey
	@SubSection
	StickyNoteInWorldView stickyNoteInWorldView();
	
	interface StickyNoteInWorldView
	{
		@ConfigKey
		@ConfigComment("The width and height to display the in-world sticky note view on the screen.")
		@Range.Integer(min = NoteBoardStickyNote.MIN_NOTE_SIZE, max = NoteBoardStickyNote.MAX_NOTE_SIZE)
		default int size()
		{
			return NoteBoardStickyNote.DEFAULT_NOTE_SIZE;
		}
		
		@ConfigKey
		void size(int value);
		
		@ConfigKey
		@ConfigComment({
				"The X position where the in-world sticky note view should display on the screen.",
				"Stored as a percentage."
		})
		@Range.Double(min = 0, max = 1)
		default double x()
		{
			return 0.01;
		}
		
		@ConfigKey
		void x(double value);
		
		@ConfigKey
		@ConfigComment({
				"The Y position where the in-world sticky note view should display on the screen.",
				"Stored as a percentage."
		})
		@Range.Double(min = 0, max = 1)
		default double y()
		{
			return 0.01;
		}
		
		@ConfigKey
		void y(double value);
	}
	
	@ConfigKey
	@ConfigComment({
			"The scale to apply to the tooltip sticky note view on the screen.",
			"1 results in the sticky note view being 180x180."
	})
	@Range.Double(min = 0, max = 4)
	default double stickyNoteTooltipViewScale()
	{
		return 0.5;
	}
	
	@ConfigKey
	@ConfigComment("The amount of time (in ticks) sticky notes remain on the screen before fading away.")
	@Range.Integer(min = 0, max = Integer.MAX_VALUE)
	default int stickyNoteInWorldViewLingerTime()
	{
		return 40;
	}
	
	@ConfigKey
	@ConfigComment({
			"The number of threads to use for pathfinding wires in microchips.",
			"Be careful editing this value."
	})
	@Range.Integer(min = 0, max = Integer.MAX_VALUE)
	default int microchipWirePathfindingThreads()
	{
		return 2;
	}
}
