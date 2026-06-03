package net.swedz.little_big_redstone.microchip.object.logic.tag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAware;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTickingContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;
import net.swedz.little_big_redstone.microchip.tag.MicrochipTagSystem;
import net.swedz.little_big_redstone.microchip.tag.TagOwnerKey;

import java.util.Objects;
import java.util.Optional;

public final class LogicTag extends LogicComponent<LogicTag, LogicTagConfig> implements MicrochipAware
{
	public static final MapCodec<LogicTag> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					LogicTagConfig.CODEC.fieldOf("config").forGetter(LogicTag::config),
					DyeColor.CODEC.optionalFieldOf("color").forGetter(LogicTag::color),
					Codec.INT.optionalFieldOf("output", 0).forGetter(LogicTag::output)
			)
			.apply(instance, LogicTag::new));
	
	public static final StreamCodec<ByteBuf, LogicTag> STREAM_CODEC = StreamCodec.composite(
			LogicTagConfig.STREAM_CODEC, LogicTag::config,
			ByteBufCodecs.optional(DyeColor.STREAM_CODEC), LogicTag::color,
			ByteBufCodecs.VAR_INT, LogicTag::output,
			LogicTag::new
	);
	
	private int outputState;
	
	private LogicTag(LogicTagConfig config, Optional<DyeColor> color, int outputState)
	{
		super(config, color);
		this.outputState = outputState;
	}
	
	private LogicTag(Optional<DyeColor> color, int outputState)
	{
		super(color);
		this.outputState = outputState;
	}
	
	public LogicTag()
	{
		this(Optional.empty(), 0);
	}
	
	@Override
	public LogicType<LogicTag, LogicTagConfig> type()
	{
		return LogicTypes.TAG;
	}
	
	@Override
	public AwarenessType<?>[] awarenessTypes()
	{
		return new AwarenessType[]{
				AwarenessTypes.LOGIC_TAG
		};
	}
	
	@Override
	protected void processTickInternal(LogicTickingContext context, int[] inputs)
	{
		int originalOutputState = outputState;
		
		if(config.input())
		{
			var owner = context.placedBy() == null || config.global() ?
					TagOwnerKey.GLOBAL :
					new TagOwnerKey(context.placedBy());
			outputState = MicrochipTagSystem.sense(owner, config.label(), config.threshold());
		}
		else
		{
			outputState = inputs[0];
			
			var awareness = context.awareness(AwarenessTypes.LOGIC_TAG);
			if(outputState > 0)
			{
				awareness.emit(config.label(), outputState);
			}
		}
		
		if(outputState != originalOutputState)
		{
			context.markDirty(this);
		}
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
	protected void internalLoadFrom(LogicTag other)
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
			   (o instanceof LogicTag other && Objects.equals(config, other.config) && Objects.equals(color, other.color));
	}
}
