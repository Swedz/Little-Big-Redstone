package net.swedz.little_big_redstone.guide.microchip;

import com.google.common.collect.Maps;
import guideme.compiler.IndexingContext;
import guideme.compiler.IndexingSink;
import guideme.compiler.PageCompiler;
import guideme.compiler.tags.BlockTagCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.block.LytBlockContainer;
import guideme.extensions.ExtensionCollection;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.guide.LBRGuide;
import net.swedz.little_big_redstone.guide.microchip.element.MicrochipSceneElementTagCompiler;

import java.util.Map;
import java.util.Set;

public final class MicrochipSceneCompiler extends BlockTagCompiler
{
	private final Map<String, MicrochipSceneElementTagCompiler> elementTagCompilers = Maps.newHashMap();
	
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("MicrochipScene");
	}
	
	@Override
	protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el)
	{
		var width = MdxAttrs.getInt(compiler, parent, el, "width", 100);
		var height = MdxAttrs.getInt(compiler, parent, el, "height", 100);
		var color = LBRGuide.getDyeColor(compiler, parent, el, "color", DyeColor.RED);
		
		var block = new MicrochipLytBlock(color, width, height);
		
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
					childCompiler.compile(block, compiler, parent, childEl);
				}
			}
		}
		
		parent.append(block);
	}
	
	@Override
	public void onExtensionsBuilt(ExtensionCollection extensions)
	{
		for(var sceneElementTag : extensions.get(MicrochipSceneElementTagCompiler.EXTENSION_POINT))
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
