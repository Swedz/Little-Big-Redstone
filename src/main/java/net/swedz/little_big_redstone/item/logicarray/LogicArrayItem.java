package net.swedz.little_big_redstone.item.logicarray;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.logicarray.LogicArrayMenu;
import net.swedz.little_big_redstone.item.tooltip.ItemContainerContentsTooltipData;

import java.util.Optional;

public final class LogicArrayItem extends Item
{
	public static final int ROWS      = 4;
	public static final int COLUMNS   = 7;
	public static final int MAX_SLOTS = ROWS * COLUMNS;
	
	public LogicArrayItem(Properties properties, DyeColor color)
	{
		super(properties
				.stacksTo(1)
				.component(LBRComponents.LOGIC_ARRAY_STORAGE, ItemContainerContents.EMPTY));
	}
	
	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);
		var storage = stack.get(LBRComponents.LOGIC_ARRAY_STORAGE);
		
		int logicArraySlot = player.getInventory().getSelectedSlot();
		
		player.awardStat(Stats.ITEM_USED.get(this));
		player.openMenu(
				new MenuProvider()
				{
					@Override
					public Component getDisplayName()
					{
						return stack.get(DataComponents.ITEM_NAME);
					}
					
					@Override
					public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player)
					{
						var handler = (LogicArrayItemHandler) ItemAccess.forStack(stack).getCapability(Capabilities.Item.ITEM);
						return new LogicArrayMenu(containerId, playerInventory, handler, handler::set, logicArraySlot);
					}
				},
				(buf) -> buf.writeVarInt(logicArraySlot)
		);
		
		return InteractionResult.SUCCESS;
	}
	
	private boolean overrideStackedOn(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access)
	{
		var capability = ItemAccess.forStack(stack).getCapability(Capabilities.Item.ITEM);
		if(capability != null)
		{
			if(other.isEmpty())
			{
				var extracted = ResourceHandlerUtil.extractFirst(capability, (resource) -> true, 64, null);
				if(!extracted.isEmpty())
				{
					var extractedStack = extracted.resource().toStack(extracted.amount());
					if(access == null)
					{
						slot.safeInsert(extractedStack);
					}
					else
					{
						access.set(extractedStack);
					}
				}
			}
			else
			{
				var inserted = ResourceHandlerUtil.insertStacking(capability, ItemResource.of(other), other.getCount(), null);
				if(inserted > 0)
				{
					other.shrink(inserted);
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player)
	{
		if(stack.getCount() != 1)
		{
			return false;
		}
		if(action == ClickAction.SECONDARY && slot.allowModification(player) && slot.mayPlace(stack))
		{
			var other = slot.getItem();
			return this.overrideStackedOn(stack, other, slot, action, player, null);
		}
		return false;
	}
	
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access)
	{
		if(stack.getCount() != 1)
		{
			return false;
		}
		if(action == ClickAction.SECONDARY && slot.allowModification(player) && slot.mayPlace(other))
		{
			return this.overrideStackedOn(stack, other, slot, action, player, access);
		}
		return false;
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		var tooltipDisplay = stack.get(DataComponents.TOOLTIP_DISPLAY);
		return tooltipDisplay.shows(LBRComponents.LOGIC_ARRAY_STORAGE.get()) ?
				Optional.ofNullable(stack.get(LBRComponents.LOGIC_ARRAY_STORAGE)).map((contents) -> new ItemContainerContentsTooltipData(contents, COLUMNS, ROWS, true)) :
				Optional.empty();
	}
}
