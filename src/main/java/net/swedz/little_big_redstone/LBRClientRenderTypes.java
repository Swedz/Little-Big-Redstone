package net.swedz.little_big_redstone;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.swedz.tesseract.neoforge.helper.CubeOverlayRenderHelper;

import java.util.Locale;
import java.util.function.Function;

@EventBusSubscriber(modid = LBR.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class LBRClientRenderTypes
{
	private static final Function<Direction, RenderType> MICROCHIP_OVERLAY = Util.memoize((direction) ->
	{
		String directionName = direction.toString().toLowerCase(Locale.ROOT);
		return CubeOverlayRenderHelper.createRenderType(
				"microchip_side_overlay_%s".formatted(directionName),
				LBR.id("textures/block/microchip_side_overlay_%s.png".formatted(directionName))
		);
	});
	
	public static RenderType microchipOverlay(Direction direction)
	{
		return MICROCHIP_OVERLAY.apply(direction);
	}
	
	@SubscribeEvent
	private static void onRegisterRenderBuffers(RegisterRenderBuffersEvent event)
	{
		for(var direction : Direction.values())
		{
			event.registerRenderBuffer(microchipOverlay(direction));
		}
	}
}
