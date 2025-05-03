package net.swedz.little_big_redstone.helper;

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

public final class GuiGraphicsHelper
{
	public static void setColor(GuiGraphics graphics, int argb)
	{
		float red = ColorConversions.redFloat(argb);
		float green = ColorConversions.greenFloat(argb);
		float blue = ColorConversions.blueFloat(argb);
		float alpha = ColorConversions.alphaFloat(argb);
		graphics.setColor(red, green, blue, alpha);
	}
	
	public static void resetColor(GuiGraphics graphics)
	{
		graphics.setColor(1, 1, 1, 1);
	}
	
	public static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, float red, float green, float blue, float alpha)
	{
		blit(graphics, texture, x, y, 0, (float) uOffset, (float) vOffset, uWidth, vHeight, 256, 256, red, green, blue, alpha);
	}
	
	public static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
	{
		blit(graphics, texture, x, x + uWidth, y, y + vHeight, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight, red, green, blue, alpha);
	}
	
	public static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
	{
		blit(graphics, texture, x, y, width, height, uOffset, vOffset, width, height, textureWidth, textureHeight, red, green, blue, alpha);
	}
	
	public static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
	{
		blit(graphics, texture, x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight, red, green, blue, alpha);
	}
	
	private static void blit(GuiGraphics graphics, ResourceLocation texture, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
	{
		innerBlit(graphics, texture, x1, x2, y1, y2, blitOffset, (uOffset + 0.0F) / (float) textureWidth, (uOffset + (float) uWidth) / (float) textureWidth, (vOffset + 0.0F) / (float) textureHeight, (vOffset + (float) vHeight) / (float) textureHeight, red, green, blue, alpha);
	}
	
	private static void innerBlit(GuiGraphics graphics, ResourceLocation texture, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV, float red, float green, float blue, float alpha)
	{
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.enableBlend();
		var pose = graphics.pose().last().pose();
		BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferbuilder.addVertex(pose, (float) x1, (float) y1, (float) blitOffset).setUv(minU, minV).setColor(red, green, blue, alpha);
		bufferbuilder.addVertex(pose, (float) x1, (float) y2, (float) blitOffset).setUv(minU, maxV).setColor(red, green, blue, alpha);
		bufferbuilder.addVertex(pose, (float) x2, (float) y2, (float) blitOffset).setUv(maxU, maxV).setColor(red, green, blue, alpha);
		bufferbuilder.addVertex(pose, (float) x2, (float) y1, (float) blitOffset).setUv(maxU, minV).setColor(red, green, blue, alpha);
		BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
		RenderSystem.disableBlend();
	}
	
	public static final class BlitBatch
	{
		private final Matrix4f      pose;
		private final BufferBuilder buffer;
		
		private final float red, green, blue, alpha;
		
		private boolean open = true;
		
		public BlitBatch(Matrix4f pose, BufferBuilder buffer,
						 float red, float green, float blue, float alpha)
		{
			this.pose = pose;
			this.buffer = buffer;
			
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}
		
		// Inherited from GuiGraphics
		public void add(int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight)
		{
			this.add(x, y, width, height, uOffset, vOffset, width, height, textureWidth, textureHeight);
		}
		
		public void add(int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
		{
			this.add(x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
		}
		
		public void add(int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight)
		{
			this.add(x1, x2, y1, y2, blitOffset, (uOffset + 0.0F) / (float) textureWidth, (uOffset + (float) uWidth) / (float) textureWidth, (vOffset + 0.0F) / (float) textureHeight, (vOffset + (float) vHeight) / (float) textureHeight);
		}
		
		private void add(int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV)
		{
			this.add(x1, x2, y1, y2, blitOffset, minU, maxU, minV, maxV, red, green, blue, alpha);
		}
		
		// Inherited from GuiGraphicsHelper
		public void add(int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, float red, float green, float blue, float alpha)
		{
			this.add(x, y, 0, (float) uOffset, (float) vOffset, uWidth, vHeight, 256, 256, red, green, blue, alpha);
		}
		
		public void add(int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
		{
			this.add(x, x + uWidth, y, y + vHeight, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight, red, green, blue, alpha);
		}
		
		public void add(int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
		{
			this.add(x, y, width, height, uOffset, vOffset, width, height, textureWidth, textureHeight, red, green, blue, alpha);
		}
		
		public void add(int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
		{
			this.add(x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight, red, green, blue, alpha);
		}
		
		private void add(int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
		{
			this.add(x1, x2, y1, y2, blitOffset, (uOffset + 0.0F) / (float) textureWidth, (uOffset + (float) uWidth) / (float) textureWidth, (vOffset + 0.0F) / (float) textureHeight, (vOffset + (float) vHeight) / (float) textureHeight, red, green, blue, alpha);
		}
		
		private void add(int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV, float red, float green, float blue, float alpha)
		{
			Assert.that(open, "BlitBatch has already been ended");
			buffer.addVertex(pose, (float) x1, (float) y1, (float) blitOffset).setUv(minU, minV).setColor(red, green, blue, alpha);
			buffer.addVertex(pose, (float) x1, (float) y2, (float) blitOffset).setUv(minU, maxV).setColor(red, green, blue, alpha);
			buffer.addVertex(pose, (float) x2, (float) y2, (float) blitOffset).setUv(maxU, maxV).setColor(red, green, blue, alpha);
			buffer.addVertex(pose, (float) x2, (float) y1, (float) blitOffset).setUv(maxU, minV).setColor(red, green, blue, alpha);
		}
		
		public void end()
		{
			BufferUploader.drawWithShader(buffer.buildOrThrow());
			RenderSystem.disableBlend();
			open = false;
		}
	}
	
	public static BlitBatch batchBlit(GuiGraphics graphics, ResourceLocation texture, float red, float green, float blue, float alpha)
	{
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.enableBlend();
		var pose = graphics.pose().last().pose();
		var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		return new BlitBatch(pose, buffer, red, green, blue, alpha);
	}
	
	public static BlitBatch batchBlit(GuiGraphics graphics, ResourceLocation texture)
	{
		return batchBlit(graphics, texture, 1, 1, 1, 1);
	}
	
	public static void nineSlice(GuiGraphics graphics, ResourceLocation texture, int screenX, int screenY, int screenWidth, int screenHeight, int textureWidth, int textureHeight, int border)
	{
		var batch = batchBlit(graphics, texture);
		
		int texCenterWidth = textureWidth - (border * 2);
		int texCenterHeight = textureHeight - (border * 2);
		
		// Corners
		batch.add(screenX, screenY, 0, 0, border, border, textureWidth, textureHeight);
		batch.add(screenX + screenWidth - border, screenY, textureWidth - border, 0, border, border, textureWidth, textureHeight);
		batch.add(screenX, screenY + screenHeight - border, 0, textureHeight - border, border, border, textureWidth, textureHeight);
		batch.add(screenX + screenWidth - border, screenY + screenHeight - border, textureWidth - border, textureHeight - border, border, border, textureWidth, textureHeight);
		
		// Edges
		for(int i = 0; i <= (screenWidth / texCenterWidth); i++)
		{
			int x = screenX + border + (i * texCenterWidth);
			int width = Math.min(texCenterWidth, screenWidth - (i * texCenterWidth) - (border * 2));
			batch.add(x, screenY, border, 0, width, border, textureWidth, textureHeight);
			batch.add(x, screenY + screenHeight - border, border, textureHeight - border, width, border, textureWidth, textureHeight);
		}
		for(int i = 0; i <= (screenHeight / texCenterHeight); i++)
		{
			int y = screenY + border + (i * texCenterHeight);
			int height = Math.min(texCenterHeight, screenHeight - (i * texCenterHeight) - (border * 2));
			batch.add(screenX, y, 0, border, border, height, textureWidth, textureHeight);
			batch.add(screenX + screenWidth - border, y, textureWidth - border, border, border, height, textureWidth, textureHeight);
		}
		
		// Center
		int centerWidth = (screenWidth - (border * 2)) / texCenterWidth;
		int centerHeight = (screenHeight - (border * 2)) / texCenterHeight;
		for(int ix = 0; ix <= centerWidth; ix++)
		{
			for(int iy = 0; iy <= centerHeight; iy++)
			{
				int x = screenX + border + (ix * texCenterWidth);
				int y = screenY + border + (iy * texCenterHeight);
				int width = Math.min(texCenterWidth, screenWidth - (ix * texCenterWidth) - (border * 2));
				int height = Math.min(texCenterHeight, screenHeight - (iy * texCenterHeight) - (border * 2));
				batch.add(x, y, border, border, width, height, textureWidth, textureHeight);
			}
		}
		
		batch.end();
	}
}
