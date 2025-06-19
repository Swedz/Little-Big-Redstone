package net.swedz.little_big_redstone.guide.tags.truthtable.element;

import guideme.color.SymbolicColor;
import guideme.compiler.PageCompiler;
import guideme.document.LytErrorSink;
import guideme.document.block.LytParagraph;
import guideme.document.block.table.LytTable;
import guideme.document.block.table.LytTableRow;
import guideme.document.flow.LytFlowText;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import guideme.style.TextStyle;
import net.swedz.little_big_redstone.guide.LBRGuide;

import java.util.Set;

public final class TruthTableStateCompiler implements TruthTableElementTagCompiler
{
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("TruthState");
	}
	
	private void append(LytTableRow row, String[] values)
	{
		for(String value : values)
		{
			boolean on = false;
			if(value.equals("0") || value.equalsIgnoreCase("off"))
			{
				value = "OFF";
			}
			else if(value.equals("1") || value.equalsIgnoreCase("on"))
			{
				value = "ON";
				on = true;
			}
			else
			{
				continue;
			}
			var cell = row.appendCell();
			var content = new LytParagraph();
			var text = LytFlowText.of(value.toUpperCase());
			text.setStyle(TextStyle.builder()
					.color(on ? SymbolicColor.GREEN : SymbolicColor.RED)
					.build());
			content.append(text);
			cell.append(content);
		}
	}
	
	@Override
	public void compile(LytTable inputTable, LytTable outputTable, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el)
	{
		var input = LBRGuide.getStringArray(compiler, errorSink, el, "input");
		var output = LBRGuide.getStringArray(compiler, errorSink, el, "output");
		
		this.append(inputTable.appendRow(), input);
		this.append(outputTable.appendRow(), output);
	}
}
