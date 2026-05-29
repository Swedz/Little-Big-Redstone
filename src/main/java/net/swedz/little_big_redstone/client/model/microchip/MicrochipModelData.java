package net.swedz.little_big_redstone.client.model.microchip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.model.data.ModelProperty;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.Arrays;

public final class MicrochipModelData
{
	public static final ModelProperty<MicrochipModelData> KEY = new ModelProperty<>();
	
	public static final MicrochipModelData DEFAULT = new MicrochipModelData();
	
	public static final Codec<MicrochipModelData> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					CodecHelper.forLowercaseEnum(Side.class).listOf(6, 6).fieldOf("sides").forGetter((data) -> Arrays.asList(data.sides))
			)
			.apply(instance, (sides) -> new MicrochipModelData(sides.toArray(Side[]::new))));
	
	private Side[] sides = new Side[]{Side.NONE, Side.NONE, Side.NONE, Side.NONE, Side.NONE, Side.NONE};
	
	private MicrochipModelData(Side[] sides)
	{
		this.sides = sides;
	}
	
	public MicrochipModelData()
	{
	}
	
	public Side side(Direction direction)
	{
		return sides[direction.ordinal()];
	}
	
	public MicrochipModelData side(int index, Side side)
	{
		this.sides[index] = side;
		return this;
	}
	
	public enum Side
	{
		NONE,
		OFF,
		ON
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(o == null || getClass() != o.getClass())
		{
			return false;
		}
		var other = (MicrochipModelData) o;
		return Arrays.equals(sides, other.sides);
	}
	
	@Override
	public int hashCode()
	{
		return Arrays.hashCode(sides);
	}
}
