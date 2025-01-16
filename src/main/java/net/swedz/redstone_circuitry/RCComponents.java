package net.swedz.redstone_circuitry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.redstone_circuitry.microchip.gate.LogicGate;
import net.swedz.redstone_circuitry.microchip.gate.LogicGates;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class RCComponents
{
	private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, RedstoneCircuitry.ID);
	
	public static final Supplier<DataComponentType<LogicGate>> LOGIC_GATE = create(
			"logic_gate",
			(b) -> b.persistent(LogicGates.CODEC).networkSynchronized(LogicGates.STREAM_CODEC)
	);
	
	public static void init(IEventBus bus)
	{
		COMPONENTS.register(bus);
	}
	
	private static <D> DeferredHolder<DataComponentType<?>, DataComponentType<D>> create(String name, UnaryOperator<DataComponentType.Builder<D>> builder)
	{
		return COMPONENTS.registerComponentType(name, builder);
	}
}
