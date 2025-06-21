package net.swedz.little_big_redstone.guide.tags.microchip.element;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.LytErrorSink;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.swedz.little_big_redstone.guide.tags.microchip.MicrochipGuidebookScene;

import java.util.Set;

public final class WireElementTagCompiler implements MicrochipSceneElementTagCompiler
{
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("Wire");
	}
	
	@Override
	public void compile(MicrochipGuidebookScene microchip, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el)
	{
		var from = MdxAttrs.getString(compiler, errorSink, el, "from", null);
		if(from == null)
		{
			errorSink.appendError(compiler, "Missing 'from' attribute", el);
			return;
		}
		
		var to = MdxAttrs.getString(compiler, errorSink, el, "to", null);
		if(to == null)
		{
			errorSink.appendError(compiler, "Missing 'to' attribute", el);
			return;
		}
		
		int fromPort = MdxAttrs.getInt(compiler, errorSink, el, "fromPort", 0);
		int toPort = MdxAttrs.getInt(compiler, errorSink, el, "toPort", 0);
		
		Boolean powerLock = el.getAttribute("powered") == null ? null : MdxAttrs.getBoolean(compiler, errorSink, el, "powered", false);
		
		microchip.addWire(from, to, fromPort, toPort, compiler, errorSink, el);
		
		if(powerLock != null)
		{
			var fromLogic = microchip.getLogic(from);
			fromLogic.component().config().setOutputLock(fromPort, powerLock);
		}
	}
	
	@Override
	public boolean isDelayed()
	{
		return true;
	}
}
