package net.swedz.little_big_redstone;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class LBRClientShaders
{
	// TODO 26.1, this is moving to LBRCLientRenderPipelines
	/*private static ShaderInstance LOGIC_ITEM_SCANLINE_INSTANCE;
	
	public static ShaderInstance logicItemScanline()
	{
		return LOGIC_ITEM_SCANLINE_INSTANCE;
	}
	
	private static ShaderInstance LOGIC_SCANLINE_INSTANCE;
	
	public static ShaderInstance logicScanline()
	{
		return LOGIC_SCANLINE_INSTANCE;
	}
	
	private static ShaderInstance PULSING_ALPHA_INSTANCE;
	
	public static ShaderInstance pulsingAlpha()
	{
		return PULSING_ALPHA_INSTANCE;
	}
	
	private static ShaderInstance PULSING_TEXTURE_LIGHTNESS_INSTANCE;
	
	public static ShaderInstance pulsingTextureLightness()
	{
		return PULSING_TEXTURE_LIGHTNESS_INSTANCE;
	}
	
	private static ShaderInstance PULSING_TEXTURE_ALPHA_INSTANCE;
	
	public static ShaderInstance pulsingTextureAlpha()
	{
		return PULSING_TEXTURE_ALPHA_INSTANCE;
	}
	
	private static ShaderInstance TINTED_ITEM_INSTANCE;
	
	public static ShaderInstance tintedItem()
	{
		return TINTED_ITEM_INSTANCE;
	}
	
	public static final RenderStateShard.ShaderStateShard LOGIC_ITEM_SCANLINE       = new RenderStateShard.ShaderStateShard(LBRClientShaders::logicItemScanline);
	public static final RenderStateShard.ShaderStateShard LOGIC_SCANLINE            = new RenderStateShard.ShaderStateShard(LBRClientShaders::logicScanline);
	public static final RenderStateShard.ShaderStateShard PULSING_ALPHA             = new RenderStateShard.ShaderStateShard(LBRClientShaders::pulsingAlpha);
	public static final RenderStateShard.ShaderStateShard PULSING_TEXTURE_LIGHTNESS = new RenderStateShard.ShaderStateShard(LBRClientShaders::pulsingTextureLightness);
	public static final RenderStateShard.ShaderStateShard PULSING_TEXTURE_ALPHA     = new RenderStateShard.ShaderStateShard(LBRClientShaders::pulsingTextureAlpha);
	public static final RenderStateShard.ShaderStateShard TINTED_ITEM               = new RenderStateShard.ShaderStateShard(LBRClientShaders::tintedItem);
	
	@SubscribeEvent
	private static void registerShaders(RegisterShadersEvent event)
	{
		try
		{
			event.registerShader(new ShaderInstance(event.getResourceProvider(), LBR.id("logic_item_scanline"), NEW_ENTITY), (shader) -> LOGIC_ITEM_SCANLINE_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), LBR.id("logic_scanline"), POSITION_TEX_COLOR), (shader) -> LOGIC_SCANLINE_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), LBR.id("pulsing_alpha"), POSITION_COLOR), (shader) -> PULSING_ALPHA_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), LBR.id("pulsing_texture_lightness"), POSITION_TEX_COLOR), (shader) -> PULSING_TEXTURE_LIGHTNESS_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), LBR.id("pulsing_texture_alpha"), POSITION_TEX_COLOR), (shader) -> PULSING_TEXTURE_ALPHA_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), LBR.id("tinted_item"), NEW_ENTITY), (shader) -> TINTED_ITEM_INSTANCE = shader);
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}*/
}
