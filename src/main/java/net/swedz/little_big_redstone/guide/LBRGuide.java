package net.swedz.little_big_redstone.guide;

import guideme.Guide;
import guideme.compiler.PageCompiler;
import guideme.compiler.TagCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.LytErrorSink;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.guide.microchip.MicrochipSceneCompiler;
import net.swedz.little_big_redstone.guide.microchip.element.LogicElementCompiler;
import net.swedz.little_big_redstone.guide.microchip.element.MicrochipSceneElementTagCompiler;
import net.swedz.little_big_redstone.guide.microchip.element.WireElementCompiler;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

public final class LBRGuide
{
	public static void init()
	{
		Guide.builder(LBR.id("guide"))
				.folder("guide")
				.extension(TagCompiler.EXTENSION_POINT, new MicrochipSceneCompiler())
				.extension(MicrochipSceneElementTagCompiler.EXTENSION_POINT, new LogicElementCompiler())
				.extension(MicrochipSceneElementTagCompiler.EXTENSION_POINT, new WireElementCompiler())
				.build();
	}
	
	public static DyeColor getDyeColor(PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el,
									   String name, DyeColor defaultColor)
	{
		var rawColor = MdxAttrs.getString(compiler, errorSink, el, name, null);
		if(rawColor != null)
		{
			try
			{
				return DyeColor.valueOf(rawColor.toUpperCase());
			}
			catch (IllegalArgumentException ignored)
			{
				errorSink.appendError(compiler, "Color must be a valid dye color", el);
				return defaultColor;
			}
		}
		return defaultColor;
	}
	
	public static LogicType<?> getLogicType(PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el,
											String name)
	{
		var logicId =  MdxAttrs.getString(compiler, errorSink, el, name, null);
		if(logicId != null)
		{
			try
			{
				return LogicTypes.get(logicId.toLowerCase());
			}
			catch(Exception ignored)
			{
				errorSink.appendError(compiler, "Logic type id does not exist", el);
				return null;
			}
		}
		return null;
	}
}
