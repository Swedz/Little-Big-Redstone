package net.swedz.little_big_redstone.microchip.object.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

public final class LogicTypes
{
	public static final ResourceKey<Registry<LogicType<?, ?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(LBR.id("logic"));
	
	public static final Registry<LogicType<?, ?>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY)
			.sync(true)
			.create();
	
	private static final Codec<ResourceLocation> RESOURCE_LOCATION_CODEC = Codec.STRING
			.comapFlatMap(
					(value) ->
					{
						if(ResourceLocation.isValidPath(value))
						{
							return DataResult.success(LBR.id(value));
						}
						return ResourceLocation.read(value);
					},
					ResourceLocation::toString
			)
			.stable();
	
	/**
	 * <p>Prior to 1.8.3-beta, {@link LogicType} identifiers were a string path and the namespace was assumed to always
	 * be LBR's. So, in order for old data to be able to be loaded on newer versions, this codec cannot simply be
	 * {@link Registry#byNameCodec()}. That uses the vanilla {@link ResourceLocation#CODEC}, which would mean that the
	 * Minecraft namespace would be used by default. This codec instead uses the
	 * {@link LogicTypes#RESOURCE_LOCATION_CODEC}, which defaults to the LBR namespace instead of the Minecraft one.
	 * Otherwise, this codec is merely a replication of what {@link Registry#byNameCodec()} would return.</p>
	 *
	 * <p>Will be removed in MC 26.1+.</p>
	 *
	 * @see Registry#byNameCodec()
	 */
	@Deprecated(since = "1.8.3-beta", forRemoval = true)
	private static final Codec<LogicType<?, ?>> CODEC = RESOURCE_LOCATION_CODEC
			.comapFlatMap(
					(id) -> REGISTRY
							.getHolder(id)
							.map(DataResult::success)
							.orElseGet(() -> DataResult
									.error(() -> "Unknown registry key in " + REGISTRY.key() + ": " + id)),
					(holder) -> holder.key().location()
			)
			.flatComapMap(
					Holder.Reference::value,
					(value) ->
					{
						var holder = REGISTRY.wrapAsHolder(value);
						if(holder.getDelegate() instanceof Holder.Reference<LogicType<?, ?>> reference)
						{
							return DataResult.success(reference);
						}
						return DataResult.error(() -> "Unregistered holder in " + REGISTRY.key() + ": " + holder);
					}
			);
	
	static final Codec<LogicComponent> COMPONENT_CODEC = CODEC
			.dispatch(LogicComponent::type, LogicType::codec);
	
	static final StreamCodec<ByteBuf, LogicComponent> COMPONENT_STREAM_CODEC = CodecHelper
			.forRegistryStream(REGISTRY)
			.dispatch(LogicComponent::type, LogicType::streamCodec);
	
	static final Codec<LogicConfig> CONFIG_CODEC = CODEC
			.dispatch(LogicConfig::type, LogicType::configCodec);
	
	static final StreamCodec<ByteBuf, LogicConfig> CONFIG_STREAM_CODEC = CodecHelper
			.forRegistryStream(REGISTRY)
			.dispatch(LogicConfig::type, LogicType::configStreamCodec);
}
