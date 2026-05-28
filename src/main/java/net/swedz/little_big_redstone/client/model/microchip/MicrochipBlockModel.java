package net.swedz.little_big_redstone.client.model.microchip;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.ComposedModelState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.swedz.little_big_redstone.LBR;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public final class MicrochipBlockModel implements DynamicBlockStateModel
{
	private static final ModelDebugName DEBUG_NAME  = () -> "MicrochipBlockModel";
	private static final ModelState     MODEL_STATE = new ComposedModelState(BlockModelRotation.IDENTITY, Transformation.IDENTITY);
	
	private final ModelBaker                   baker;
	private final FaceTextures                 fallback;
	private final Map<Direction, FaceTextures> sides;
	
	private Material.Baked particleMaterial;
	
	private final Map<MicrochipModelData, List<BlockStateModelPart>> cache = Maps.newHashMap();
	
	private MicrochipBlockModel(
			ModelBaker baker,
			FaceTextures fallback,
			Map<Direction, FaceTextures> sides
	)
	{
		this.baker = baker;
		this.fallback = fallback;
		this.sides = sides;
		this.particleMaterial = fallback.base().map((material) -> baker.materials().get(material, DEBUG_NAME)).orElse(null);
	}
	
	@Override
	public void collectParts(
			BlockAndTintGetter level,
			BlockPos pos,
			BlockState state,
			RandomSource random,
			List<BlockStateModelPart> output
	)
	{
		var data = level.getModelData(pos).get(MicrochipModelData.KEY);
		if(data == null)
		{
			data = MicrochipModelData.DEFAULT;
		}
		
		var parts = cache.get(data);
		if(parts == null)
		{
			parts = Collections.unmodifiableList(this.buildModel(data));
			cache.put(data, parts);
		}
		output.addAll(parts);
	}
	
	@Override
	public Material.Baked particleMaterial()
	{
		return particleMaterial;
	}
	
	@Override
	public int materialFlags()
	{
		// TODO 26.1
		return 0;
	}
	
	private List<BlockStateModelPart> buildModel(MicrochipModelData data)
	{
		List<BlockStateModelPart> parts = Lists.newArrayList();
		
		parts.add(bakeLayerAsPart(baker, this.prepareLayer((direction, textures) -> textures.base(), true)));
		
		parts.add(bakeLayerAsPart(baker, this.prepareLayer((direction, textures) ->
		{
			if(direction != null)
			{
				return data.side(direction) ?
						textures.signalOnOverlay() :
						textures.signalOffOverlay();
			}
			return Optional.empty();
		}, false)));
		
		return parts;
	}
	
	private UnbakedModel prepareLayer(BiFunction<Direction, FaceTextures, Optional<Material>> materialFunction, boolean particles)
	{
		var fullFaceUv = new CuboidFace.UVs(0, 0, 16, 16);
		var faces = Util.makeEnumMap(
				Direction.class,
				(direction) -> new CuboidFace(
						direction,
						-1,
						direction.getName(),
						fullFaceUv,
						Quadrant.R0
				)
		);
		var cube = new CuboidModelElement(
				new Vector3f(0, 0, 0),
				new Vector3f(16, 16, 16),
				faces
		);
		var textures = new TextureSlots.Data.Builder();
		if(particles)
		{
			var particleMaterial = materialFunction.apply(null, fallback);
			particleMaterial.ifPresent((m) -> textures.addTexture("particle", m));
		}
		for(var direction : Direction.values())
		{
			var faceTextures = sides.getOrDefault(direction, fallback);
			var material = materialFunction.apply(direction, faceTextures);
			if(material.isEmpty() && faceTextures != fallback)
			{
				material = materialFunction.apply(direction, fallback);
			}
			material.ifPresent((m) -> textures.addTexture(direction.getName(), m));
		}
		return new CuboidModel(
				new UnbakedCuboidGeometry(List.of(cube)),
				null,
				null,
				ItemTransforms.NO_TRANSFORMS,
				textures.build(),
				null
		);
	}
	
	private static BlockStateModelPart bakeLayerAsPart(ModelBaker baker, UnbakedModel unbakedModel)
	{
		var resolvedModel = baker.resolveInlineModel(unbakedModel, DEBUG_NAME);
		var quads = resolvedModel.bakeTopGeometry(resolvedModel.getTopTextureSlots(), baker, MODEL_STATE);
		return new SimpleModelWrapper(quads, resolvedModel.getTopAmbientOcclusion(), resolvedModel.resolveParticleMaterial(resolvedModel.getTopTextureSlots(), baker));
	}
	
	public record FaceTextures(
			Optional<Material> base,
			Optional<Material> signalOnOverlay,
			Optional<Material> signalOffOverlay
	)
	{
		public static final Codec<FaceTextures> CODEC = RecordCodecBuilder.create((instance) -> instance
				.group(
						Material.CODEC.optionalFieldOf("base").forGetter(FaceTextures::base),
						Material.CODEC.optionalFieldOf("signal_on_overlay").forGetter(FaceTextures::signalOnOverlay),
						Material.CODEC.optionalFieldOf("signal_off_overlay").forGetter(FaceTextures::signalOffOverlay)
				)
				.apply(instance, FaceTextures::new));
	}
	
	public record Unbaked(
			FaceTextures fallback,
			Map<Direction, FaceTextures> sides
	) implements CustomUnbakedBlockStateModel
	{
		public static final Identifier ID = LBR.id("microchip");
		
		public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
				.group(
						FaceTextures.CODEC.fieldOf("fallback").forGetter(Unbaked::fallback),
						Codec.unboundedMap(Direction.CODEC, FaceTextures.CODEC).fieldOf("sides").forGetter(Unbaked::sides)
				)
				.apply(instance, Unbaked::new));
		
		@Override
		public MapCodec<? extends CustomUnbakedBlockStateModel> codec()
		{
			return CODEC;
		}
		
		@Override
		public BlockStateModel bake(ModelBaker baker)
		{
			return new MicrochipBlockModel(baker, fallback, sides);
		}
		
		@Override
		public void resolveDependencies(Resolver resolver)
		{
		}
	}
}
