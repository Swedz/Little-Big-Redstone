package net.swedz.little_big_redstone.item.floppydisk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.tesseract.neoforge.api.Assert;

public record FloppyDiskProgramName(String name)
{
	public static final int MAX_LENGTH = 32;
	
	public static final String PATTERN = "[a-zA-Z0-9_]*";
	
	public static final Codec<FloppyDiskProgramName> CODEC = Codec.STRING
			.validate((string) ->
			{
				if(string.length() > MAX_LENGTH)
				{
					return DataResult.error(() -> "Program name cannot be > " + MAX_LENGTH + " characters");
				}
				if(!string.matches(PATTERN))
				{
					return DataResult.error(() -> "Program name must be alphanumeric");
				}
				return DataResult.success(string);
			})
			.xmap(FloppyDiskProgramName::new, FloppyDiskProgramName::name);
	
	public static final StreamCodec<ByteBuf, FloppyDiskProgramName> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
			.map(FloppyDiskProgramName::new, FloppyDiskProgramName::name);
	
	public static boolean isValid(String name)
	{
		return name.length() <= MAX_LENGTH &&
			   name.matches(PATTERN);
	}
	
	public FloppyDiskProgramName
	{
		Assert.that(name.length() <= MAX_LENGTH, "Program name cannot be > " + MAX_LENGTH + " characters");
		Assert.that(name.matches(PATTERN), "Program name must be alphanumeric");
	}
}
