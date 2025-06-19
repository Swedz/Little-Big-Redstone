package net.swedz.little_big_redstone.guide.truthtable;

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
import net.swedz.little_big_redstone.guide.truthtable.element.TruthTableElementTagCompiler;

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
	
	private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	
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
				header(inputTableHeader, String.valueOf(ALPHABET[i]));
			}
			var outputTableHeader = outputTable.appendRow();
			for(int i = 0; i < outputs; i++)
			{
				header(outputTableHeader, String.valueOf(ALPHABET[i]));
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
					parent.appendError(compiler, "Unknown microchip scene element", child);
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
