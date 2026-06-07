package net.swedz.little_big_redstone.microchip.object.logic.reader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRLogicTypes;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAware;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.Objects;
import java.util.Optional;

public final class LogicReader extends LogicComponent<LogicReader, LogicReaderConfig> implements MicrochipAware
{
	public static final MapCodec<LogicReader> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicReaderConfig.CODEC.fieldOf("config").forGetter(LogicReader::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicReader::color),
					Codec.INT.optionalFieldOf("output", 0).forGetter(LogicReader::output)
			)
			.apply(instance, LogicReader::new));
	
	public static final StreamCodec<ByteBuf, LogicReader> STREAM_CODEC = StreamCodec.composite(
			LogicReaderConfig.STREAM_CODEC, LogicReader::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicReader::color,
			ByteBufCodecs.INT, LogicReader::output,
			LogicReader::new
	);
	
	private int outputState;
	
	private LogicReader(LogicReaderConfig config, Optional<DyeColor> color, int outputState)
	{
		super(config, color);
		this.outputState = outputState;
	}
	
	private LogicReader(Optional<DyeColor> color, int outputState)
	{
		super(color);
		this.outputState = outputState;
	}
	
	public LogicReader()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	public AwarenessType<?>[] awarenessTypes()
	{
		return new AwarenessType[]{switch(config.mode())
		{
			case ITEM -> AwarenessTypes.CAPABILITY_ITEM;
			case FLUID -> AwarenessTypes.CAPABILITY_FLUID;
			case ENERGY -> AwarenessTypes.CAPABILITY_ENERGY;
			case COMPARATOR -> AwarenessTypes.ANALOG_SIGNAL;
		}};
	}
	
	@Override
	protected void processTickInternal(LogicTickingContext context, int[] inputs)
	{
		int originalOutputState = outputState;
		
		boolean isPercentage = config.fillThreshold().isPercentage();
		Number fill = 0;
		Number compareAgainst = isPercentage ? config.fillThreshold().percentage() : config.fillThreshold().number();
		int signal = 0;
		
		if(context.level() instanceof ServerLevel)
		{
			if(config.mode() == LogicReaderMode.ITEM)
			{
				var awareness = context.awareness(AwarenessTypes.CAPABILITY_ITEM);
				var handler = awareness.get(context.level(), context.blockPos(), config.direction());
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
					fill = isPercentage ?
							((float) totalItems / maxItems) :
							totalItems;
				}
			}
			
			else if(config.mode() == LogicReaderMode.FLUID)
			{
				var awareness = context.awareness(AwarenessTypes.CAPABILITY_FLUID);
				var handler = awareness.get(context.level(), context.blockPos(), config.direction());
				if(handler != null)
				{
					int totalFluid = 0;
					int maxFluid = 0;
					for(int tank = 0; tank < handler.getTanks(); tank++)
					{
						totalFluid += handler.getFluidInTank(tank).getAmount();
						maxFluid += handler.getTankCapacity(tank);
					}
					fill = isPercentage ?
							((float) totalFluid / maxFluid) :
							totalFluid;
				}
			}
			
			else if(config.mode() == LogicReaderMode.ENERGY)
			{
				var awareness = context.awareness(AwarenessTypes.CAPABILITY_ENERGY);
				var handler = awareness.get(context.level(), context.blockPos(), config.direction());
				if(handler != null)
				{
					int totalEnergy = handler.getEnergyStored();
					int maxEnergy = handler.getMaxEnergyStored();
					fill = isPercentage ?
							((float) totalEnergy / maxEnergy) :
							totalEnergy;
				}
			}
			
			else if(config.mode() == LogicReaderMode.COMPARATOR)
			{
				var awareness = context.awareness(AwarenessTypes.ANALOG_SIGNAL);
				signal = awareness.getSignal(config.direction());
			}
		}
		
		if(config.mode().readsSignal())
		{
			outputState = config.comparison().test(signal, config.signalThreshold()) ? signal : 0;
		}
		else
		{
			outputState = config.comparison().test(fill, compareAgainst) ? 1 : 0;
		}
		if(outputState != originalOutputState)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	public LogicType type()
	{
		return LBRLogicTypes.READER.get();
	}
	
	@Override
	protected int outputInternal(int index)
	{
		return outputState;
	}
	
	public int output()
	{
		return this.output(0);
	}
	
	@Override
	protected void internalLoadFrom(LogicReader other)
	{
		outputState = other.outputState;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type(), config, color);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicReader other && Objects.equals(config, other.config) && Objects.equals(color, other.color));
	}
}