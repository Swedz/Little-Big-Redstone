package net.swedz.little_big_redstone.client.shader;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.swedz.little_big_redstone.LBRClientShaders;

public final class TintedTextureStateShard extends RenderStateShard.EmptyTextureStateShard
{
	private final int color;
	
	public TintedTextureStateShard(RenderType parent, int color)
	{
		super(parent.setupState, parent.clearState);
		this.color = color;
	}
	
	@Override
	public void setupRenderState()
	{
		super.setupRenderState();
		
		float red = FastColor.ARGB32.red(color) / 255f;
		float green = FastColor.ARGB32.green(color) / 255f;
		float blue = FastColor.ARGB32.blue(color) / 255f;
		var colorTint = LBRClientShaders.tintedItem().getUniform("ColorTint");
		if(colorTint != null)
		{
			colorTint.set(new float[]{red, green, blue, 1});
		}
	}
}
