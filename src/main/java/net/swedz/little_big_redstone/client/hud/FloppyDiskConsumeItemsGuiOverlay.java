package net.swedz.little_big_redstone.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerChangeGameTypeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClient;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.item.floppydisk.FloppyDiskInstallResult;
import net.swedz.little_big_redstone.item.floppydisk.FloppyDiskInstaller;
import net.swedz.little_big_redstone.item.floppydisk.FloppyDiskItem;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.packet.RequestMicrochipWatcherPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class FloppyDiskConsumeItemsGuiOverlay
{
	private static boolean                 SHOULD_FADE;
	private static FloppyDiskInstallResult ITEMS;
	private static int                     DISPLAY_TIME;
	
	private static void displayItems(FloppyDiskInstallResult items)
	{
		SHOULD_FADE = false;
		ITEMS = items;
		DISPLAY_TIME = LBRClient.config().floppyDiskViewLingerTime();
	}
	
	private static void renderItem(GuiGraphicsExtractor graphics, ItemStack stack, boolean isPresent, int x, int color)
	{
		graphics.blit(LBR.id("textures/gui/slot_atlas.png"), x - 1, -1, 0, 0, 18, 18, color);
		
		// TODO 26.1 color
		graphics.item(stack, x, 0);
		graphics.itemDecorations(Minecraft.getInstance().font, stack, x, 0);
		
		graphics.blit(LBR.id("textures/gui/slot_atlas.png"), x - 1, -1, 18, isPresent ? 0 : 18, 18, 18, color);
	}
	
	private static void renderItems(GuiGraphicsExtractor graphics, int maxItems, AtomicInteger index, AtomicInteger x, List<ItemStack> items, boolean isPresent, int color)
	{
		var font = Minecraft.getInstance().font;
		for(int itemIndex = 0;
			itemIndex < items.size() && index.get() < maxItems;
			itemIndex++, index.incrementAndGet(), x.addAndGet(18))
		{
			if(index.get() == maxItems - 1 &&
			   index.get() != ITEMS.size() - 1)
			{
				graphics.blit(LBR.id("textures/gui/slot_atlas.png"), x.get() - 1, -1, 0, 18 * 2, 18, 18, color);
				var text = LBR.text().floppyDiskMoreItems(ITEMS.size() - index.get());
				graphics.text(font, text, x.get() + 19 - 2 - font.width(text), 9, color, false);
				continue;
			}
			var stack = items.get(itemIndex);
			renderItem(graphics, stack, isPresent, x.get(), color);
		}
	}
	
	private static void renderItems(GuiGraphicsExtractor graphics, int color)
	{
		graphics.pose().pushMatrix();
		graphics.pose().translate(0, -24);
		
		int maxItems = 9;
		int x = -(Math.min(ITEMS.size(), maxItems) * 18) / 2;
		
		graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				LBR.id("slot_background"),
				x - 2,
				-2,
				Math.min(ITEMS.size(), maxItems) * 18 + 2,
				20
		);
		
		var index = new AtomicInteger();
		var itemX = new AtomicInteger(x);
		renderItems(graphics, maxItems, index, itemX, ITEMS.missing(), false, color);
		renderItems(graphics, maxItems, index, itemX, ITEMS.present(), true, color);
		
		graphics.pose().popMatrix();
	}
	
	public static void extract(GuiGraphicsExtractor graphics, DeltaTracker delta)
	{
		if(Minecraft.getInstance().options.hideGui)
		{
			return;
		}
		
		if(ITEMS != null && ITEMS.size() > 0 && DISPLAY_TIME > 0)
		{
			int alpha = Math.min((int) ((DISPLAY_TIME - delta.getGameTimeDeltaPartialTick(false)) * (255f / 20f)), 255);
			if(alpha > 8)
			{
				var gui = Minecraft.getInstance().gui;
				
				graphics.pose().pushMatrix();
				
				int yShift = Math.max(gui.leftHeight, gui.rightHeight) + (68 - 59);
				graphics.pose().translate((float) (graphics.guiWidth() / 2), (float) (graphics.guiHeight() - Math.max(yShift, 68)));
				graphics.pose().translate(0, -4);
				
				renderItems(graphics, ARGB.color(alpha, 0xFFFFFF));
				
				graphics.pose().popMatrix();
			}
		}
	}
	
	private static int      TICK;
	private static int      LAST_SELECTED_SLOT;
	private static BlockPos LAST_TARGET_BLOCK_POS;
	private static boolean  SHOULD_FORCE_UPDATE;
	
	public static void update(boolean force)
	{
		if(force || ITEMS != null)
		{
			SHOULD_FORCE_UPDATE = true;
		}
	}
	
	private static boolean update(Level level, Player player, BlockPos targetBlock, Microchip.Immutable watchedMicrochip)
	{
		if(isMicrochip(level, targetBlock) && watchedMicrochip != null)
		{
			var stack = FloppyDiskItem.getHeldStack(player);
			if(!stack.isEmpty())
			{
				var microchip = stack.get(LBRComponents.FLOPPY_DISK);
				if(microchip != null)
				{
					var items = FloppyDiskInstaller.consumeItems(player, microchip, watchedMicrochip, true);
					displayItems(items);
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean isMicrochip(Level level, BlockPos pos)
	{
		return pos != null && level.getBlockEntity(pos) instanceof MicrochipBlockEntity;
	}
	
	@SubscribeEvent
	private static void tick(ClientTickEvent.Post event)
	{
		var level = Minecraft.getInstance().level;
		if(level == null)
		{
			return;
		}
		var proxy = Proxies.get(LBRProxy.class);
		var player = Minecraft.getInstance().player;
		int selectedSlot = player.getInventory().getSelectedSlot();
		var targetBlock = Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult ? hitResult.getBlockPos() : null;
		var watchedMicrochip = proxy.getWatchedMicrochip();
		
		if(selectedSlot != LAST_SELECTED_SLOT ||
		   !Objects.equals(targetBlock, LAST_TARGET_BLOCK_POS))
		{
			proxy.updateWatchedMicrochip(null);
			if(isMicrochip(level, targetBlock))
			{
				var floppyDisk = FloppyDiskItem.getHeldStack(player);
				if(!floppyDisk.isEmpty())
				{
					new RequestMicrochipWatcherPacket(targetBlock, true).sendToServer();
				}
				else if(watchedMicrochip != null)
				{
					new RequestMicrochipWatcherPacket(targetBlock, false).sendToServer();
				}
			}
			else if(watchedMicrochip != null)
			{
				new RequestMicrochipWatcherPacket(targetBlock, false).sendToServer();
			}
		}
		else if(SHOULD_FORCE_UPDATE ||
				watchedMicrochip == null)
		{
			SHOULD_FORCE_UPDATE = false;
			if(!update(level, player, targetBlock, watchedMicrochip))
			{
				SHOULD_FADE = true;
			}
		}
		
		if(SHOULD_FADE)
		{
			if(DISPLAY_TIME > 0)
			{
				DISPLAY_TIME--;
			}
			else
			{
				ITEMS = null;
				SHOULD_FADE = false;
			}
		}
		
		LAST_SELECTED_SLOT = selectedSlot;
		LAST_TARGET_BLOCK_POS = targetBlock;
	}
	
	@SubscribeEvent
	private static void onGameModeChange(ClientPlayerChangeGameTypeEvent event)
	{
		if(ITEMS != null)
		{
			SHOULD_FORCE_UPDATE = true;
		}
	}
}
