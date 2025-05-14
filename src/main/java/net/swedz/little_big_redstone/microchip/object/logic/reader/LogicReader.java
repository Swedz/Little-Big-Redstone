package net.swedz.little_big_redstone.microchip.object.logic.reader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAware;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.swedz.little_big_redstone.LBRTextLine.*;

public final class LogicReader extends LogicComponent<LogicReader, LogicReaderConfig> implements MicrochipAware
{
	public static final MapCodec<LogicReader> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicReaderConfig.CODEC.fieldOf("config").forGetter(LogicReader::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicReader::color),
					Codec.BOOL.optionalFieldOf("output", false).forGetter(LogicReader::output)
			)
			.apply(instance, LogicReader::new));
	
	public static final StreamCodec<ByteBuf, LogicReader> STREAM_CODEC = StreamCodec.composite(
			LogicReaderConfig.STREAM_CODEC, LogicReader::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicReader::color,
			ByteBufCodecs.BOOL, LogicReader::output,
			LogicReader::new
	);
	
	private boolean outputState;
	
	private LogicReader(LogicReaderConfig config, Optional<DyeColor> color, boolean outputState)
	{
		super(config, color);
		this.outputState = outputState;
	}
	
	private LogicReader(Optional<DyeColor> color, boolean outputState)
	{
		super(color);
		this.outputState = outputState;
	}
	
	public LogicReader()
	{
		this(Optional.empty(), false);
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
		
		outputState = fill >= config.fillThreshold;
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
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(line(LBRText.LOGIC_HELP_READER_1));
		lines.add(line(LBRText.LOGIC_HELP_READER_2));
	}
	
	@Override
	protected void internalLoadFrom(LogicReader other)
	{
		outputState = other.outputState;
	}
	
	@Override
	public void internalResetForPickup()
	{
		outputState = false;
	}
	
	@Override
	public LogicReader copy()
	{
		return new LogicReader(config.copy(), color, outputState);
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
