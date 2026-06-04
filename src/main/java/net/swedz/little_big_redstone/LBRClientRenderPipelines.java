package net.swedz.little_big_redstone;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class LBRClientRenderPipelines
{
	public static final RenderPipeline LOGIC_SCANLINE = RenderPipeline
			.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
			.withLocation(LBR.id("pipelines/logic_scanline"))
			.withVertexShader(LBR.id("core/logic_scanline"))
			.withFragmentShader(LBR.id("core/logic_scanline"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
			.withSampler("Sampler0")
			.build();
	
	public static final RenderPipeline LOGIC_SCANLINE_ENTITY = RenderPipeline
			.builder(RenderPipelines.MATRICES_FOG_LIGHT_DIR_SNIPPET)
			.withLocation(LBR.id("pipelines/logic_scanline_entity"))
			.withVertexShader(LBR.id("core/logic_scanline"))
			.withFragmentShader(LBR.id("core/logic_scanline"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
			.withSampler("Sampler0")
			.withSampler("Sampler2")
			.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
			.withShaderDefine("USE_LIGHTMAP")
			.build();
	
	public static final RenderPipeline PULSING_ALPHA = RenderPipeline
			.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
			.withLocation(LBR.id("pipelines/pulsing_alpha"))
			.withVertexShader(LBR.id("core/pulsing_alpha"))
			.withFragmentShader(LBR.id("core/pulsing_alpha"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			.build();
	
	public static final RenderPipeline PULSING_TEXTURE_ALPHA = RenderPipeline
			.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
			.withLocation(LBR.id("pipelines/pulsing_texture_alpha"))
			.withVertexShader(LBR.id("core/pulsing_texture_alpha"))
			.withFragmentShader(LBR.id("core/pulsing_texture_alpha"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
			.withSampler("Sampler0")
			.build();
	
	public static final RenderPipeline PULSING_TEXTURE_LIGHTNESS = RenderPipeline
			.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
			.withLocation(LBR.id("pipelines/pulsing_texture_lightness"))
			.withVertexShader(LBR.id("core/pulsing_texture_lightness"))
			.withFragmentShader(LBR.id("core/pulsing_texture_lightness"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
			.withSampler("Sampler0")
			.build();
	
	public static final RenderPipeline MICROCHIP_OVERLAY = RenderPipeline
			.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(LBR.id("pipeline/microchip_overlay"))
			.withVertexShader("core/entity")
			.withFragmentShader("core/entity")
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("NO_OVERLAY")
			.withShaderDefine("NO_CARDINAL_LIGHTING")
			.withSampler("Sampler0")
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
			.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
			.build();
	
	@SubscribeEvent
	private static void register(RegisterRenderPipelinesEvent event)
	{
		event.registerPipeline(LOGIC_SCANLINE);
		event.registerPipeline(LOGIC_SCANLINE_ENTITY);
		event.registerPipeline(PULSING_ALPHA);
		event.registerPipeline(PULSING_TEXTURE_ALPHA);
		event.registerPipeline(PULSING_TEXTURE_LIGHTNESS);
	}
}
