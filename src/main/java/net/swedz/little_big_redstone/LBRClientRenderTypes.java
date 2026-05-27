package net.swedz.little_big_redstone;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.swedz.little_big_redstone.client.shader.LogicItemTextureStateShard;
import net.swedz.little_big_redstone.client.shader.TintedTextureStateShard;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class LBRClientRenderTypes
{
	private static final Function<Direction, RenderType> MICROCHIP_OVERLAY = Util.memoize((direction) ->
	{
		String directionName = direction.toString().toLowerCase(Locale.ROOT);
		return CubeOverlayRenderHelper.createRenderType(
				"microchip_side_overlay_%s".formatted(directionName),
				LBR.id("textures/block/microchip/overlay_%s.png".formatted(directionName))
		);
	});
	
	public static RenderType microchipOverlay(Direction direction)
	{
		return MICROCHIP_OVERLAY.apply(direction);
	}
	
	private static final RenderType LOGIC_SCANLINE = RenderType.create(
			"logic_item_scanline",
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			256,
			true,
			true,
			RenderType.CompositeState.builder()
					.setShaderState(LBRClientShaders.LOGIC_ITEM_SCANLINE)
					.setTextureState(new LogicItemTextureStateShard(InventoryMenu.BLOCK_ATLAS, LBR.id("textures/logic/scanline.png"), false, false))
					.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
					.setCullState(RenderType.NO_CULL)
					.setLightmapState(RenderType.LIGHTMAP)
					.setOverlayState(RenderType.OVERLAY)
					.createCompositeState(true)
	);
	
	public static RenderType logicScanline()
	{
		return LOGIC_SCANLINE;
	}
	
	private static final BiFunction<RenderType, Integer, RenderType> TINTED_ITEM = Util.memoize((parent, color) -> RenderType.create(
			"tinted_item",
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			256,
			true,
			false,
			RenderType.CompositeState.builder()
					.setShaderState(LBRClientShaders.TINTED_ITEM)
					.setTextureState(new TintedTextureStateShard(parent, color))
					.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
					.setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
					.setLightmapState(RenderType.LIGHTMAP)
					.setOverlayState(RenderType.OVERLAY)
					.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
					.createCompositeState(true)
	));
	
	public static RenderType tintedItem(RenderType parent, int color)
	{
		return TINTED_ITEM.apply(parent, color);
	}
	
	@SubscribeEvent
	private static void onRegisterRenderBuffers(RegisterRenderBuffersEvent event)
	{
		for(var direction : Direction.values())
		{
			event.registerRenderBuffer(microchipOverlay(direction));
		}
	}
}
