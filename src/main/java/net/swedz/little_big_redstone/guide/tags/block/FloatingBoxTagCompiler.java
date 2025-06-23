package net.swedz.little_big_redstone.guide.tags.block;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.BoxFlowDirection;
import guideme.compiler.tags.FlowTagCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.block.LytAxisBox;
import guideme.document.flow.InlineBlockAlignment;
import guideme.document.flow.LytFlowInlineBlock;
import guideme.document.flow.LytFlowParent;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;

import java.util.Set;

public final class FloatingBoxTagCompiler extends FlowTagCompiler
{
	private final BoxFlowDirection direction;
	
	public FloatingBoxTagCompiler(BoxFlowDirection direction)
	{
		this.direction = direction;
	}
	
	@Override
	public Set<String> getTagNames()
	{
		return Set.of(direction == BoxFlowDirection.ROW ? "FloatingRow" : "FloatingColumn");
	}
	
	@Override
	protected void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el)
	{
		int gap = MdxAttrs.getInt(compiler, parent, el, "gap", 5);
		var align = el.getAttributeString("align", "left");
		int width = MdxAttrs.getInt(compiler, parent, el, "width", -1);
		
		LytAxisBox box = switch (direction)
		{
			case ROW -> new LytSizedHBox(width);
			case COLUMN -> new LytSizedVBox(width);
		};
		box.setGap(gap);
		var inlineBlock = LytFlowInlineBlock.of(box);
		switch (align)
		{
			case "left" -> inlineBlock.setAlignment(InlineBlockAlignment.FLOAT_LEFT);
			case "right" -> inlineBlock.setAlignment(InlineBlockAlignment.FLOAT_RIGHT);
			default -> parent.append(compiler.createErrorFlowContent("Invalid align. Must be left or right.", el));
		}
		compiler.compileBlockContext(el.children(), box);
		parent.append(inlineBlock);
	}
}
