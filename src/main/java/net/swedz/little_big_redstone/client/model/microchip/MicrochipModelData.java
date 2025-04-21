package net.swedz.little_big_redstone.client.model.microchip;

import com.google.common.primitives.Booleans;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import java.util.Arrays;

public final class MicrochipModelData
{
	public static final ModelProperty<MicrochipModelData> KEY = new ModelProperty<>();
	
	public static final MicrochipModelData DEFAULT = new MicrochipModelData();
	
	public static final Codec<MicrochipModelData> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.BOOL.listOf().fieldOf("sides").forGetter((data) -> Booleans.asList(data.sides))
			)
			.apply(instance, (sides) -> new MicrochipModelData(Booleans.toArray(sides))));
	
	private boolean[] sides = new boolean[6];
	
	private MicrochipModelData(boolean[] sides)
	{
		this.sides = sides;
	}
	
	public MicrochipModelData()
	{
	}
	
	public boolean side(Direction direction)
	{
		return sides[direction.ordinal()];
	}
	
	public MicrochipModelData sides(boolean[] sides)
	{
		this.sides = sides;
		return this;
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
