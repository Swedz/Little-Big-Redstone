package net.swedz.little_big_redstone.microchip.object.logic.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.function.Function;
import java.util.function.Supplier;

public final class LogicTypeDeferredRegister extends DeferredRegister<LogicType>
{
	public LogicTypeDeferredRegister(String namespace)
	{
		super(LogicTypes.REGISTRY_KEY, namespace);
	}
	
	@Override
	public <I extends LogicType> DeferredLogicType<I> register(String name, Supplier<? extends I> sup)
	{
		return (DeferredLogicType<I>) super.register(name, sup);
	}
	
	@Override
	public <I extends LogicType> DeferredLogicType<I> register(String name, Function<ResourceLocation, ? extends I> func)
	{
		return (DeferredLogicType<I>) super.register(name, func);
	}
	
	@Override
	protected <I extends LogicType> DeferredLogicType<I> createHolder(ResourceKey<? extends Registry<LogicType>> registryKey, ResourceLocation key)
	{
		return new DeferredLogicType(ResourceKey.create(registryKey, key));
	}
}
