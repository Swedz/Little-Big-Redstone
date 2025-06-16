package net.swedz.little_big_redstone.guide.microchip.element;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.LytErrorSink;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.swedz.little_big_redstone.guide.LBRGuide;
import net.swedz.little_big_redstone.guide.microchip.MicrochipLytBlock;

import java.util.Set;

public final class LogicElementCompiler implements MicrochipSceneElementTagCompiler
{
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("Logic");
	}
	
	@Override
	public void compile(MicrochipLytBlock microchip, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el)
	{
		var name = MdxAttrs.getString(compiler, errorSink, el, "name", null);
		if(name == null)
		{
			errorSink.appendError(compiler, "No name provided for logic element", el);
			return;
		}
		else if(microchip.getLogic(name) != null)
		{
			errorSink.appendError(compiler, "Logic element already exists for that name", el);
			return;
		}
		int x = MdxAttrs.getInt(compiler, errorSink, el, "x", 0);
		int y = MdxAttrs.getInt(compiler, errorSink, el, "y", 0);
		var color = LBRGuide.getDyeColor(compiler, errorSink, el, "color", null);
		var type = LBRGuide.getLogicType(compiler, errorSink, el, "type");
		if(type == null)
		{
			errorSink.appendError(compiler, "Invalid logic element type", el);
			return;
		}
		microchip.addLogic(name, x, y, color, type);
	}
}
