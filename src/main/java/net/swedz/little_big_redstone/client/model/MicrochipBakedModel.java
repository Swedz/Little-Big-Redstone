package net.swedz.little_big_redstone.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlock;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class MicrochipBakedModel implements IDynamicBakedModel
{
	private final ItemTransforms transforms;
	private final boolean        useAmbientOcclusion;
	private final boolean        isGui3d;
	private final boolean        usesBlockLight;
	
	private final TextureAtlasSprite                 particle;
	private final Map<Direction, TextureAtlasSprite> baseTextures;
	private final Map<Direction, TextureAtlasSprite> sideOverlayTextures;
	private final Map<Direction, TextureAtlasSprite> signalOnOverlayTextures;
	private final Map<Direction, TextureAtlasSprite> signalOffOverlayTextures;
	
	MicrochipBakedModel(ItemTransforms transforms, boolean useAmbientOcclusion, boolean isGui3d, boolean usesBlockLight,
						TextureAtlasSprite particle,
						Map<Direction, TextureAtlasSprite> baseTextures,
						Map<Direction, TextureAtlasSprite> sideOverlayTextures,
						Map<Direction, TextureAtlasSprite> signalOnOverlayTextures,
						Map<Direction, TextureAtlasSprite> signalOffOverlayTextures)
	{
		this.transforms = transforms;
		this.useAmbientOcclusion = useAmbientOcclusion;
		this.isGui3d = isGui3d;
		this.usesBlockLight = usesBlockLight;
		this.particle = particle;
		this.baseTextures = baseTextures;
		this.sideOverlayTextures = sideOverlayTextures;
		this.signalOnOverlayTextures = signalOnOverlayTextures;
		this.signalOffOverlayTextures = signalOffOverlayTextures;
	}
	
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction cullDirection, RandomSource random, ModelData data, RenderType renderType)
	{
		List<BakedQuad> quads = Lists.newArrayList();
		quads.addAll(new ElementBuilder().allFaces().getQuads(baseTextures::get));
		quads.addAll(new ElementBuilder().allFaces().getQuads(sideOverlayTextures::get));
		quads.addAll(new ElementBuilder().allFaces().getQuads((direction) -> (state != null && state.getValue(MicrochipBlock.getDirectionalState(direction)) ? signalOnOverlayTextures : signalOffOverlayTextures).get(direction)));
		return quads;
	}
	
	private final static class ElementBuilder
	{
		private final Map<Direction, BlockElementFace> faces = Maps.newHashMap();
		
		public ElementBuilder face(Direction direction)
		{
			faces.put(direction, new BlockElementFace(null, -1, null, new BlockFaceUV(null, 0), null, new MutableObject<>()));
			return this;
		}
		
		public ElementBuilder allFaces()
		{
			for(var direction : Direction.values())
			{
				this.face(direction);
			}
			return this;
		}
		
		public List<BakedQuad> getQuads(Function<Direction, TextureAtlasSprite> spriteGetter)
		{
			List<BakedQuad> quads = Lists.newArrayList();
			var element = new BlockElement(new Vector3f(0, 0, 0), new Vector3f(16, 16, 16), faces, null, true);
			for(var direction : faces.keySet())
			{
				quads.add(BlockModel.bakeFace(element, faces.get(direction), spriteGetter.apply(direction), direction, BlockModelRotation.X0_Y0));
			}
			return quads;
		}
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
