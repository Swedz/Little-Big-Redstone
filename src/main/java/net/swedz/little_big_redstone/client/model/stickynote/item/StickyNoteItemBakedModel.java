package net.swedz.little_big_redstone.client.model.stickynote.item;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.client.model.stickynote.StickyNoteModelData;

import java.util.List;

public final class StickyNoteItemBakedModel implements IDynamicBakedModel
{
	private static final RenderTypeGroup RENDER_TYPES = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
	
	private final ItemTransforms transforms;
	private final boolean        useAmbientOcclusion;
	private final boolean        isGui3d;
	private final boolean        usesBlockLight;
	private final ModelState     modelState;
	
	private final TextureAtlasSprite       particle;
	private final List<TextureAtlasSprite> layers;
	
	StickyNoteItemBakedModel(ItemTransforms transforms, boolean useAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, ModelState modelState,
							 TextureAtlasSprite particle,
							 List<TextureAtlasSprite> layers)
	{
		this.transforms = transforms;
		this.useAmbientOcclusion = useAmbientOcclusion;
		this.isGui3d = isGui3d;
		this.usesBlockLight = usesBlockLight;
		this.modelState = modelState;
		
		this.particle = particle;
		this.layers = layers;
	}
	
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction direction, RandomSource random, ModelData modelData, RenderType renderType)
	{
		var data = StickyNoteModelData.get(modelData);
		
		List<BakedQuad> quads = Lists.newArrayList();
		
		for(int index = 0; index < layers.size(); index++)
		{
			var layer = layers.get(index);
			
			ExtraFaceData faceData = index == 0 ? null : new ExtraFaceData(LBRColors.stickyNoteText(data.textColor()), ExtraFaceData.DEFAULT.blockLight(), ExtraFaceData.DEFAULT.skyLight(), ExtraFaceData.DEFAULT.ambientOcclusion());
			
			var builder = CompositeModel.Baked.builder(useAmbientOcclusion, isGui3d, usesBlockLight, particle, this.getOverrides(), transforms);
			var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(index, layer, faceData);
			var layerQuads = UnbakedGeometryHelper.bakeElements(unbaked, (__) -> layer, modelState);
			quads.addAll(layerQuads);
		}
		
		return quads;
	}
	
	@Override
	public boolean useAmbientOcclusion()
	{
		return useAmbientOcclusion;
	}
	
	@Override
	public boolean isGui3d()
	{
		return isGui3d;
	}
	
	@Override
	public boolean usesBlockLight()
	{
		return usesBlockLight;
	}
	
	@Override
	public boolean isCustomRenderer()
	{
		return true;
	}
	
	@Override
	public TextureAtlasSprite getParticleIcon()
	{
		return particle;
	}
	
	@Override
	public ItemOverrides getOverrides()
	{
		return ItemOverrides.EMPTY;
	}
	
	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data)
	{
		return ChunkRenderTypeSet.of(RenderType.CUTOUT);
	}
	
	@Override
	public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform)
	{
		IDynamicBakedModel.super.applyTransform(transformType, poseStack, applyLeftHandTransform);
		transforms.getTransform(transformType).apply(applyLeftHandTransform, poseStack);
		return this;
	}
}
