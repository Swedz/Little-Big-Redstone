package net.swedz.little_big_redstone.guide.tags.truthtable;

import com.google.common.collect.Maps;
import guideme.compiler.IndexingContext;
import guideme.compiler.IndexingSink;
import guideme.compiler.PageCompiler;
import guideme.compiler.tags.BlockTagCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.block.LytBlockContainer;
import guideme.document.block.LytParagraph;
import guideme.document.block.table.LytTable;
import guideme.document.block.table.LytTableRow;
import guideme.document.flow.LytFlowText;
import guideme.extensions.ExtensionCollection;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.swedz.little_big_redstone.guide.tags.truthtable.element.TruthTableElementTagCompiler;

import java.util.Map;
import java.util.Set;

public final class TruthTableCompiler extends BlockTagCompiler
{
	private final Map<String, TruthTableElementTagCompiler> elementTagCompilers = Maps.newHashMap();
	
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("TruthTable");
	}
	
	private static final char[] INPUT_CHARS  = "ABCD".toCharArray();
	private static final char[] OUTPUT_CHARS = "QXYZ".toCharArray();
	
	private static void header(LytTableRow row, String label)
	{
		var header = row.appendCell();
		var text = new LytParagraph();
		text.append(LytFlowText.of(label));
		header.append(text);
	}
	
	@Override
	protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el)
	{
		int inputs = MdxAttrs.getInt(compiler, parent, el, "inputs", 1);
		int outputs = MdxAttrs.getInt(compiler, parent, el, "outputs", 1);
		
		var table = new LytTable();
		
		var header = table.appendRow();
		header(header, "Input");
		header(header, "Output");
		
		var contentRow = table.appendRow();
		var inputColumn = contentRow.appendCell();
		var inputTable = new LytTable();
		var outputColumn = contentRow.appendCell();
		var outputTable = new LytTable();
		
		if(inputs > 1 || outputs > 1)
		{
			var inputTableHeader = inputTable.appendRow();
			for(int i = 0; i < inputs; i++)
			{
				header(inputTableHeader, String.valueOf(INPUT_CHARS[i]));
			}
			var outputTableHeader = outputTable.appendRow();
			for(int i = 0; i < outputs; i++)
			{
				header(outputTableHeader, String.valueOf(OUTPUT_CHARS[i]));
			}
		}
		
		for(var child : el.children())
		{
			if(child instanceof MdxJsxElementFields childEl)
			{
				var childTagName = childEl.name();
				var childCompiler = elementTagCompilers.get(childTagName);
				if(childCompiler == null)
				{
					parent.appendError(compiler, "Unknown truth table element", child);
				}
				else
				{
					childCompiler.compile(inputTable, outputTable, compiler, parent, childEl);
				}
			}
		}
		
		inputColumn.append(inputTable);
		outputColumn.append(outputTable);
		
		parent.append(table);
		
		/*var box = new LytSizedVBox(width);
		var heading = new LytHeading();
		heading.setDepth(3);
		heading.append(LytFlowText.of("Truth Table"));
		box.append(heading);
		box.append(table);
		var footer = new LytParagraph();
		footer.setPaddingTop(5);
		footer.append(LytFlowText.of("For details about truth tables, see the page [here](introduction.md)."));
		box.append(footer);
		var inlineBlock = LytFlowInlineBlock.of(box);
		switch (align)
		{
			case "left" -> inlineBlock.setAlignment(InlineBlockAlignment.FLOAT_LEFT);
			case "right" -> inlineBlock.setAlignment(InlineBlockAlignment.FLOAT_RIGHT);
			default -> parent.append(compiler.createErrorFlowContent("Invalid align. Must be left or right.", el));
		}
		parent.append(inlineBlock);*/
	}
	
	@Override
	public void onExtensionsBuilt(ExtensionCollection extensions)
	{
		for(var sceneElementTag : extensions.get(TruthTableElementTagCompiler.EXTENSION_POINT))
		{
			for(var tagName : sceneElementTag.getTagNames())
			{
				elementTagCompilers.put(tagName, sceneElementTag);
			}
		}
	}
	
	@Override
	public void index(IndexingContext indexer, MdxJsxElementFields el, IndexingSink sink)
	{
	}
}
