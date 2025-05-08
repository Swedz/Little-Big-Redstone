package net.swedz.little_big_redstone;

import net.swedz.tesseract.neoforge.registry.SortOrder;

public interface LBRSortOrder
{
	SortOrder MICROCHIP    = new SortOrder(0);
	SortOrder RESOURCES    = new SortOrder(1);
	SortOrder LOGIC        = new SortOrder(2);
	SortOrder LOGIC_ARRAYS = new SortOrder(3);
	SortOrder FLOPPY_DISKS = new SortOrder(4);
	SortOrder STICKY_NOTES = new SortOrder(5);
}
