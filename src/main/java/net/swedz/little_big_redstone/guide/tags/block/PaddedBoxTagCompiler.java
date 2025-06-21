package net.swedz.little_big_redstone.guide.tags.block;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.BlockTagCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.block.LytBlockContainer;
import guideme.document.block.LytVBox;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;

import java.util.Set;

public final class PaddedBoxTagCompiler extends BlockTagCompiler
{
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("PaddedBox");
	}
	
	@Override
	protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el)
	{
		int paddingLeft = MdxAttrs.getInt(compiler, parent, el, "left", 0);
		int paddingTop = MdxAttrs.getInt(compiler, parent, el, "top", 0);
		int paddingRight = MdxAttrs.getInt(compiler, parent, el, "right", 0);
		int paddingBottom = MdxAttrs.getInt(compiler, parent, el, "bottom", 0);
		
		var box = new LytVBox();
		
		compiler.compileBlockContext(el.children(), box);
		
		box.setPaddingLeft(paddingLeft);
		box.setPaddingTop(paddingTop);
		box.setPaddingRight(paddingRight);
		box.setPaddingBottom(paddingBottom);
		
		parent.append(box);
	}
}
