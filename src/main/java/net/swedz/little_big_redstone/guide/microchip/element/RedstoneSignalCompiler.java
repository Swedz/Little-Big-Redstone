package net.swedz.little_big_redstone.guide.microchip.element;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.LytErrorSink;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import net.minecraft.core.Direction;
import net.swedz.little_big_redstone.guide.microchip.MicrochipGuidebookScene;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;

import java.util.Set;

public final class RedstoneSignalCompiler implements MicrochipSceneElementTagCompiler
{
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("RedstoneSignal");
	}
	
	@Override
	public void compile(MicrochipGuidebookScene microchip, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el)
	{
		var direction = MdxAttrs.getEnum(compiler, errorSink, el, "direction", Direction.NORTH);
		int strength = MdxAttrs.getInt(compiler, errorSink, el, "strength", 0);
		
		var redstone = microchip.getAwareness(AwarenessTypes.REDSTONE);
		redstone.setInputPowered(direction, strength);
	}
	
	@Override
	public boolean isDelayed()
	{
		return true;
	}
}
