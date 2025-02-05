package net.swedz.little_big_redstone.datagen.server.provider.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;

import java.util.Comparator;

public final class BlockTagDatagenProvider extends BlockTagsProvider
{
	public BlockTagDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider(), LBR.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void addTags(HolderLookup.Provider provider)
	{
		for(BlockHolder<?> block : LBRBlocks.values().stream().sorted(Comparator.comparing((item) -> item.identifier().id())).toList())
		{
			for(TagKey<Block> tag : block.tags())
			{
				this.tag(tag).add(block.get());
			}
		}
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
