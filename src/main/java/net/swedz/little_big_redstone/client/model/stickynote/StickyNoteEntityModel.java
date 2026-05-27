package net.swedz.little_big_redstone.client.model.stickynote;

import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.swedz.little_big_redstone.LBR;

import java.util.List;
import java.util.Optional;

public record StickyNoteEntityModel(

) implements DynamicBlockStateModel
{
	@Override
	public void collectParts(
			BlockAndTintGetter level,
			BlockPos pos,
			BlockState state,
			RandomSource random,
			List<BlockStateModelPart> parts
	)
	{
	
	}
	
	@Override
	public Material.Baked particleMaterial()
	{
		return null;
	}
	
	@Override
	public int materialFlags()
	{
		return 0;
	}
	
	public record Unbaked(
			Optional<Transformation> transformation,
			Material paperTexture
	) implements CustomUnbakedBlockStateModel
	{
		public static final Identifier ID = LBR.id("sticky_note_entity");
		
		public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(Unbaked::transformation),
						Material.CODEC.fieldOf("paper_texture").forGetter(Unbaked::paperTexture)
				)
				.apply(instance, Unbaked::new));
		
		private static ModelTemplate PAPER_TEMPLATE = new ModelTemplate(Optional.of(LBR.id("block/sticky_note_paper")), Optional.empty(), TextureSlot.TEXTURE);
		
		@Override
		public MapCodec<? extends CustomUnbakedBlockStateModel> codec()
		{
			return CODEC;
		}
		
		@Override
		public BlockStateModel bake(ModelBaker baker)
		{
			// TODO 26.1 somehow use this to bake the model? PAPER_TEMPLATE.createBaseTemplate()
			return null;
		}
		
		@Override
		public void resolveDependencies(Resolver resolver)
		{
			resolver.markDependency(LBR.id("block/sticky_note_paper"));
			resolver.markDependency(LBR.id("block/sticky_note_text"));
		}
	}
}
