package net.swedz.little_big_redstone.guide;

import guideme.Guide;
import guideme.compiler.TagCompiler;
import net.swedz.little_big_redstone.LBR;

public final class LBRGuide
{
	public static void init()
	{
		Guide.builder(LBR.id("guide"))
				.folder("guide")
				.extension(TagCompiler.EXTENSION_POINT, new MicrochipCompiler())
				.build();
	}
}
