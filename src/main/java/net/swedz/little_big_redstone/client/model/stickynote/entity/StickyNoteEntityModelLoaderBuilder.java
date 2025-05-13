package net.swedz.little_big_redstone.client.model.stickynote.entity;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.swedz.tesseract.neoforge.api.Assert;

import java.util.List;

public final class StickyNoteEntityModelLoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
	public static <T extends ModelBuilder<T>> StickyNoteEntityModelLoaderBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
	{
		return new StickyNoteEntityModelLoaderBuilder<>(parent, existingFileHelper);
	}
	
	private final List<T> baseLayer = Lists.newArrayList();
	private final List<T> textLayer = Lists.newArrayList();
	
	private StickyNoteEntityModelLoaderBuilder(T parent, ExistingFileHelper existingFileHelper)
	{
		super(StickyNoteEntityUnbakedModel.ID, parent, existingFileHelper, false);
	}
	
	public StickyNoteEntityModelLoaderBuilder baseLayer(T model)
	{
		Assert.notNull(model);
		this.baseLayer.add(model);
		return this;
	}
	
	public StickyNoteEntityModelLoaderBuilder textLayer(T model)
	{
		Assert.notNull(model);
		this.textLayer.add(model);
		return this;
	}
	
	private static <T extends ModelBuilder<T>> JsonArray toJson(List<T> models)
	{
		var json = new JsonArray();
		for(T model : models)
		{
			json.add(model.toJson());
		}
		return json;
	}
	
	@Override
	public JsonObject toJson(JsonObject json)
	{
		Assert.that(!baseLayer.isEmpty(), "Base layer must have at least one element", JsonParseException::new);
		Assert.that(!textLayer.isEmpty(), "Text layer must have at least one element", JsonParseException::new);
		
		json = super.toJson(json);
		
		json.add("base_layer", toJson(baseLayer));
		json.add("text_layer", toJson(textLayer));
		
		return json;
	}
}
