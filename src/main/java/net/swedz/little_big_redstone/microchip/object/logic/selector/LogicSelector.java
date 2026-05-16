package net.swedz.little_big_redstone.microchip.object.logic.selector;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicGridSize;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.List;
import java.util.Optional;

public final class LogicSelector extends LogicComponent<LogicSelector, LogicSelectorConfig>
{
	public static final MapCodec<LogicSelector> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicSelectorConfig.CODEC.fieldOf("config").forGetter(LogicSelector::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicSelector::color),
					Codec.INT.optionalFieldOf("selected", 0).forGetter(LogicSelector::selected),
					Codec.INT.optionalFieldOf("output", 0).forGetter(LogicSelector::output)
			)
			.apply(instance, LogicSelector::new));
	
	public static final StreamCodec<ByteBuf, LogicSelector> STREAM_CODEC = StreamCodec.composite(
			LogicSelectorConfig.STREAM_CODEC, LogicSelector::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicSelector::color,
			ByteBufCodecs.VAR_INT, LogicSelector::selected,
			ByteBufCodecs.VAR_INT, LogicSelector::output,
			LogicSelector::new
	);
	
	private int selected, outputState;
	
	private LogicSelector(LogicSelectorConfig config, Optional<DyeColor> color, int selected, int outputState)
	{
		super(config, color);
		this.selected = selected;
		this.outputState = outputState;
	}
	
	private LogicSelector(Optional<DyeColor> color, int selected, int outputState)
	{
		super(color);
		this.selected = selected;
		this.outputState = outputState;
	}
	
	public LogicSelector()
	{
		this(Optional.empty(), 0, 0);
	}
	
	public int selected()
	{
		return selected;
	}
	
	@Override
	protected void processTickInternal(LogicContext context, int[] inputs)
	{
		int originalSelected = selected;
		int originalOutputState = outputState;
		
		if(config.mode == LogicSelectorMode.COUNTER)
		{
			boolean decrement = inputs[0] > 0;
			boolean increment = inputs[1] > 0;
			int newSelected = selected;
			if(decrement)
			{
				newSelected--;
				if(newSelected < 0)
				{
					newSelected = config.outputs - 1;
				}
			}
			if(increment)
			{
				newSelected++;
				if(newSelected >= config.outputs)
				{
					newSelected = 0;
				}
			}
			if(decrement || increment)
			{
				selected = newSelected;
				outputState = Math.max(inputs[0], inputs[1]);
			}
		}
		else if(config.mode == LogicSelectorMode.SETTER)
		{
			for(int index = inputs.length - 1; index >= 0; index--)
			{
				int signal = inputs[index];
				if(signal > 0)
				{
					selected = index;
					outputState = signal;
					break;
				}
			}
		}
		
		if(selected != originalSelected ||
		   outputState != originalOutputState)
		{
			context.markDirty(this);
		}
	}
	
	@Override
	protected int outputInternal(int index)
	{
		return index == selected ? (config.passSignal ? outputState : (index + 1)) : 0;
	}
	
	public int output()
	{
		return outputState;
	}
	
	@Override
	public LogicGridSize size()
	{
		int outputs = this.outputs();
		return new LogicGridSize(1, Math.max(1, outputs / 2));
	}
	
	@Override
	public void appendShiftHoverText(List<Component> lines)
	{
		lines.add(LBR.text().logicHelpSelector());
	}
	
	@Override
	protected void internalLoadFrom(LogicSelector other)
	{
		selected = other.selected;
		outputState = other.outputState;
	}
	
	@Override
	protected void internalResetForPickup()
	{
		selected = 0;
		outputState = 0;
	}
	
	@Override
	protected LogicSelectorConfig defaultConfig()
	{
		return new LogicSelectorConfig();
	}
	
	@Override
	public LogicType<LogicSelector> type()
	{
		return LogicTypes.SELECTOR;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.type(), config, color, selected);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof LogicSelector other && Objects.equal(config, other.config) && Objects.equal(color, other.color) && selected == other.selected);
	}
}