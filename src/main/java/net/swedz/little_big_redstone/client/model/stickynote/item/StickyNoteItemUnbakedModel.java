package net.swedz.little_big_redstone.client.model.stickynote.item;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.helper.ModelHelper;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public final class StickyNoteItemUnbakedModel implements IUnbakedGeometry<StickyNoteItemUnbakedModel>
{
	public static final ResourceLocation                            ID     = LBR.id("sticky_note_item");
	public static final IGeometryLoader<StickyNoteItemUnbakedModel> LOADER = (json, context) -> new StickyNoteItemUnbakedModel(
			ModelHelper.gatherTextures(json, "item_textures").values(),
			ModelHelper.gatherTextures(json, "microchip_textures").values()
	);
	
	private final Collection<Material> itemTextureLayers, microchipTextureLayers;
	
	private StickyNoteItemUnbakedModel(Collection<Material> itemTextureLayers, Collection<Material> microchipTextureLayers)
	{
		this.itemTextureLayers = itemTextureLayers;
		this.microchipTextureLayers = microchipTextureLayers;
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
	{
		var rootTransform = context.getRootTransform();
		if(!rootTransform.isIdentity())
		{
			modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
		}
		
		var particle = spriteGetter.apply(context.getMaterial("particle"));
		
		List<TextureAtlasSprite> itemSpriteLayers = Lists.newArrayList();
		for(var textureLayer : itemTextureLayers)
		{
			itemSpriteLayers.add(spriteGetter.apply(textureLayer));
		}
		List<TextureAtlasSprite> microchipSpriteLayers = Lists.newArrayList();
		for(var textureLayer : microchipTextureLayers)
		{
			microchipSpriteLayers.add(spriteGetter.apply(textureLayer));
		}
		
		return new StickyNoteItemBakedModel(
				context.getTransforms(),
				context.useAmbientOcclusion(),
				context.isGui3d(),
				context.useBlockLight(),
				modelState,
				particle,
				itemSpriteLayers,
				microchipSpriteLayers
		);
	}
}
