package net.swedz.little_big_redstone.item.logicarray;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.logicarray.LogicArrayMenu;
import net.swedz.little_big_redstone.item.DyeColoredItem;
import net.swedz.little_big_redstone.item.tooltip.ItemContainerContentsTooltipData;
import net.swedz.tesseract.neoforge.helper.TransferHelper;

import java.util.Optional;

public final class LogicArrayItem extends Item implements DyeColoredItem
{
	public static final int ROWS      = 4;
	public static final int COLUMNS   = 7;
	public static final int MAX_SLOTS = ROWS * COLUMNS;
	
	private final DyeColor color;
	
	public LogicArrayItem(Properties properties, DyeColor color)
	{
		super(properties.stacksTo(1).component(LBRComponents.LOGIC_ARRAY_STORAGE, ItemContainerContents.EMPTY));
		this.color = color;
	}
	
	@Override
	public DyeColor color()
	{
		return color;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);
		var storage = stack.get(LBRComponents.LOGIC_ARRAY_STORAGE);
		
		int logicArraySlot = player.getInventory().selected;
		
		player.awardStat(Stats.ITEM_USED.get(this));
		player.openMenu(
				new MenuProvider()
				{
					@Override
					public Component getDisplayName()
					{
						return LogicArrayItem.this.getDescription();
					}
					
					@Override
					public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player)
					{
						return new LogicArrayMenu(containerId, playerInventory, stack.getCapability(Capabilities.ItemHandler.ITEM), logicArraySlot);
					}
				},
				(buf) -> buf.writeVarInt(logicArraySlot)
		);
		
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}
	
	private boolean overrideStackedOn(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access)
	{
		var capability = stack.getCapability(Capabilities.ItemHandler.ITEM);
		if(capability != null)
		{
			if(other.isEmpty())
			{
				var extracted = TransferHelper.extractFirst(capability, 64);
				if(!extracted.isEmpty())
				{
					if(access == null)
					{
						slot.safeInsert(extracted);
					}
					else
					{
						access.set(extracted);
					}
				}
			}
			else
			{
				var inserted = TransferHelper.insert(capability, other);
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
		return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP) ?
				Optional.ofNullable(stack.get(LBRComponents.LOGIC_ARRAY_STORAGE)).map((contents) -> new ItemContainerContentsTooltipData(contents, COLUMNS, ROWS, true)) :
				Optional.empty();
	}
}
