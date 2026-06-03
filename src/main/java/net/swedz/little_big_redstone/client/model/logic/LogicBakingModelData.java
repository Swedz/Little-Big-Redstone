package net.swedz.little_big_redstone.client.model.logic;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class LogicBakingModelData
{
	private static final UnboundedMapCodec<String, ResourceLocation> TEXTURE_MAP_CODEC = Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC);
	
	public static final Codec<LogicBakingModelData> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.unboundedMap(CodecHelper.forLowercaseEnum(DyeColor.class), LogicModelColorSet.CODEC).fieldOf("color_palette").forGetter((d) -> d.colorPalette),
					TEXTURE_MAP_CODEC.fieldOf("item_textures").forGetter((d) -> d.itemTextures),
					TEXTURE_MAP_CODEC.fieldOf("board_textures").forGetter((d) -> d.boardTextures)
			)
			.apply(instance, LogicBakingModelData::new));
	
	public static LogicBakingModelData get(LogicType<?, ?> type)
	{
		return ((LogicBakedModel) Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.inventory(LBR.id(type.id())))).getData();
	}
	
	private final Map<DyeColor, LogicModelColorSet> colorPalette;
	private final Map<String, ResourceLocation>     itemTextures;
	private final Map<String, ResourceLocation>     boardTextures;
	
	private LogicBakingModelData(Map<DyeColor, LogicModelColorSet> colorPalette,
								 Map<String, ResourceLocation> itemTextures,
								 Map<String, ResourceLocation> boardTextures)
	{
		this.colorPalette = Collections.unmodifiableMap(colorPalette);
		this.itemTextures = Collections.unmodifiableMap(itemTextures);
		this.boardTextures = Collections.unmodifiableMap(boardTextures);
	}
	
	public LogicModelColorSet getColorSet(DyeColor color)
	{
		return colorPalette.getOrDefault(color, LogicModelColorSet.DEFAULT);
	}
	
	public LogicModelColorSet getColorSet(DyeColor value, DyeColor fallback)
	{
		return this.getColorSet(value != null ? value : fallback);
	}
	
	public LogicModelColorSet getColorSet(Optional<DyeColor> value, DyeColor fallback)
	{
		return this.getColorSet(value.orElse(fallback));
	}
	
	public ResourceLocation getItemTexture(String texture)
	{
		return itemTextures.getOrDefault(texture, ResourceLocation.withDefaultNamespace("missingno"));
	}
	
	public ResourceLocation getItemTextureLocation(String texture)
	{
		return this.getItemTexture(texture).withPath("textures/%s.png"::formatted);
	}
	
	public ResourceLocation getBoardTexture(String texture)
	{
		return boardTextures.getOrDefault(texture, ResourceLocation.withDefaultNamespace("missingno"));
	}
	
	public ResourceLocation getBoardTextureLocation(String texture)
	{
		return this.getBoardTexture(texture).withPath("textures/%s.png"::formatted);
	}
	
	public static <T extends ModelBuilder<T>> Builder<T> builder(T parent, ExistingFileHelper existingFileHelper)
	{
		return new Builder<>(parent, existingFileHelper);
	}
	
	public static final class Builder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
	{
		private final Map<DyeColor, LogicModelColorSet> colorPalette  = Maps.newHashMap();
		private final Map<String, ResourceLocation>     itemTextures  = Maps.newHashMap();
		private final Map<String, ResourceLocation>     boardTextures = Maps.newHashMap();
		
		private Builder(T parent, ExistingFileHelper existingFileHelper)
		{
			super(LogicUnbakedModel.ID, parent, existingFileHelper, false);
		}
		
		public Builder<T> foregroundColor(DyeColor dyeColor, int color)
		{
			Assert.noneNull(dyeColor);
			colorPalette.compute(dyeColor, (__, colorSet) ->
			{
				if(colorSet == null)
				{
					colorSet = LogicModelColorSet.DEFAULT;
				}
				return new LogicModelColorSet(color, colorSet.background());
			});
			return this;
		}
		
		public Builder<T> backgroundColor(DyeColor dyeColor, int color)
		{
			Assert.noneNull(dyeColor);
			colorPalette.compute(dyeColor, (__, colorSet) ->
			{
				if(colorSet == null)
				{
					colorSet = LogicModelColorSet.DEFAULT;
				}
				return new LogicModelColorSet(colorSet.foreground(), color);
			});
			return this;
		}
		
		public Builder<T> itemTexture(String key, ResourceLocation texture)
		{
			Assert.notNull(key);
			Assert.notNull(texture);
			Assert.that(existingFileHelper.exists(texture, ModelProvider.TEXTURE), "Texture %s does not exist".formatted(texture));
			itemTextures.put(key, texture);
			return this;
		}
		
		public Builder<T> boardTexture(String key, ResourceLocation texture)
		{
			Assert.notNull(key);
			Assert.notNull(texture);
			Assert.that(existingFileHelper.exists(texture, ModelProvider.TEXTURE), "Texture %s does not exist".formatted(texture));
			boardTextures.put(key, texture);
			return this;
		}
		
		public LogicBakingModelData build()
		{
			return new LogicBakingModelData(colorPalette, itemTextures, boardTextures);
		}
		
		@Override
		public JsonObject toJson(JsonObject json)
		{
			json = super.toJson(json);
			var parsed = CODEC.encodeStart(JsonOps.INSTANCE, this.build()).getOrThrow(JsonParseException::new);
			for(var entry : parsed.getAsJsonObject().entrySet())
			{
				json.add(entry.getKey(), entry.getValue());
			}
			return json;
		}
	}
}
