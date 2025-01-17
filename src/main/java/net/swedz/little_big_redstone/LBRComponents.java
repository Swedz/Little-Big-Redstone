package net.swedz.little_big_redstone;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.microchip.logic.Logic;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

import java.util.function.Supplier;

public final class LBRComponents
{
	private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LBR.ID);
	
	public static final Supplier<DataComponentType<Logic>> LOGIC = create("logic", LogicTypes.CODEC, LogicTypes.STREAM_CODEC);
	
	public static void init(IEventBus bus)
	{
		COMPONENTS.register(bus);
	}
	
	private static <D> DeferredHolder<DataComponentType<?>, DataComponentType<D>> create(String name, Codec<D> codec, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec)
	{
		return COMPONENTS.registerComponentType(name, (b) -> b.persistent(codec).networkSynchronized(streamCodec));
	}
}
