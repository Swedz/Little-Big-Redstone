package net.swedz.little_big_redstone.client.model.stickynote;

import com.google.common.collect.Lists;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.ComposedModelState;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.LBRComponents;
import org.joml.Matrix4fc;

import java.util.List;
import java.util.Optional;

public record StickyNoteItemModel(
		BakingContext context,
		Matrix4fc transformation,
		ItemTransforms itemTransforms,
		Material.Baked noteTexture,
		Material.Baked textTexture
) implements ItemModel
{
	private static final ModelDebugName DEBUG_NAME  = () -> "StickyNoteItemModel";
	private static final ModelState     MODEL_STATE = new ComposedModelState(BlockModelRotation.IDENTITY, Transformation.IDENTITY);
	
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
		var textDyeColor = stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR);
		int textColor = LBRColors.stickyNoteText(textDyeColor);
		var textFaceData = new ExtraFaceData(textColor, ExtraFaceData.DEFAULT.lightEmission(), ExtraFaceData.DEFAULT.ambientOcclusion());
		
		List<ItemModel> models = Lists.newArrayList();
		models.add(this.bakeLayer(noteTexture, 0, ExtraFaceData.DEFAULT));
		models.add(this.bakeLayer(textTexture, 1, ExtraFaceData.DEFAULT));
		models.forEach((model) -> model.update(output, stack, modelResolver, displayContext, level, owner, seed));
	}
	
	private ItemModel bakeLayer(Material.Baked material, int index, ExtraFaceData extraFaceData)
	{
		var baker = context.blockModelBaker();
		var overlayRenderProperties = new ModelRenderProperties(false, material, itemTransforms);
		var overlayQuads = baker.compute(new ItemModelGenerator.ItemLayerKey(material, MODEL_STATE, index, extraFaceData));
		return new CuboidItemModelWrapper(List.of(), overlayQuads, overlayRenderProperties, transformation);
	}
	
	public record Unbaked(
			Optional<Transformation> transformation,
			Material noteTexture,
			Material textTexture
	) implements ItemModel.Unbaked
	{
		public static final Identifier ID = LBR.id("logic");
		
		public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(Unbaked::transformation),
						Material.CODEC.fieldOf("note_texture").forGetter(Unbaked::noteTexture),
						Material.CODEC.fieldOf("text_texture").forGetter(Unbaked::textTexture)
				)
				.apply(instance, Unbaked::new));
		
		private static final Identifier ITEM_STICKY_NOTE = LBR.id("item/sticky_note");
		
		@Override
		public MapCodec<? extends ItemModel.Unbaked> type()
		{
			return CODEC;
		}
		
		@Override
		public ItemModel bake(BakingContext context, Matrix4fc transformation)
		{
			var baseItemModel = context.blockModelBaker().getModel(ITEM_STICKY_NOTE);
			var itemTransforms = baseItemModel.getTopTransforms();
			var materials = context.blockModelBaker().materials();
			
			return new StickyNoteItemModel(
					context,
					Transformation.compose(transformation, this.transformation),
					itemTransforms,
					materials.get(noteTexture, DEBUG_NAME),
					materials.get(textTexture, DEBUG_NAME)
			);
		}
		
		@Override
		public void resolveDependencies(Resolver resolver)
		{
			resolver.markDependency(ITEM_STICKY_NOTE);
		}
	}
}
