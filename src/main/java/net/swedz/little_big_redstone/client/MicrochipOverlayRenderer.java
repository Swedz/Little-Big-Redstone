package net.swedz.little_big_redstone.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlock;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class MicrochipOverlayRenderer
{
	@SubscribeEvent
	private static void onRenderBlockHighlight(ExtractBlockOutlineRenderStateEvent event)
	{
		if(Minecraft.getInstance().player.isCrouching())
		{
			var hitResult = event.getHitResult();
			var direction = hitResult.getDirection();
			var pos = hitResult.getBlockPos();
			var camera = event.getCamera();
			var state = Minecraft.getInstance().level.getBlockState(pos);
			if(state.getBlock() instanceof MicrochipBlock)
			{
				// TODO 26.1
				/*var poseStack = event.getPoseStack();
				var buffer = event.getMultiBufferSource();
				poseStack.pushPose();
				poseStack.translate(pos.getX() - camera.getPosition().x(), pos.getY() - camera.getPosition().y(), pos.getZ() - camera.getPosition().z());
				poseStack.translate(-0.005, -0.005, -0.005);
				poseStack.scale(1.01f, 1.01f, 1.01f);
				CubeOverlayRenderHelper.render(poseStack, buffer.getBuffer(LBRClientRenderTypes.microchipOverlay(direction)), direction, 1, 1, 1, OverlayTexture.NO_OVERLAY);
				poseStack.popPose();*/
			}
		}
	}
}
