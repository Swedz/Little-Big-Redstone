package net.swedz.little_big_redstone.guide.tags.microchip.element;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.LytErrorSink;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.minecraft.core.Direction;
import net.swedz.little_big_redstone.guide.tags.microchip.MicrochipGuidebookScene;

import java.util.Set;

public final class RedstoneSignalTagCompiler implements MicrochipSceneElementTagCompiler
{
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("RedstoneSignal");
	}
	
	@Override
	public void compile(MicrochipGuidebookScene microchip, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el)
	{
		Integer step = MdxAttrs.getInt(compiler, errorSink, el, "step", -1);
		if(step == -1)
		{
			step = null;
		}
		var direction = MdxAttrs.getEnum(compiler, errorSink, el, "direction", Direction.NORTH);
		int strength = MdxAttrs.getInt(compiler, errorSink, el, "strength", 0);
		
		microchip.setRedstoneSignal(step, direction, strength);
	}
	
	@Override
	public boolean isDelayed()
	{
		return true;
	}
}
