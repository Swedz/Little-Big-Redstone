package net.swedz.little_big_redstone.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
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
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.item.FloppyDiskItem;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;
import java.util.Objects;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class FloppyDiskConsumeItemsGuiOverlay
{
	private static boolean                      SHOULD_FADE;
	private static FloppyDiskItem.ConsumeResult ITEMS;
	private static int                          DISPLAY_TIME;
	
	private static void displayItems(FloppyDiskItem.ConsumeResult items)
	{
		SHOULD_FADE = false;
		ITEMS = items;
		DISPLAY_TIME = 40;
	}
	
	private static void renderItem(TesseractGuiGraphics graphics, ItemStack stack, boolean isPresent, int x, float alpha)
	{
		graphics.setTexture(LBR.id("textures/gui/slot_atlas.png"));
		graphics.blit(x - 1, -1, 0, 0, 18, 18);
		
		var vanilla = graphics.vanilla();
		vanilla.setColor(1, 1, 1, alpha);
		vanilla.renderItem(stack, x, 0);
		vanilla.renderItemDecorations(graphics.getFont(), stack, x, 0);
		vanilla.setColor(1, 1, 1, 1);
		
		graphics.pose().pushPose();
		graphics.pose().translate(0, 0, 200);
		graphics.blit(x - 1, -1, 18, isPresent ? 0 : 18, 18, 18);
		graphics.pose().popPose();
	}
	
	private static void renderItems(TesseractGuiGraphics graphics, int maxItems, MutableInt index, MutableInt x, List<ItemStack> items, boolean isPresent, float alpha)
	{
		for(int itemIndex = 0;
			itemIndex < items.size() && index.getValue() < maxItems;
			itemIndex++, index.increment(), x.add(18))
		{
			if(index.getValue() == maxItems - 1 &&
			   index.getValue() != ITEMS.size() - 1)
			{
				graphics.setTexture(LBR.id("textures/gui/slot_atlas.png"));
				graphics.blit(x.getValue() - 1, -1, 0, 18 * 2, 18, 18);
				var text = LBRText.FLOPPY_DISK_MORE_ITEMS.text(ITEMS.size() - index.getValue());
				graphics.setColor(1, 1, 1, alpha);
				graphics.drawString(text, x.getValue() + 19 - 2 - graphics.getFont().width(text), 9, true);
				graphics.resetColor();
				continue;
			}
			var stack = items.get(itemIndex);
			renderItem(graphics, stack, isPresent, x.getValue(), alpha);
		}
	}
	
	private static void renderItems(TesseractGuiGraphics graphics, float alpha)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(0, -24, 0);
		graphics.setColor(1, 1, 1, alpha);
		
		int maxItems = 9;
		int x = -(Math.min(ITEMS.size(), maxItems) * 18) / 2;
		
		graphics.setTexture(LBR.id("textures/gui/slot_background.png"));
		graphics.nineSlice(x - 2, -2, Math.min(ITEMS.size(), maxItems) * 18 + 2, 20, 32, 32, 4);
		
		MutableInt index = new MutableInt();
		MutableInt itemX = new MutableInt(x);
		renderItems(graphics, maxItems, index, itemX, ITEMS.missing(), false, alpha);
		renderItems(graphics, maxItems, index, itemX, ITEMS.present(), true, alpha);
		
		graphics.resetColor();
		graphics.pose().popPose();
	}
	
	public static void render(GuiGraphics vanilla, DeltaTracker delta)
	{
		if(ITEMS != null && ITEMS.size() > 0 && DISPLAY_TIME > 0)
		{
			int alpha = Math.min((int) ((DISPLAY_TIME - delta.getGameTimeDeltaPartialTick(false)) * (255f / 20f)), 255);
			if(alpha > 8)
			{
				var gui = Minecraft.getInstance().gui;
				
				var graphics = new TesseractGuiGraphics(vanilla);
				
				graphics.pose().pushPose();
				
				int yShift = Math.max(gui.leftHeight, gui.rightHeight) + (68 - 59);
				graphics.pose().translate((float) (vanilla.guiWidth() / 2), (float) (vanilla.guiHeight() - Math.max(yShift, 68)), 0);
				graphics.pose().translate(0, -4, 0);
				
				renderItems(graphics, alpha / 255f);
				
				graphics.pose().popPose();
			}
		}
	}
	
	private static int      TICK;
	private static int      LAST_SELECTED_SLOT;
	private static BlockPos LAST_TARGET_BLOCK_POS;
	private static boolean  SHOULD_FORCE_UPDATE;
	
	public static void forceUpdate()
	{
		SHOULD_FORCE_UPDATE = true;
	}
	
	private static boolean update(Level level, Player player, BlockPos targetBlock)
	{
		if(targetBlock != null && level.getBlockEntity(targetBlock) instanceof MicrochipBlockEntity)
		{
			for(var hand : InteractionHand.values())
			{
				var stack = player.getItemInHand(hand);
				if(stack.has(LBRComponents.FLOPPY_DISK))
				{
					var microchip = stack.get(LBRComponents.FLOPPY_DISK);
					if(microchip != null)
					{
						var items = FloppyDiskItem.consumeItems(player, microchip, true);
						displayItems(items);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@SubscribeEvent
	private static void tick(ClientTickEvent.Post event)
	{
		var level = Minecraft.getInstance().level;
		if(level == null)
		{
			return;
		}
		var player = Minecraft.getInstance().player;
		int selectedSlot = player.getInventory().selected;
		var targetBlock = Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult ? hitResult.getBlockPos() : null;
		
		// TODO set force update to true when the inventory updates rather than just every second...
		if(++TICK % 20 == 0)
		{
			SHOULD_FORCE_UPDATE = true;
		}
		
		if(SHOULD_FORCE_UPDATE ||
		   selectedSlot != LAST_SELECTED_SLOT ||
		   !Objects.equals(targetBlock, LAST_TARGET_BLOCK_POS))
		{
			SHOULD_FORCE_UPDATE = false;
			if(!update(level, player, targetBlock))
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
