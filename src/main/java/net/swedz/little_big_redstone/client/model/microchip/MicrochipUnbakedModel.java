package net.swedz.little_big_redstone.client.model.microchip;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.swedz.little_big_redstone.LBR;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class MicrochipUnbakedModel implements IUnbakedGeometry<MicrochipUnbakedModel>
{
	public static final ResourceLocation                       ID     = LBR.id("microchip");
	public static final IGeometryLoader<MicrochipUnbakedModel> LOADER = (json, context) -> new MicrochipUnbakedModel();
	
	private MicrochipUnbakedModel()
	{
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ItemOverrides overrides)
	{
		var baseTextureFallback = spriteGetter.apply(context.getMaterial("base"));
		var signalOnOverlayTextureFallback = spriteGetter.apply(context.getMaterial("signal_on_overlay"));
		var signalOffOverlayTextureFallback = spriteGetter.apply(context.getMaterial("signal_off_overlay"));
		Map<Direction, TextureAtlasSprite> baseTextures = Maps.newHashMap();
		Map<Direction, TextureAtlasSprite> sideOverlayTextures = Maps.newHashMap();
		Map<Direction, TextureAtlasSprite> signalOnOverlayTextures = Maps.newHashMap();
		Map<Direction, TextureAtlasSprite> signalOffOverlayTextures = Maps.newHashMap();
		for(var direction : Direction.values())
		{
			var directionName = direction.name().toLowerCase(Locale.ROOT);
			baseTextures.put(direction, getMaterialOrDefault(context, spriteGetter, "base_" + directionName, baseTextureFallback));
			sideOverlayTextures.put(direction, spriteGetter.apply(context.getMaterial("side_overlay_" + directionName)));
			signalOnOverlayTextures.put(direction, getMaterialOrDefault(context, spriteGetter, "signal_on_overlay_" + directionName, signalOnOverlayTextureFallback));
			signalOffOverlayTextures.put(direction, getMaterialOrDefault(context, spriteGetter, "signal_off_overlay_" + directionName, signalOffOverlayTextureFallback));
		}
		return new MicrochipBakedModel(
				context.getTransforms(),
				context.useAmbientOcclusion(),
				context.isGui3d(),
				context.useBlockLight(),
				spriteGetter.apply(context.getMaterial("particle")),
				baseTextures,
				sideOverlayTextures,
				signalOnOverlayTextures, signalOffOverlayTextures
		);
	}
	
	private static TextureAtlasSprite getMaterialOrDefault(IGeometryBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, String name, TextureAtlasSprite fallback)
	{
		return context.hasMaterial(name) ? spriteGetter.apply(context.getMaterial(name)) : fallback;
	}
}
