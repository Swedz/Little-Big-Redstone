package net.swedz.little_big_redstone.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.swedz.little_big_redstone.LBRClientModels;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;

public final class StickyNoteEntityRenderer extends EntityRenderer<StickyNoteEntity>
{
	private final BlockRenderDispatcher blockRenderer;
	
	public StickyNoteEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		blockRenderer = context.getBlockRenderDispatcher();
	}
	
	@Override
	public ResourceLocation getTextureLocation(StickyNoteEntity entity)
	{
		return InventoryMenu.BLOCK_ATLAS;
	}
	
	@Override
	public void render(StickyNoteEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
		
		poseStack.pushPose();
		
		Direction attachedFace = entity.getDirection();
		poseStack.translate(attachedFace.getStepX() * StickyNoteEntity.POSITION_OFFSET, attachedFace.getStepY() * StickyNoteEntity.POSITION_OFFSET, attachedFace.getStepZ() * StickyNoteEntity.POSITION_OFFSET);
		
		poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
		poseStack.mulPose(Axis.YP.rotationDegrees(180 - entity.getYRot()));
		
		poseStack.translate(-0.5, -0.5, -0.5);
		
		if(attachedFace.getAxis().isVertical())
		{
			boolean up = attachedFace == Direction.UP;
			Direction facing = entity.getFacing();
			
			poseStack.mulPose(Axis.ZP.rotationDegrees(facing.toYRot() * (up ? 1 : -1)));
			
			Direction directionX = up ? Direction.EAST : Direction.WEST;
			Direction directionY = directionX.getOpposite();
			int ox = (facing == directionX || facing == Direction.NORTH) ? -1 : 0;
			int oy = (facing == Direction.NORTH || facing == directionY) ? -1 : 0;
			poseStack.translate(ox, oy, 0);
		}
		
		blockRenderer.getModelRenderer().renderModel(
				poseStack.last(),
				buffer.getBuffer(Sheets.cutoutBlockSheet()),
				null,
				this.getStickyNoteModel(entity.getColor()),
				1, 1, 1,
				packedLight,
				OverlayTexture.NO_OVERLAY,
				ModelData.EMPTY,
				null
		);
		
		poseStack.popPose();
	}
	
	private BakedModel getStickyNoteModel(DyeColor color)
	{
		ModelManager modelManager = blockRenderer.getBlockModelShaper().getModelManager();
		return modelManager.getModel(LBRClientModels.stickyNote(color));
	}
}
