package net.swedz.little_big_redstone.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;

// TODO move to Tesseract
public final class QuadColorFix
{
	/**
	 * <p>Writes a {@link BakedQuad} to a {@link VertexConsumer} using the colors applied by
	 * {@link QuadTransformers#applyingColor(int)}.</p>
	 *
	 * <p>For some reason, the color written by {@link QuadTransformers#applyingColor(int)} does not get carried over
	 * when rendering the quad normally. It's unclear to me if this is a bug, or if I am doing something wrong, but
	 * this seems to fix the issue.</p>
	 *
	 * @param buffer        the buffer to write the quad to
	 * @param pose          the pose
	 * @param quad          the quad
	 * @param packedLight   the packed light
	 * @param packedOverlay the packed overlay
	 */
	public static void putBulkData(VertexConsumer buffer, PoseStack.Pose pose, BakedQuad quad, int packedLight, int packedOverlay)
	{
		float red = 1f;
		float green = 1f;
		float blue = 1f;
		float alpha = 1f;
		if(quad.isTinted())
		{
			int tintIndex = quad.getTintIndex();
			int abgr = quad.getVertices()[tintIndex * IQuadTransformer.STRIDE + IQuadTransformer.COLOR];
			red = FastColor.ABGR32.red(abgr) / 255f;
			green = FastColor.ABGR32.green(abgr) / 255f;
			blue = FastColor.ABGR32.blue(abgr) / 255f;
			alpha = FastColor.ABGR32.alpha(abgr) / 255f;
		}
		buffer.putBulkData(pose, quad, red, green, blue, alpha, packedLight, packedOverlay);
	}
}
