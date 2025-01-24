package net.swedz.little_big_redstone.microchip.logic.reader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAware;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.little_big_redstone.microchip.logic.LogicTypes;

import java.util.Objects;

public final class LogicReader extends LogicComponent<LogicReader, LogicReaderConfig> implements MicrochipAware
{
	public static final MapCodec<LogicReader> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicReaderConfig.CODEC.fieldOf("config").forGetter(LogicReader::config),
					Codec.BOOL.fieldOf("output").forGetter(LogicReader::output)
			)
			.apply(instance, LogicReader::new));
	
	public static final StreamCodec<ByteBuf, LogicReader> STREAM_CODEC = StreamCodec.composite(
			LogicReaderConfig.STREAM_CODEC, LogicReader::config,
			ByteBufCodecs.BOOL, LogicReader::output,
			LogicReader::new
	);
	
	private boolean outputState;
	
	private LogicReader(LogicReaderConfig config, boolean outputState)
	{
		super(config);
		this.outputState = outputState;
	}
	
	private LogicReader(boolean outputState)
	{
		super();
		this.outputState = outputState;
	}
	
	public LogicReader()
	{
		this(false);
	}
	
	@Override
	public AwarenessType<?>[] awarenessTypes()
	{
		return new AwarenessType[]{switch (config.mode)
		{
			case ITEM -> AwarenessTypes.CAPABILITY_ITEM;
			case FLUID -> AwarenessTypes.CAPABILITY_FLUID;
			case ENERGY -> AwarenessTypes.CAPABILITY_ENERGY;
		}};
	}
	
	@Override
	protected LogicReaderConfig defaultConfig()
	{
		return new LogicReaderConfig();
	}
	
	@Override
	protected void processTickInternal(LogicContext context, boolean[] inputs)
	{
		boolean originalOutputState = outputState;
		
		float fill = 0;
		
		if(config.mode == LogicReaderMode.ITEM)
		{
			var awareness = context.awareness(AwarenessTypes.CAPABILITY_ITEM);
			var handler = awareness.get(context.level(), context.pos(), config.direction);
			if(handler != null)
			{
				int totalItems = 0;
				int maxItems = 0;
				for(int slot = 0; slot < handler.getSlots(); slot++)
				{
					var stack = handler.getStackInSlot(slot);
					if(stack.isEmpty())
					{
						maxItems += Mth.clamp(handler.getSlotLimit(slot), 0, 64);
					}
					else
					{
						totalItems += stack.getCount();
						maxItems += stack.getMaxStackSize();
					}
				}
				fill = (float) totalItems / maxItems;
			}
		}
		
		else if(config.mode == LogicReaderMode.FLUID)
		{
			var awareness = context.awareness(AwarenessTypes.CAPABILITY_FLUID);
			var handler = awareness.get(context.level(), context.pos(), config.direction);
			if(handler != null)
			{
				int totalFluid = 0;
				int maxFluid = 0;
				for(int tank = 0; tank < handler.getTanks(); tank++)
				{
					totalFluid += handler.getFluidInTank(tank).getAmount();
					maxFluid += handler.getTankCapacity(tank);
				}
				fill = (float) totalFluid / maxFluid;
			}
		}
		
		else if(config.mode == LogicReaderMode.ENERGY)
		{
			var awareness = context.awareness(AwarenessTypes.CAPABILITY_ENERGY);
			var handler = awareness.get(context.level(), context.pos(), config.direction);
			if(handler != null)
			{
				int totalEnergy = handler.getEnergyStored();
				int maxEnergy = handler.getMaxEnergyStored();
				fill = (float) totalEnergy / maxEnergy;
			}
		}
		
		outputState = config.powerRange.contains(fill);
		if(outputState != originalOutputState)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	public LogicType<LogicReader> type()
	{
		return LogicTypes.READER;
	}
	
	@Override
	public boolean output(int index)
	{
		return outputState;
	}
	
	public boolean output()
	{
		return outputState;
	}
	
	@Override
	protected void internalLoadFrom(LogicReader other)
	{
		outputState = other.outputState;
	}
	
	@Override
	public void resetForPickup()
	{
		outputState = false;
	}
	
	@Override
	public LogicReader copy()
	{
		return new LogicReader(config.copy(), outputState);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(config);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicReader other && Objects.equals(config, other.config));
	}
}
