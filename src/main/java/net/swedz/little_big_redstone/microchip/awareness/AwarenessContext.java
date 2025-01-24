package net.swedz.little_big_redstone.microchip.awareness;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.microchip.Microchip;

public record AwarenessContext(
		Level level, BlockPos pos,
		MicrochipBlockEntity blockEntity,
		Microchip microchip
)
{
	public AwarenessContext(MicrochipBlockEntity blockEntity)
	{
		this(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity, blockEntity.microchip());
	}
	
	public BlockState state()
	{
		return level.getBlockState(pos);
	}
}
