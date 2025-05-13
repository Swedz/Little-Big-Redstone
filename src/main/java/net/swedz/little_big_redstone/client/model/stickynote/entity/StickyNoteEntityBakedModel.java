package net.swedz.little_big_redstone.client.model.stickynote.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.swedz.little_big_redstone.client.model.stickynote.StickyNoteModelData;

import java.util.List;

public final class StickyNoteEntityBakedModel implements IDynamicBakedModel
{
	private final ItemTransforms transforms;
	private final boolean        useAmbientOcclusion;
	private final boolean        isGui3d;
	private final boolean        usesBlockLight;
	
	private final TextureAtlasSprite particle;
	
	private final ImmutableList<BakedModel> baseLayer, textLayer;
	
	StickyNoteEntityBakedModel(ItemTransforms transforms, boolean useAmbientOcclusion, boolean isGui3d, boolean usesBlockLight,
							   TextureAtlasSprite particle,
							   ImmutableList<BakedModel> baseLayer, ImmutableList<BakedModel> textLayer)
	{
		this.transforms = transforms;
		this.useAmbientOcclusion = useAmbientOcclusion;
		this.isGui3d = isGui3d;
		this.usesBlockLight = usesBlockLight;
		this.particle = particle;
		this.baseLayer = baseLayer;
		this.textLayer = textLayer;
	}
	
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData modelData, RenderType renderType)
	{
		var data = StickyNoteModelData.get(modelData);
		List<BakedQuad> quads = Lists.newArrayList();
		for(var model : baseLayer)
		{
			quads.addAll(model.getQuads(state, side, random, modelData, renderType));
		}
		for(var model : textLayer)
		{
			for(var quad : model.getQuads(state, side, random, modelData, renderType))
			{
				var copy = new BakedQuad(quad.getVertices(), 0, quad.getDirection(), quad.getSprite(), quad.isShade(), quad.hasAmbientOcclusion());
				QuadTransformers.applyingColor(0xFF000000 | data.textColor().getTextColor()).processInPlace(copy);
				quads.add(copy);
			}
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
		return false;
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
