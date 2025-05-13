package net.swedz.little_big_redstone.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;

import java.util.List;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class FloppyDiskMissingItemsGuiOverlay
{
	private static List<ItemStack> MISSING_ITEMS;
	private static int             MISSING_ITEMS_TIME;
	
	public static void displayMissingItems(List<ItemStack> missingItems)
	{
		MISSING_ITEMS = missingItems;
		MISSING_ITEMS_TIME = 6 * 20;
	}
	
	private static void renderItems(TesseractGuiGraphics graphics, DeltaTracker delta, float alpha)
	{
		var font = Minecraft.getInstance().font;
		
		graphics.pose().pushPose();
		graphics.pose().translate(0, -20, 0);
		graphics.setColor(1, 1, 1, alpha);
		
		int x = -(MISSING_ITEMS.size() * 18) / 2;
		
		graphics.setTexture(LBR.id("textures/gui/floppy_disk/background.png"));
		graphics.nineSlice(x - 2, -2, MISSING_ITEMS.size() * 18 + 2, 20, 32, 32, 4);
		
		for(var stack : MISSING_ITEMS)
		{
			graphics.setTexture(LBR.id("textures/gui/floppy_disk/slot.png"));
			graphics.blit(x - 1, -1, 0, 0, 18, 18, 18, 18);
			
			var vanilla = graphics.vanilla();
			vanilla.setColor(1, 1, 1, alpha);
			vanilla.renderItem(stack, x, 0);
			vanilla.renderItemDecorations(font, stack, x, 0);
			vanilla.setColor(1, 1, 1, 1);
			
			x += 18;
		}
		
		graphics.resetColor();
		graphics.pose().popPose();
	}
	
	private static void renderText(TesseractGuiGraphics graphics, DeltaTracker delta, int alpha)
	{
		var font = Minecraft.getInstance().font;
		
		var text = LBRText.FLOPPY_DISK_APPLY_FAILURE.text();
		
		graphics.pose().pushPose();
		graphics.setColor(1, 1, 1, alpha / 255f);
		
		graphics.drawString(text, -font.width(text) / 2, 0, true);
		
		graphics.resetColor();
		graphics.pose().popPose();
	}
	
	public static void render(GuiGraphics vanilla, DeltaTracker delta)
	{
		if(MISSING_ITEMS != null && MISSING_ITEMS_TIME > 0)
		{
			int alpha = Math.min((int) ((MISSING_ITEMS_TIME - delta.getGameTimeDeltaPartialTick(false)) * (255f / 20f)), 255);
			if(alpha > 8)
			{
				var gui = Minecraft.getInstance().gui;
				
				var graphics = new TesseractGuiGraphics(vanilla);
				
				graphics.pose().pushPose();
				
				int yShift = Math.max(gui.leftHeight, gui.rightHeight) + (68 - 59);
				graphics.pose().translate((float) (vanilla.guiWidth() / 2), (float) (vanilla.guiHeight() - Math.max(yShift, 68)), 0);
				graphics.pose().translate(0, -4, 0);
				
				renderItems(graphics, delta, alpha / 255f);
				renderText(graphics, delta, alpha);
				
				graphics.pose().popPose();
			}
		}
	}
	
	@SubscribeEvent
	private static void tick(ClientTickEvent.Post event)
	{
		if(MISSING_ITEMS_TIME > 0)
		{
			MISSING_ITEMS_TIME--;
		}
		else if(MISSING_ITEMS != null)
		{
			MISSING_ITEMS = null;
		}
	}
	
	@SubscribeEvent
	private static void overrideRenderLayer(RenderGuiLayerEvent.Pre event)
	{
		if(event.getName() == VanillaGuiLayers.OVERLAY_MESSAGE && MISSING_ITEMS_TIME > 0)
		{
			event.setCanceled(true);
		}
	}
}
