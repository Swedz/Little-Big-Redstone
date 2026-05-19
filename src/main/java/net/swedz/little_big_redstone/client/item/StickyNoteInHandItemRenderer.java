package net.swedz.little_big_redstone.client.item;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

public final class StickyNoteInHandItemRenderer
{
	private static void renderItem(PoseStack pose, MultiBufferSource bufferSource, ItemStack stack, int packedLight, boolean center)
	{
		pose.pushPose();
		pose.mulPose(Axis.YP.rotationDegrees(180f));
		pose.mulPose(Axis.ZP.rotationDegrees(180f));
		pose.scale(0.0025f, 0.0025f, 0.0025f);
		pose.translate(-90.5f, center ? -75f : -90.5f, 0);
		
		var graphics = new TesseractGuiGraphics(new GuiGraphics(Minecraft.getInstance(), pose, (MultiBufferSource.BufferSource) bufferSource));
		
		var textColor = stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR);
		var note = stack.get(LBRComponents.STICKY_NOTE);
		var view = new StickyNoteView(((StickyNoteItem) stack.getItem()).color(), textColor, note.parsed());
		
		graphics.setPackedLight(packedLight);
		graphics.setTextureShader(GameRenderer::getPositionColorTexLightmapShader, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
		
		StickyNoteViewRenderer.renderBackground(graphics, view);
		StickyNoteViewRenderer.renderText(graphics, view);
		
		graphics.resetTextureShader();
		graphics.resetPackedLight();
		
		pose.popPose();
	}
	
	/**
	 * @see <a href="https://github.com/cc-tweaked/CC-Tweaked/blob/mc-1.21.x/projects/common/src/client/java/dan200/computercraft/client/render/ItemMapLikeRenderer.java">CC-Tweaked's ItemMapLikeRenderer.java</a>
	 */
	public static void renderItemFirstPerson(PoseStack pose, MultiBufferSource bufferSource, int packedLight, InteractionHand hand, float pitch, float equipProgress, float swingProgress, ItemStack stack)
	{
		var player = Minecraft.getInstance().player;
		
		pose.pushPose();
		if(hand == InteractionHand.MAIN_HAND && player.getOffhandItem().isEmpty())
		{
			renderItemFirstPersonCenter(pose, bufferSource, packedLight, pitch, equipProgress, swingProgress, stack);
		}
		else
		{
			var arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
			renderItemFirstPersonSide(pose, bufferSource, packedLight, arm, equipProgress, swingProgress, stack);
		}
		pose.popPose();
	}
	
	/**
	 * @see <a href="https://github.com/cc-tweaked/CC-Tweaked/blob/mc-1.21.x/projects/common/src/client/java/dan200/computercraft/client/render/ItemMapLikeRenderer.java">CC-Tweaked's ItemMapLikeRenderer.java</a>
	 */
	private static void renderItemFirstPersonSide(PoseStack pose, MultiBufferSource bufferSource, int packedLight, HumanoidArm arm, float equipProgress, float swingProgress, ItemStack stack)
	{
		var minecraft = Minecraft.getInstance();
		var offset = arm == HumanoidArm.RIGHT ? 1f : -1f;
		pose.translate(offset * 0.125f, -0.125f, 0f);
		
		if(!minecraft.player.isInvisible())
		{
			pose.pushPose();
			pose.mulPose(Axis.ZP.rotationDegrees(offset * 10f));
			minecraft.getEntityRenderDispatcher().getItemInHandRenderer().renderPlayerArm(pose, bufferSource, packedLight, equipProgress, swingProgress, arm);
			pose.popPose();
		}
		
		pose.pushPose();
		pose.translate(offset * 0.51f, -0.08f + equipProgress * -1.2f, -0.75f);
		var f1 = Mth.sqrt(swingProgress);
		var f2 = Mth.sin(f1 * (float) Math.PI);
		var f3 = -0.5f * f2;
		var f4 = 0.4f * Mth.sin(f1 * ((float) Math.PI * 2f));
		var f5 = -0.3f * Mth.sin(swingProgress * (float) Math.PI);
		pose.translate(offset * f3, f4 - 0.3f * f2, f5);
		pose.mulPose(Axis.XP.rotationDegrees(f2 * -45f));
		pose.mulPose(Axis.YP.rotationDegrees(offset * f2 * -30f));
		
		renderItem(pose, bufferSource, stack, packedLight, false);
		
		pose.popPose();
	}
	
	/**
	 * @see <a href="https://github.com/cc-tweaked/CC-Tweaked/blob/mc-1.21.x/projects/common/src/client/java/dan200/computercraft/client/render/ItemMapLikeRenderer.java">CC-Tweaked's ItemMapLikeRenderer.java</a>
	 */
	private static void renderItemFirstPersonCenter(PoseStack pose, MultiBufferSource bufferSource, int packedLight, float pitch, float equipProgress, float swingProgress, ItemStack stack)
	{
		var minecraft = Minecraft.getInstance();
		var renderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();
		
		var swingRt = Mth.sqrt(swingProgress);
		var tX = -0.2f * Mth.sin(swingProgress * (float) Math.PI);
		var tZ = -0.4f * Mth.sin(swingRt * (float) Math.PI);
		pose.translate(0, -tX / 2, tZ);
		
		var pitchAngle = renderer.calculateMapTilt(pitch);
		pose.translate(0, 0.04f + equipProgress * -1.2f + pitchAngle * -0.5f, -0.72f);
		pose.mulPose(Axis.XP.rotationDegrees(pitchAngle * -85f));
		if(!minecraft.player.isInvisible())
		{
			pose.pushPose();
			pose.mulPose(Axis.YP.rotationDegrees(90f));
			renderer.renderMapHand(pose, bufferSource, packedLight, HumanoidArm.RIGHT);
			renderer.renderMapHand(pose, bufferSource, packedLight, HumanoidArm.LEFT);
			pose.popPose();
		}
		
		var rX = Mth.sin(swingRt * (float) Math.PI);
		pose.mulPose(Axis.XP.rotationDegrees(rX * 20f));
		pose.scale(1.5f, 1.5f, 1.5f);
		
		renderItem(pose, bufferSource, stack, packedLight, true);
	}
}
