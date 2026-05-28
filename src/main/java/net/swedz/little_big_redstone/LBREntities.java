package net.swedz.little_big_redstone;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;

import java.util.function.Supplier;

public final class LBREntities
{
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, LBR.ID);
	
	public static final Supplier<EntityType<StickyNoteEntity>> STICKY_NOTE = create("sticky_note", () -> EntityType.Builder
			.<StickyNoteEntity>of(StickyNoteEntity::new, MobCategory.MISC)
			.sized(0.5f, 0.5f)
			.eyeHeight(0)
			.clientTrackingRange(10)
			.updateInterval(Integer.MAX_VALUE));
	
	private static <T extends Entity> Supplier<EntityType<T>> create(String name, Supplier<EntityType.Builder<T>> builder)
	{
		return ENTITY_TYPES.register(name, () -> builder.get().build(ResourceKey.create(ENTITY_TYPES.getRegistryKey(), LBR.id(name))));
	}
	
	public static void init(IEventBus bus)
	{
		ENTITY_TYPES.register(bus);
	}
}
