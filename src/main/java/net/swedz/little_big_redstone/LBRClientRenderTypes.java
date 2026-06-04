package net.swedz.little_big_redstone;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class LBRClientRenderTypes
{
	@SuppressWarnings("deprecation")
	public static final RenderType MICROCHIP_OVERLAY = RenderType.create(
			"microchip_overlay",
			RenderSetup
					.builder(LBRClientRenderPipelines.MICROCHIP_OVERLAY)
					.withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
					.sortOnUpload()
					.createRenderSetup()
	);
	
	@SubscribeEvent
	private static void register(RegisterRenderBuffersEvent event)
	{
		event.registerRenderBuffer(MICROCHIP_OVERLAY);
	}
}
