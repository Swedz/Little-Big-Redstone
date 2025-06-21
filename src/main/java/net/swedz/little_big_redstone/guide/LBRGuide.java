package net.swedz.little_big_redstone.guide;

import guideme.Guide;
import guideme.compiler.PageCompiler;
import guideme.compiler.TagCompiler;
import guideme.compiler.tags.BoxFlowDirection;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.LytErrorSink;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import guideme.scene.ImplicitAnnotationStrategy;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.guide.tags.block.FloatingBoxTagCompiler;
import net.swedz.little_big_redstone.guide.tags.block.PaddedBoxTagCompiler;
import net.swedz.little_big_redstone.guide.tags.microchip.MicrochipSceneTagCompiler;
import net.swedz.little_big_redstone.guide.tags.microchip.element.LogicElementTagCompiler;
import net.swedz.little_big_redstone.guide.tags.microchip.element.MicrochipSceneElementTagCompiler;
import net.swedz.little_big_redstone.guide.tags.microchip.element.RedstoneSignalTagCompiler;
import net.swedz.little_big_redstone.guide.tags.microchip.element.WireElementTagCompiler;
import net.swedz.little_big_redstone.guide.tags.scene.InputOutputImplicitAnnotationStrategy;
import net.swedz.little_big_redstone.guide.tags.truthtable.TruthTableTagCompiler;
import net.swedz.little_big_redstone.guide.tags.truthtable.element.TruthTableElementTagCompiler;
import net.swedz.little_big_redstone.guide.tags.truthtable.element.TruthTableStateTagCompiler;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

public final class LBRGuide
{
	public static void init()
	{
		Guide.builder(LBR.id("guide"))
				.folder("guide")
				.extension(TagCompiler.EXTENSION_POINT, new PaddedBoxTagCompiler())
				.extension(TagCompiler.EXTENSION_POINT, new MicrochipSceneTagCompiler())
				.extension(MicrochipSceneElementTagCompiler.EXTENSION_POINT, new LogicElementTagCompiler())
				.extension(MicrochipSceneElementTagCompiler.EXTENSION_POINT, new WireElementTagCompiler())
				.extension(MicrochipSceneElementTagCompiler.EXTENSION_POINT, new RedstoneSignalTagCompiler())
				.extension(TagCompiler.EXTENSION_POINT, new TruthTableTagCompiler())
				.extension(TruthTableElementTagCompiler.EXTENSION_POINT, new TruthTableStateTagCompiler())
				.extension(TagCompiler.EXTENSION_POINT, new FloatingBoxTagCompiler(BoxFlowDirection.ROW))
				.extension(TagCompiler.EXTENSION_POINT, new FloatingBoxTagCompiler(BoxFlowDirection.COLUMN))
				.extension(ImplicitAnnotationStrategy.EXTENSION_POINT, new InputOutputImplicitAnnotationStrategy())
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
		var logicId = MdxAttrs.getString(compiler, errorSink, el, name, null);
		if(logicId != null)
		{
			try
			{
				return LogicTypes.get(logicId.toLowerCase());
			}
			catch (Exception ignored)
			{
				errorSink.appendError(compiler, "Logic type does not exist", el);
				return null;
			}
		}
		return null;
	}
	
	public static String[] getStringArray(PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el,
										  String name)
	{
		var rawInts = MdxAttrs.getString(compiler, errorSink, el, name, null);
		if(rawInts != null)
		{
			return rawInts.split(",");
		}
		return null;
	}
}
