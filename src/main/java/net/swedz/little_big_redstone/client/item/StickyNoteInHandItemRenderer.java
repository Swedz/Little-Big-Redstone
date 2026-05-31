package net.swedz.little_big_redstone.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.tesseract.neoforge.helper.gui.SpriteGraphics;

public final class StickyNoteInHandItemRenderer
{
	private static void submitItem(PoseStack pose, SubmitNodeCollector submit, ItemStack stack, int packedLight, boolean center)
	{
		pose.pushPose();
		
		pose.mulPose(Axis.YP.rotationDegrees(180f));
		pose.mulPose(Axis.ZP.rotationDegrees(180f));
		pose.scale(0.0025f, 0.0025f, 0.0025f);
		pose.translate(-90.5f, center ? -75f : -90.5f, 0);
		
		var view = new StickyNoteView(stack);
		submitBackground(pose, submit, view, packedLight);
		submitText(pose, submit, view, packedLight);
		
		pose.popPose();
	}
	
	private static void submitBackground(PoseStack pose, SubmitNodeCollector submit, StickyNoteView view, int packedLight)
	{
		SpriteGraphics.blit(
				pose,
				submit,
				LBR.id("sticky_note/background/" + view.color().getName()),
				0,
				0,
				180,
				180,
				0,
				packedLight,
				0xFFFFFFFF
		);
		
		SpriteGraphics.blit(
				pose,
				submit,
				LBR.id("sticky_note/pin/" + view.color().getName()),
				(180 / 2) - 9,
				4,
				32,
				32,
				-0.001f,
				packedLight,
				0xFFFFFFFF
		);
	}
	
	private static void submitText(PoseStack pose, SubmitNodeCollector submit, StickyNoteView view, int packedLight)
	{
		pose.pushPose();
		pose.translate(5, 27, 0);
		
		var font = Minecraft.getInstance().font;
		int textColor = LBRColors.stickyNoteText(view.textColor());
		int index = 0;
		for(var line : font.split(view.text(), 170))
		{
			int y = index * font.lineHeight;
			submit.submitText(pose, 0, y, line, false, Font.DisplayMode.POLYGON_OFFSET, packedLight, textColor, 0, 0);
			index++;
		}
		
		pose.popPose();
	}
	
	/**
	 * @see <a href="https://github.com/cc-tweaked/CC-Tweaked/blob/mc-1.21.x/projects/common/src/client/java/dan200/computercraft/client/render/ItemMapLikeRenderer.java">CC-Tweaked's ItemMapLikeRenderer.java</a>
	 */
	public static void submit(PoseStack pose, SubmitNodeCollector submit, int packedLight, InteractionHand hand, float pitch, float equipProgress, float swingProgress, ItemStack stack)
	{
		var player = Minecraft.getInstance().player;
		var arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
		var minecraft = Minecraft.getInstance();
		var offset = arm == HumanoidArm.RIGHT ? 1f : -1f;
		
		pose.pushPose();
		
		pose.translate(offset * 0.125f, -0.125f, 0f);
		
		if(!minecraft.player.isInvisible())
		{
			pose.pushPose();
			pose.mulPose(Axis.ZP.rotationDegrees(offset * 10f));
			minecraft.getEntityRenderDispatcher().getItemInHandRenderer().renderPlayerArm(pose, submit, packedLight, equipProgress, swingProgress, arm);
			pose.popPose();
		}
		
		pose.translate(offset * 0.51f, -0.08f + equipProgress * -1.2f, -0.75f);
		var f1 = Mth.sqrt(swingProgress);
		var f2 = Mth.sin(f1 * (float) Math.PI);
		var f3 = -0.5f * f2;
		var f4 = 0.4f * Mth.sin(f1 * ((float) Math.PI * 2f));
		var f5 = -0.3f * Mth.sin(swingProgress * (float) Math.PI);
		pose.translate(offset * f3, f4 - 0.3f * f2, f5);
		pose.mulPose(Axis.XP.rotationDegrees(f2 * -45f));
		pose.mulPose(Axis.YP.rotationDegrees(offset * f2 * -30f));
		
		submitItem(pose, submit, stack, packedLight, false);
		
		pose.popPose();
	}
}
