package net.swedz.little_big_redstone.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.block.microchip.MicrochipWatcher;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
@Implements(@Interface(iface = MicrochipWatcher.class, prefix = "microchipWatcher$"))
public abstract class MicrochipWatcherPlayerMixin extends LivingEntity
{
	protected MicrochipWatcherPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level)
	{
		super(entityType, level);
	}
	
	@Unique
	private BlockPos watchedMicrochip;
	
	public BlockPos microchipWatcher$getWatchedMicrochip()
	{
		return watchedMicrochip;
	}
	
	public void microchipWatcher$setWatchedMicrochip(BlockPos pos)
	{
		watchedMicrochip = pos;
	}
}
