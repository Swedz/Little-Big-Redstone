package net.swedz.little_big_redstone;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class LBRClientRenderPipelines
{
	public static final RenderPipeline PULSING_ALPHA = RenderPipeline.builder()
			.withVertexShader(LBR.id("pulsing_alpha"))
			.withFragmentShader(LBR.id("pulsing_alpha"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			.build();
	
	public static final RenderPipeline PULSING_TEXTURE_ALPHA = RenderPipeline.builder()
			.withVertexShader(LBR.id("pulsing_texture_alpha"))
			.withFragmentShader(LBR.id("pulsing_texture_alpha"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
			.withSampler("Sampler0")
			.build();
	
	public static final RenderPipeline PULSING_TEXTURE_LIGHTNESS = RenderPipeline.builder()
			.withVertexShader(LBR.id("pulsing_texture_lightness"))
			.withFragmentShader(LBR.id("pulsing_texture_lightness"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
			.withSampler("Sampler0")
			.build();
	
	@SubscribeEvent
	private static void register(RegisterRenderPipelinesEvent event)
	{
		event.registerPipeline(PULSING_ALPHA);
		event.registerPipeline(PULSING_TEXTURE_ALPHA);
		event.registerPipeline(PULSING_TEXTURE_LIGHTNESS);
	}
}
