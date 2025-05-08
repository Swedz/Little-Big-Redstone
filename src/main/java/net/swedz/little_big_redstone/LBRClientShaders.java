package net.swedz.little_big_redstone;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;

@EventBusSubscriber(value = Dist.CLIENT, modid = LBR.ID, bus = EventBusSubscriber.Bus.MOD)
public final class LBRClientShaders
{
	private static ShaderInstance LOGIC_SCANLINE_INSTANCE;
	
	public static ShaderInstance logicScanline()
	{
		return LOGIC_SCANLINE_INSTANCE;
	}
	
	private static ShaderInstance MICROCHIP_GRID_SNAPPING_OVERLAY_INSTANCE;
	
	public static ShaderInstance microchipGridSnappingOverlay()
	{
		return MICROCHIP_GRID_SNAPPING_OVERLAY_INSTANCE;
	}
	
	private static ShaderInstance MICROCHIP_WIRE_HOVERED_INSTANCE;
	
	public static ShaderInstance microchipWireHovered()
	{
		return MICROCHIP_WIRE_HOVERED_INSTANCE;
	}
	
	public static final RenderStateShard.ShaderStateShard LOGIC_SCANLINE                  = new RenderStateShard.ShaderStateShard(LBRClientShaders::logicScanline);
	public static final RenderStateShard.ShaderStateShard MICROCHIP_GRID_SNAPPING_OVERLAY = new RenderStateShard.ShaderStateShard(LBRClientShaders::microchipGridSnappingOverlay);
	public static final RenderStateShard.ShaderStateShard MICROCHIP_WIRE_HOVERED          = new RenderStateShard.ShaderStateShard(LBRClientShaders::microchipWireHovered);
	
	@SubscribeEvent
	private static void registerShaders(RegisterShadersEvent event)
	{
		try
		{
			event.registerShader(new ShaderInstance(event.getResourceProvider(), LBR.id("logic_scanline"), POSITION_TEX_COLOR), (shader) -> LOGIC_SCANLINE_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), LBR.id("microchip_grid_snapping_overlay"), POSITION_COLOR), (shader) -> MICROCHIP_GRID_SNAPPING_OVERLAY_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), LBR.id("microchip_wire_hovered"), POSITION_TEX_COLOR), (shader) -> MICROCHIP_WIRE_HOVERED_INSTANCE = shader);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
