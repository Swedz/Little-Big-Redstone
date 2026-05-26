package net.swedz.little_big_redstone.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import org.joml.Matrix3x2fStack;

public final class StickyNoteInHandItemRenderer
{
	private static void renderItem(PoseStack pose, SubmitNodeCollector submitNodeCollector, ItemStack stack, int packedLight, boolean center)
	{
		pose.pushPose();
		
		pose.mulPose(Axis.YP.rotationDegrees(180f));
		pose.mulPose(Axis.ZP.rotationDegrees(180f));
		pose.scale(0.0025f, 0.0025f, 0.0025f);
		pose.translate(-90.5f, center ? -75f : -90.5f, 0);
		
		var minecraft = Minecraft.getInstance();
		var graphics = new GuiGraphicsExtractor(minecraft, new Matrix3x2fStack(16), minecraft.gameRenderer.getGameRenderState().guiRenderState, -1, -1);
		
		var view = new StickyNoteView(stack);
		StickyNoteViewRenderer.extractBackground(graphics, view, 1, packedLight);
		StickyNoteViewRenderer.extractText(graphics, view);
		
		pose.popPose();
	}
	
	/**
	 * @see <a href="https://github.com/cc-tweaked/CC-Tweaked/blob/mc-1.21.x/projects/common/src/client/java/dan200/computercraft/client/render/ItemMapLikeRenderer.java">CC-Tweaked's ItemMapLikeRenderer.java</a>
	 */
	public static void renderItemFirstPerson(PoseStack pose, SubmitNodeCollector submitNodeCollector, int packedLight, InteractionHand hand, float pitch, float equipProgress, float swingProgress, ItemStack stack)
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
			minecraft.getEntityRenderDispatcher().getItemInHandRenderer().renderPlayerArm(pose, submitNodeCollector, packedLight, equipProgress, swingProgress, arm);
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
		
		renderItem(pose, submitNodeCollector, stack, packedLight, false);
		
		pose.popPose();
	}
}
