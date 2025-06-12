package net.swedz.little_big_redstone.guide;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.BlockTagCompiler;
import guideme.document.block.LytBlockContainer;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.microchip.object.logic.LogicTypes;

import java.util.Set;

public final class MicrochipCompiler extends BlockTagCompiler
{
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("Microchip");
	}
	
	@Override
	protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields fields)
	{
		// TODO build the block dynamically
		var block = new MicrochipLytBlock(DyeColor.RED, 100, 50);
		block.microchip().components().add(10, 10, LogicTypes.AND.defaultFactory().create());
		block.microchip().components().add(100-16-10, 10, LogicTypes.AND.defaultFactory().create());
		block.microchip().components().add(50-8, 10, LogicTypes.OR.defaultFactory().create());
		block.microchip().wires().add(0, 0, 1, 0);
		parent.append(block);
	}
}
