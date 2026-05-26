package net.swedz.little_big_redstone.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRClientModels;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.client.shader.TintedItemBufferSource;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.tesseract.neoforge.helper.model.QuadColorFix;

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
	public Identifier getTextureLocation(StickyNoteEntity entity)
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
		
		var buffer = bufferSource.getBuffer(Sheets.translucentItemSheet());
		for(var quad : this.getStickyNoteModel(entity.getColor()).getQuads(null, null, random, entity.getModelData(), null))
		{
			QuadColorFix.putBulkData(buffer, poseStack.last(), quad, packedLight, OverlayTexture.NO_OVERLAY);
		}
		
		poseStack.popPose();
		
		var displayItem = entity.getDisplayItem();
		if(!displayItem.isEmpty())
		{
			this.renderStack(entity, poseStack, bufferSource, packedLight, displayItem);
		}
	}
	
	private void renderStack(StickyNoteEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ItemStack stack)
	{
		var attachedFace = entity.getDirection();
		
		poseStack.pushPose();
		
		poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
		poseStack.mulPose(Axis.YN.rotationDegrees(entity.getYRot()));
		if(attachedFace.getAxis().isVertical())
		{
			boolean up = attachedFace == Direction.UP;
			Direction facing = entity.getFacing();
			poseStack.mulPose(Axis.ZP.rotationDegrees(facing.toYRot() * (up ? -1 : 1)));
		}
		
		poseStack.translate(0, -0.03125f, -0.00625f);
		poseStack.scale(0.25f, 0.25f, 0.01f);
		
		var itemRenderer = Minecraft.getInstance().getItemRenderer();
		var model = itemRenderer.getModel(stack, null, null, 0);
		
		if(!entity.isDefaultTextColor())
		{
			var textColor = LBRColors.stickyNoteText(entity.getTextColor());
			bufferSource = new TintedItemBufferSource(bufferSource, textColor);
		}
		
		itemRenderer.render(stack, ItemDisplayContext.GUI, false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, model);
		
		poseStack.popPose();
	}
	
	private BakedModel getStickyNoteModel(DyeColor color)
	{
		ModelManager modelManager = blockRenderer.getBlockModelShaper().getModelManager();
		return modelManager.getModel(LBRClientModels.stickyNote(color));
	}
}
