package net.swedz.little_big_redstone.helper.guigraphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.swedz.tesseract.neoforge.api.Assert;
import org.joml.Matrix4f;

public final class GuiGraphicsBatch
{
	private final Matrix4f      pose;
	private final BufferBuilder buffer;
	
	private boolean open = true;
	
	private GuiGraphicsBatch(Matrix4f pose, BufferBuilder buffer)
	{
		this.pose = pose;
		this.buffer = buffer;
	}
	
	public static GuiGraphicsBatch start(GuiGraphics graphics, ResourceLocation texture)
	{
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.enableBlend();
		var pose = graphics.pose().last().pose();
		var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		return new GuiGraphicsBatch(pose, buffer);
	}
	
	public Matrix4f pose()
	{
		return pose;
	}
	
	public BufferBuilder buffer()
	{
		return buffer;
	}
	
	public void end()
	{
		Assert.that(open, "Batch is no longer open");
		open = false;
		BufferUploader.drawWithShader(buffer.buildOrThrow());
		RenderSystem.disableBlend();
	}
}
