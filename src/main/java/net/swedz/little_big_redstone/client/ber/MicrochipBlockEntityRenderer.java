package net.swedz.little_big_redstone.client.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.swedz.little_big_redstone.LBRClient;
import net.swedz.little_big_redstone.LBRClientRenderTypes;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;

import java.util.List;

public final class MicrochipBlockEntityRenderer<T extends MicrochipBlockEntity> implements BlockEntityRenderer<T, MicrochipBlockEntityRenderer.RenderState>
{
	public MicrochipBlockEntityRenderer(BlockEntityRendererProvider.Context context)
	{
	}
	
	@Override
	public RenderState createRenderState()
	{
		return new RenderState();
	}
	
	public static final class RenderState extends BlockEntityRenderState
	{
		public Direction           targetDirection;
		public BlockStateModelPart overlayModel;
	}
	
	@Override
	public void extractRenderState(
			T blockEntity,
			RenderState state,
			float partialTicks,
			Vec3 cameraPosition,
			ModelFeatureRenderer.CrumblingOverlay breakProgress
	)
	{
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		
		var minecraft = Minecraft.getInstance();
		if(minecraft.player != null &&
		   minecraft.player.isShiftKeyDown() &&
		   minecraft.hitResult instanceof BlockHitResult hitResult)
		{
			var hitBlockPos = hitResult.getBlockPos();
			if(hitBlockPos.equals(state.blockPos))
			{
				state.targetDirection = hitResult.getDirection();
				
				var modelKey = LBRClient.getMicrochipOverlayModel(state.targetDirection);
				state.overlayModel = minecraft.getModelManager().getStandaloneModel(modelKey);
			}
		}
	}
	
	@Override
	public void submit(
			RenderState renderState,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			CameraRenderState cameraRenderState
	)
	{
		if(renderState.overlayModel != null)
		{
			poseStack.pushPose();
			poseStack.translate(-0.005f, -0.005f, -0.005f);
			poseStack.scale(1.01f, 1.01f, 1.01f);
			submitNodeCollector.submitBlockModel(
					poseStack,
					LBRClientRenderTypes.MICROCHIP_OVERLAY,
					List.of(renderState.overlayModel),
					BlockModelRenderState.EMPTY_TINTS,
					renderState.lightCoords,
					OverlayTexture.NO_OVERLAY,
					0
			);
			poseStack.popPose();
		}
	}
}
