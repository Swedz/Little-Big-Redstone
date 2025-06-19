package net.swedz.little_big_redstone.guide.tags.microchip.element;

import guideme.compiler.PageCompiler;
import guideme.document.LytErrorSink;
import guideme.extensions.Extension;
import guideme.extensions.ExtensionPoint;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.swedz.little_big_redstone.guide.tags.microchip.MicrochipGuidebookScene;

import java.util.Set;

public interface MicrochipSceneElementTagCompiler extends Extension
{
	ExtensionPoint<MicrochipSceneElementTagCompiler> EXTENSION_POINT = new ExtensionPoint<>(MicrochipSceneElementTagCompiler.class);
	
	Set<String> getTagNames();
	
	void compile(MicrochipGuidebookScene microchip, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el);
	
	default boolean isDelayed()
	{
		return false;
	}
}
