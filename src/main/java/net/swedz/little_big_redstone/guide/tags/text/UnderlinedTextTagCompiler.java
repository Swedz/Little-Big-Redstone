package net.swedz.little_big_redstone.guide.tags.text;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.FlowTagCompiler;
import guideme.document.flow.LytFlowParent;
import guideme.document.flow.LytFlowSpan;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;

import java.util.Set;

public final class UnderlinedTextTagCompiler extends FlowTagCompiler
{
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("Underlined");
	}
	
	@Override
	protected void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el)
	{
		var span = new LytFlowSpan();
		span.modifyStyle((style) -> style.underlined(true));
		compiler.compileFlowContext(el.children(), span);
		parent.append(span);
	}
}
