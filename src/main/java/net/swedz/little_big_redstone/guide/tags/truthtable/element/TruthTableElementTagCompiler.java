package net.swedz.little_big_redstone.guide.tags.truthtable.element;

import guideme.compiler.PageCompiler;
import guideme.document.LytErrorSink;
import guideme.document.block.table.LytTable;
import guideme.extensions.Extension;
import guideme.extensions.ExtensionPoint;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;

import java.util.Set;

public interface TruthTableElementTagCompiler extends Extension
{
	ExtensionPoint<TruthTableElementTagCompiler> EXTENSION_POINT = new ExtensionPoint<>(TruthTableElementTagCompiler.class);
	
	Set<String> getTagNames();
	
	void compile(LytTable inputTable, LytTable outputTable, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el);
}
