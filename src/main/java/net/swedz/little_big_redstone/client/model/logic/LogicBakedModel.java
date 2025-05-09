package net.swedz.little_big_redstone.client.model.logic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class LogicBakedModel implements IDynamicBakedModel
{
	private final LogicBakingModelData bakingModelData;
	
	private final Map<DyeColor, BakedModel> itemModels;
	private final BakedModel                fallback;
	
	LogicBakedModel(LogicBakingModelData bakingModelData,
					Map<DyeColor, BakedModel> itemModels)
	{
		this.bakingModelData = bakingModelData;
		this.itemModels = Collections.unmodifiableMap(itemModels);
		this.fallback = itemModels.get(DyeColor.WHITE);
	}
	
	public LogicBakingModelData getData()
	{
		return bakingModelData;
	}
	
	public BakedModel getModel(LogicComponent<?, ?> component)
	{
		if(component == null)
		{
			return fallback;
		}
		var color = component.color().orElse(DyeColor.WHITE);
		return itemModels.getOrDefault(color, fallback);
	}
	
	@Override
	public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous)
	{
		var component = stack.get(LBRComponents.LOGIC);
		return List.of(this.getModel(component));
	}
	
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData modelData, RenderType renderType)
	{
		LBR.LOGGER.error("getQuads of LogicBakedModel was called, this should never happen!");
		return List.of();
	}
	
	@Override
	public boolean useAmbientOcclusion()
	{
		return fallback.useAmbientOcclusion();
	}
	
	@Override
	public boolean isGui3d()
	{
		return fallback.isGui3d();
	}
	
	@Override
	public boolean usesBlockLight()
	{
		return fallback.usesBlockLight();
	}
	
	@Override
	public boolean isCustomRenderer()
	{
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public TextureAtlasSprite getParticleIcon()
	{
		return fallback.getParticleIcon();
	}
	
	@Override
	public ItemOverrides getOverrides()
	{
		return fallback.getOverrides();
	}
	
	@Override
	public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform)
	{
		fallback.applyTransform(transformType, poseStack, applyLeftHandTransform);
		return this;
	}
}
