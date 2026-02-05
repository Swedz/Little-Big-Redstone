package net.swedz.little_big_redstone.microchip.object.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.swedz.tesseract.neoforge.api.WorldPos;

import java.util.UUID;

public interface LogicContextAccess
{
	Level level();
	
	BlockPos blockPos();
	
	default WorldPos worldPos()
	{
		return new WorldPos(this.level(), this.blockPos());
	}
	
	UUID placedBy();
}
