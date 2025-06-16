package net.swedz.little_big_redstone.guide.microchip.element;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.LytErrorSink;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.swedz.little_big_redstone.guide.microchip.MicrochipLytBlock;

import java.util.Set;

public final class WireElementCompiler implements MicrochipSceneElementTagCompiler
{
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("Wire");
	}
	
	@Override
	public void compile(MicrochipLytBlock microchip, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el)
	{
		var from = MdxAttrs.getString(compiler, errorSink, el, "from", null);
		if(from == null)
		{
			errorSink.appendError(compiler, "Missing 'from' attribute", el);
			return;
		}
		else if(microchip.getLogic(from) == null)
		{
			errorSink.appendError(compiler, "No logic exists for that name", el);
			return;
		}
		
		var to = MdxAttrs.getString(compiler, errorSink, el, "to", null);
		if(to == null)
		{
			errorSink.appendError(compiler, "Missing 'to' attribute", el);
			return;
		}
		else if(microchip.getLogic(to) == null)
		{
			errorSink.appendError(compiler, "No logic exists for that name", el);
			return;
		}
		
		int fromPort = MdxAttrs.getInt(compiler, errorSink, el, "from_port", 0);
		int toPort = MdxAttrs.getInt(compiler, errorSink, el, "to_port", 0);
		
		microchip.addWire(from, to, fromPort, toPort);
	}
}
