package net.swedz.little_big_redstone.microchip.object.logic.register;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

public final class DeferredLogicType<I extends LogicType> extends DeferredHolder<LogicType, I>
{
	DeferredLogicType(ResourceKey<LogicType> key)
	{
		super(key);
	}
}
