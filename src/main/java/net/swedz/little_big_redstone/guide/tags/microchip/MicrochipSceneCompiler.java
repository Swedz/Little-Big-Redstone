package net.swedz.little_big_redstone.guide.tags.microchip;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import net.swedz.little_big_redstone.guide.tags.microchip.element.MicrochipSceneElementTagCompiler;

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
		var width = MdxAttrs.getInt(compiler, parent, el, "width", -1);
		var height = MdxAttrs.getInt(compiler, parent, el, "height", -1);
		var marginWidth = MdxAttrs.getInt(compiler, parent, el, "marginWidth", width == -1 ? 10 : 0);
		var marginHeight = MdxAttrs.getInt(compiler, parent, el, "marginHeight", height == -1 ? 10 : 0);
		var color = LBRGuide.getDyeColor(compiler, parent, el, "color", DyeColor.RED);
		var includeToolbar = MdxAttrs.getBoolean(compiler, parent, el, "includeToolbar", false);
		
		var block = new MicrochipGuidebookScene(color, width, height, marginWidth, marginHeight, includeToolbar);
		
		Set<MdxJsxElementFields> delayedChildren = Sets.newHashSet();
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
				else if(childCompiler.isDelayed())
				{
					delayedChildren.add(childEl);
				}
				else
				{
					childCompiler.compile(block, compiler, parent, childEl);
				}
			}
		}
		
		block.adjustSize();
		
		for(var childEl : delayedChildren)
		{
			var childTagName = childEl.name();
			var childCompiler = elementTagCompilers.get(childTagName);
			childCompiler.compile(block, compiler, parent, childEl);
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
