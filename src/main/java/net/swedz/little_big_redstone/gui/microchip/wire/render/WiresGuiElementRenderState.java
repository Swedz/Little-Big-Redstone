package net.swedz.little_big_redstone.gui.microchip.wire.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.ARGB;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientRenderPipelines;
import org.joml.Matrix3x2f;

public record WiresGuiElementRenderState(
		Matrix3x2f pose,
		WiresRenderState state,
		boolean powered,
		boolean hovered
) implements GuiElementRenderState
{
	private static float mix(float x, float y, float a)
	{
		return x * (1 - a) + (y * a);
	}
	
	private int calculateColor(int color)
	{
		if(hovered)
		{
			float time = (System.currentTimeMillis() % 1500) / 1500f;
			float wave = ((float) Math.sin(time * 6.28318f) + 1f) / 2f;
			float alpha = mix(0.25f, 0.5f, wave);
			float red = mix(ARGB.redFloat(color), 1, alpha);
			float green = mix(ARGB.greenFloat(color), 1, alpha);
			float blue = mix(ARGB.blueFloat(color), 1, alpha);
			return ARGB.colorFromFloat(1, red, green, blue);
		}
		return color;
	}
	
	@Override
	public void buildVertices(VertexConsumer buffer)
	{
		for(var wire : state.wires)
		{
			if(wire.powered == powered && wire.hovered == hovered)
			{
				for(var position : wire.positions)
				{
					float x0 = position.x();
					float x1 = x0 + wire.wireSize;
					float y0 = position.y();
					float y1 = y0 + wire.wireSize;
					
					float u0 = x0 / 16f;
					float u1 = x1 / 16f;
					float v0 = y0 / 16f;
					float v1 = y1 / 16f;
					
					int color = wire.color;//this.calculateColor(wire.color);
					
					buffer.addVertexWith2DPose(pose, x0, y1).setColor(color).setUv(u0, v1);
					buffer.addVertexWith2DPose(pose, x1, y1).setColor(color).setUv(u1, v1);
					buffer.addVertexWith2DPose(pose, x1, y0).setColor(color).setUv(u1, v0);
					buffer.addVertexWith2DPose(pose, x0, y0).setColor(color).setUv(u0, v0);
				}
			}
		}
	}
	
	@Override
	public RenderPipeline pipeline()
	{
		return hovered ?
				LBRClientRenderPipelines.PULSING_TEXTURE_LIGHTNESS :
				RenderPipelines.GUI_TEXTURED;
	}
	
	@Override
	public TextureSetup textureSetup()
	{
		var location = LBR.id("textures/gui/container/microchip/wire_%s.png".formatted(powered ? "on" : "off"));
		var texture = Minecraft.getInstance().getTextureManager().getTexture(location);
		return TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler());
	}
	
	@Override
	public ScreenRectangle scissorArea()
	{
		return null;
	}
	
	@Override
	public ScreenRectangle bounds()
	{
		// TODO 26.1 use proper bounds?
		return new ScreenRectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
}
