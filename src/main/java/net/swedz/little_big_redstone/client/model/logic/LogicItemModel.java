package net.swedz.little_big_redstone.client.model.logic;

import com.google.common.collect.Lists;
import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.model.ComposedModelState;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItemDisplayContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public record LogicItemModel(
		BakingContext context,
		Matrix4fc transformation,
		ItemTransforms itemTransforms,
		LogicModelColorPalette colorPalette,
		TextureMap itemTextures,
		TextureMap boardTextures
) implements ItemModel
{
	private static final ModelDebugName DEBUG_NAME  = () -> "LogicItemModel";
	private static final ModelState     MODEL_STATE = new ComposedModelState(BlockModelRotation.IDENTITY, Transformation.IDENTITY);
	
	public static LogicItemModel get(LogicComponent<?, ?> component)
	{
		var modelId = LBR.id(component.type().id());
		var model = Minecraft.getInstance().getModelManager().getItemModel(modelId);
		if(!(model instanceof LogicItemModel logicItemModel))
		{
			throw new IllegalStateException("Model " + modelId + " is not a LogicItemModel");
		}
		return logicItemModel;
	}
	
	@Override
	public void update(
			ItemStackRenderState output,
			ItemStack stack,
			ItemModelResolver modelResolver,
			ItemDisplayContext displayContext,
			ClientLevel level,
			ItemOwner owner,
			int seed
	)
	{
		var component = stack.get(LBRComponents.LOGIC);
		var textureMap = displayContext == LBRItemDisplayContext.MICROCHIP_GUI ? boardTextures : itemTextures;
		var colorSet = colorPalette.getColorSet(component, DyeColor.WHITE);
		
		List<ItemModel> models = Lists.newArrayList();
		models.add(this.bakeLayer(textureMap, 0, "background", colorSet.backgroundFaceData()));
		models.add(this.bakeLayer(textureMap, 1, "border", colorSet.foregroundFaceData()));
		models.add(this.bakeLayer(textureMap, 2, "icon", colorSet.foregroundFaceData()));
		models.forEach((model) -> model.update(output, stack, modelResolver, displayContext, level, owner, seed));
	}
	
	private ItemModel bakeLayer(TextureMap textureMap, int index, String texture, ExtraFaceData extraFaceData)
	{
		var baker = context.blockModelBaker();
		var materials = baker.materials();
		
		var sprite = textureMap.get(texture);
		var overlayMaterial = materials.get(new Material(sprite), DEBUG_NAME);
		var overlayRenderProperties = new ModelRenderProperties(false, overlayMaterial, itemTransforms);
		// TODO 26.1 logic scanline render type
		var renderType = /*index == 2 ? LBRClientRenderTypes.logicScanline() : */NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get();
		var overlayQuads = baker.compute(new ItemLayerKey(overlayMaterial, MODEL_STATE, index, renderType, extraFaceData));
		return new CuboidItemModelWrapper(List.of(), overlayQuads, overlayRenderProperties, transformation);
	}
	
	// TODO 26.1 move to tesseract?
	private record ItemLayerKey(
			Material.Baked material,
			ModelState modelState,
			int layerIndex,
			RenderType renderType,
			ExtraFaceData faceData
	) implements ModelBaker.SharedOperationKey<QuadCollection>
	{
		@Override
		public QuadCollection compute(ModelBaker modelBakery)
		{
			var builder = new QuadCollection.Builder();
			var sprite = material.sprite();
			var transparency = sprite.transparency();
			var materialInfo = new BakedQuad.MaterialInfo(
					sprite,
					ChunkSectionLayer.byTransparency(transparency),
					renderType,
					layerIndex,
					true,
					faceData.lightEmission(),
					faceData.ambientOcclusion()
			);
			bakeExtrudedSprite(builder, modelBakery.interner(), modelState, modelBakery.interner().materialInfo(materialInfo), faceData);
			return builder.build();
		}
	}
	
	// TODO 26.1 remove these
	private static final CuboidFace.UVs SOUTH_FACE_UVS = new CuboidFace.UVs(0.0F, 0.0F, 16.0F, 16.0F);
	private static final CuboidFace.UVs NORTH_FACE_UVS = new CuboidFace.UVs(16.0F, 0.0F, 0.0F, 16.0F);
	
	// TODO 26.1 access transform in ItemModelGenerator
	private static void bakeExtrudedSprite(
			QuadCollection.Builder builder,
			ModelBaker.Interner interner,
			ModelState modelState,
			BakedQuad.MaterialInfo materialInfo,
			net.neoforged.neoforge.client.model.ExtraFaceData faceData
	)
	{
		Vector3f from = new Vector3f(0.0F, 0.0F, 7.5F);
		Vector3f to = new Vector3f(16.0F, 16.0F, 8.5F);
		builder.addUnculledFace(FaceBakery.bakeQuad(interner, from, to, SOUTH_FACE_UVS, Quadrant.R0, materialInfo, Direction.SOUTH, modelState, null, faceData));
		builder.addUnculledFace(FaceBakery.bakeQuad(interner, from, to, NORTH_FACE_UVS, Quadrant.R0, materialInfo, Direction.NORTH, modelState, null, faceData));
		ItemModelGenerator.bakeSideFaces(builder, interner, modelState, materialInfo, faceData);
	}
	
	public record Unbaked(
			Optional<Transformation> transformation,
			LogicModelColorPalette colorPalette,
			TextureMap itemTextures,
			TextureMap boardTextures
	) implements ItemModel.Unbaked
	{
		public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(Unbaked::transformation),
						LogicModelColorPalette.CODEC.fieldOf("color_palette").forGetter(Unbaked::colorPalette),
						TextureMap.CODEC.fieldOf("item_textures").forGetter(Unbaked::itemTextures),
						TextureMap.CODEC.fieldOf("board_textures").forGetter(Unbaked::boardTextures)
				)
				.apply(instance, Unbaked::new));
		
		private static final Identifier ITEM_GENERATED = Identifier.withDefaultNamespace("item/generated");
		
		@Override
		public MapCodec<? extends ItemModel.Unbaked> type()
		{
			return CODEC;
		}
		
		@Override
		public ItemModel bake(BakingContext context, Matrix4fc transformation)
		{
			var baseItemModel = context.blockModelBaker().getModel(ITEM_GENERATED);
			var itemTransforms = baseItemModel.getTopTransforms();
			
			return new LogicItemModel(
					context,
					Transformation.compose(transformation, this.transformation),
					itemTransforms,
					colorPalette,
					itemTextures,
					boardTextures
			);
		}
		
		@Override
		public void resolveDependencies(Resolver resolver)
		{
			resolver.markDependency(ITEM_GENERATED);
		}
	}
}
