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
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBRClientModels;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.tesseract.neoforge.helper.QuadColorFix;

public final class StickyNoteEntityRenderer extends EntityRenderer<StickyNoteEntity>
{
	private final RandomSource          random;
	private final BlockRenderDispatcher blockRenderer;
	
	public StickyNoteEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		random = RandomSource.create();
		blockRenderer = context.getBlockRenderDispatcher();
	}
	
	@Override
	public ResourceLocation getTextureLocation(StickyNoteEntity entity)
	{
		return InventoryMenu.BLOCK_ATLAS;
	}
	
	@Override
	public void render(StickyNoteEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		
		var attachedFace = entity.getDirection();
		
		poseStack.pushPose();
		
		poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
		poseStack.mulPose(Axis.YP.rotationDegrees(180 - entity.getYRot()));
		
		if(attachedFace.getAxis().isVertical())
		{
			boolean up = attachedFace == Direction.UP;
			Direction facing = entity.getFacing();
			poseStack.mulPose(Axis.ZP.rotationDegrees(facing.toYRot() * (up ? 1 : -1)));
		}
		
		var buffer = bufferSource.getBuffer(Sheets.cutoutBlockSheet());
		for(var quad : this.getStickyNoteModel(entity.getColor()).getQuads(null, null, random, entity.getModelData(), null))
		{
			QuadColorFix.putBulkData(buffer, poseStack.last(), quad, packedLight, OverlayTexture.NO_OVERLAY);
		}
		
		poseStack.popPose();
	}
	
	private BakedModel getStickyNoteModel(DyeColor color)
	{
		ModelManager modelManager = blockRenderer.getBlockModelShaper().getModelManager();
		return modelManager.getModel(LBRClientModels.stickyNote(color));
	}
}
