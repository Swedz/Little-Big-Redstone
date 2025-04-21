package net.swedz.little_big_redstone.client.model;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class BasicCustomLoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
	public BasicCustomLoaderBuilder(ResourceLocation loaderId, T parent, ExistingFileHelper existingFileHelper)
	{
		super(loaderId, parent, existingFileHelper, false);
	}
}
