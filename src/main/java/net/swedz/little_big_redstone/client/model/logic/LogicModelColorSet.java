package net.swedz.little_big_redstone.client.model.logic;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.client.model.ExtraFaceData;

import java.util.function.Function;

public record LogicModelColorSet(
		int foreground,
		int background
)
{
	public static final LogicModelColorSet DEFAULT = new LogicModelColorSet(0xFFFFFFFF, 0x00000000);
	
	private static final Codec<Integer> COLOR = Codec.either(Codec.INT, Codec.STRING).xmap(
			either -> either.map(Function.identity(), str -> (int) Long.parseLong(str, 16)),
			color -> Either.right(Integer.toHexString(color))
	);
	
	public static final Codec<LogicModelColorSet> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					COLOR.optionalFieldOf("foreground", DEFAULT.foreground()).forGetter(LogicModelColorSet::foreground),
					COLOR.optionalFieldOf("background", DEFAULT.background()).forGetter(LogicModelColorSet::background)
			)
			.apply(instance, LogicModelColorSet::new));
	
	public ExtraFaceData foregroundFaceData()
	{
		return new ExtraFaceData(foreground, ExtraFaceData.DEFAULT.lightEmission(), ExtraFaceData.DEFAULT.ambientOcclusion());
	}
	
	public ExtraFaceData backgroundFaceData()
	{
		return new ExtraFaceData(background, ExtraFaceData.DEFAULT.lightEmission(), ExtraFaceData.DEFAULT.ambientOcclusion());
	}
}
