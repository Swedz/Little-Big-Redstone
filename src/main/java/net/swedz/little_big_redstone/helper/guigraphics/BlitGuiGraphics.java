package net.swedz.little_big_redstone.helper.guigraphics;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public interface BlitGuiGraphics extends ColoredGuiGraphics
{
	TextureShaderConfiguration getTextureShader();
	
	void setTextureShader(TextureShaderConfiguration textureShader);
	
	default void setTextureShader(Supplier<ShaderInstance> shader, VertexFormat.Mode mode, VertexFormat format)
	{
		this.setTextureShader(new TextureShaderConfiguration(shader, mode, format));
	}
	
	default void setTextureShader(Supplier<ShaderInstance> shader)
	{
		this.setTextureShader(shader, TextureShaderConfiguration.DEFAULT.mode(), TextureShaderConfiguration.DEFAULT.format());
	}
	
	default void resetTextureShader()
	{
		this.setTextureShader(TextureShaderConfiguration.DEFAULT);
	}
	
	ResourceLocation getTexture();
	
	void setTexture(ResourceLocation texture);
	
	//
	
	default void blit(int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
	{
		this.blit(x, y, 0, (float) uOffset, (float) vOffset, uWidth, vHeight, 256, 256);
	}
	
	default void blit(int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, int argb)
	{
		int[] originalColor = this.getColor();
		this.setColor(argb);
		this.blit(x, y, uOffset, vOffset, uWidth, vHeight);
		this.setColor(originalColor);
	}
	
	default void blit(int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, int[] rgba)
	{
		int[] originalColor = this.getColor();
		this.setColor(rgba);
		this.blit(x, y, uOffset, vOffset, uWidth, vHeight);
		this.setColor(originalColor);
	}
	
	default void blit(int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, float red, float green, float blue, float alpha)
	{
		int[] originalColor = this.getColor();
		this.setColor(red, green, blue, alpha);
		this.blit(x, y, uOffset, vOffset, uWidth, vHeight);
		this.setColor(originalColor);
	}
	
	//
	
	default void blit(int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
	{
		this.blit(x, x + uWidth, y, y + vHeight, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
	}
	
	default void blit(int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, int argb)
	{
		int[] originalColor = this.getColor();
		this.setColor(argb);
		this.blit(x, y, blitOffset, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	default void blit(int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, int[] rgba)
	{
		int[] originalColor = this.getColor();
		this.setColor(rgba);
		this.blit(x, y, blitOffset, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	default void blit(int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
	{
		int[] originalColor = this.getColor();
		this.setColor(red, green, blue, alpha);
		this.blit(x, y, blitOffset, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	//
	
	default void blit(int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight)
	{
		this.blit(x, y, width, height, uOffset, vOffset, width, height, textureWidth, textureHeight);
	}
	
	default void blit(int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight, int argb)
	{
		int[] originalColor = this.getColor();
		this.setColor(argb);
		this.blit(x, y, uOffset, vOffset, width, height, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	default void blit(int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight, int[] rgba)
	{
		int[] originalColor = this.getColor();
		this.setColor(rgba);
		this.blit(x, y, uOffset, vOffset, width, height, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	default void blit(int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
	{
		int[] originalColor = this.getColor();
		this.setColor(red, green, blue, alpha);
		this.blit(x, y, uOffset, vOffset, width, height, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	//
	
	default void blit(int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
	{
		this.blit(x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
	}
	
	default void blit(int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, int argb)
	{
		int[] originalColor = this.getColor();
		this.setColor(argb);
		this.blit(x, y, width, height, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	default void blit(int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, int[] rgba)
	{
		int[] originalColor = this.getColor();
		this.setColor(rgba);
		this.blit(x, y, width, height, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	default void blit(int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
	{
		int[] originalColor = this.getColor();
		this.setColor(red, green, blue, alpha);
		this.blit(x, y, width, height, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	//
	
	default void blit(int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight)
	{
		this.innerBlit(x1, x2, y1, y2, blitOffset, uOffset / (float) textureWidth, (uOffset + (float) uWidth) / (float) textureWidth, vOffset / (float) textureHeight, (vOffset + (float) vHeight) / (float) textureHeight);
	}
	
	default void blit(int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight, int argb)
	{
		int[] originalColor = this.getColor();
		this.setColor(argb);
		this.blit(x1, x2, y1, y2, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	default void blit(int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight, int[] rgba)
	{
		int[] originalColor = this.getColor();
		this.setColor(rgba);
		this.blit(x1, x2, y1, y2, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	default void blit(int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight, float red, float green, float blue, float alpha)
	{
		int[] originalColor = this.getColor();
		this.setColor(red, green, blue, alpha);
		this.blit(x1, x2, y1, y2, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
		this.setColor(originalColor);
	}
	
	//
	
	default void nineSlice(int screenX, int screenY, int screenWidth, int screenHeight, int textureWidth, int textureHeight, int border)
	{
		int texCenterWidth = textureWidth - (border * 2);
		int texCenterHeight = textureHeight - (border * 2);
		
		// Corners
		this.blit(screenX, screenY, 0, 0, border, border, textureWidth, textureHeight);
		this.blit(screenX + screenWidth - border, screenY, textureWidth - border, 0, border, border, textureWidth, textureHeight);
		this.blit(screenX, screenY + screenHeight - border, 0, textureHeight - border, border, border, textureWidth, textureHeight);
		this.blit(screenX + screenWidth - border, screenY + screenHeight - border, textureWidth - border, textureHeight - border, border, border, textureWidth, textureHeight);
		
		// Edges
		for(int i = 0; i <= (screenWidth / texCenterWidth); i++)
		{
			int x = screenX + border + (i * texCenterWidth);
			int width = Math.min(texCenterWidth, screenWidth - (i * texCenterWidth) - (border * 2));
			this.blit(x, screenY, border, 0, width, border, textureWidth, textureHeight);
			this.blit(x, screenY + screenHeight - border, border, textureHeight - border, width, border, textureWidth, textureHeight);
		}
		for(int i = 0; i <= (screenHeight / texCenterHeight); i++)
		{
			int y = screenY + border + (i * texCenterHeight);
			int height = Math.min(texCenterHeight, screenHeight - (i * texCenterHeight) - (border * 2));
			this.blit(screenX, y, 0, border, border, height, textureWidth, textureHeight);
			this.blit(screenX + screenWidth - border, y, textureWidth - border, border, border, height, textureWidth, textureHeight);
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
				this.blit(x, y, border, border, width, height, textureWidth, textureHeight);
			}
		}
	}
	
	@ApiStatus.Internal
	void innerBlit(int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV);
}
