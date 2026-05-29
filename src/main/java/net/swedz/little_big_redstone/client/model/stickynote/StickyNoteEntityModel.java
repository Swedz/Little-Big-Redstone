package net.swedz.little_big_redstone.client.model.stickynote;

import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.ComposedModelState;
import org.joml.Matrix4fc;

import java.util.Optional;

public record StickyNoteEntityModel(
		BlockStateModelPart paperModel,
		Optional<BlockStateModelPart> textModel
) implements ItemModel
{
	private static final ModelState MODEL_STATE = new ComposedModelState(BlockModelRotation.IDENTITY, Transformation.IDENTITY);
	
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
		var paperLayer = output.newLayer();
		paperLayer.prepareQuadList().addAll(paperModel.getQuads(null));
		
		textModel.ifPresent((model) ->
		{
			var textLayer = output.newLayer();
			textLayer.prepareQuadList().addAll(model.getQuads(null));
		});
	}
	
	public record Unbaked(
			Identifier paperModel,
			Optional<Identifier> textModel
	) implements ItemModel.Unbaked
	{
		public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						Identifier.CODEC.fieldOf("paper_model").forGetter(Unbaked::paperModel),
						Identifier.CODEC.optionalFieldOf("text_model").forGetter(Unbaked::textModel)
				)
				.apply(instance, Unbaked::new));
		
		@Override
		public MapCodec<? extends ItemModel.Unbaked> type()
		{
			return CODEC;
		}
		
		@Override
		public ItemModel bake(BakingContext context, Matrix4fc transformation)
		{
			return new StickyNoteEntityModel(
					SimpleModelWrapper.bake(context.blockModelBaker(), paperModel, MODEL_STATE),
					textModel.map((modelId) -> SimpleModelWrapper.bake(context.blockModelBaker(), modelId, MODEL_STATE))
			);
		}
		
		@Override
		public void resolveDependencies(Resolver resolver)
		{
			resolver.markDependency(paperModel);
			textModel.ifPresent(resolver::markDependency);
		}
	}
}
