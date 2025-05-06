package net.swedz.little_big_redstone.item.logicarray;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.logicarray.LogicArrayMenu;

import java.util.List;
import java.util.Optional;

public final class LogicArrayItem extends Item
{
	public LogicArrayItem(Properties properties)
	{
		super(properties.stacksTo(1).component(LBRComponents.LOGIC_ARRAY_STORAGE, LogicArrayStorage.EMPTY));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);
		var storage = stack.get(LBRComponents.LOGIC_ARRAY_STORAGE);
		
		player.awardStat(Stats.ITEM_USED.get(this));
		player.openMenu(new MenuProvider()
		{
			@Override
			public Component getDisplayName()
			{
				return LogicArrayItem.this.getDescription();
			}
			
			@Override
			public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player)
			{
				// TODO Logic Array: synchronize any changes with the item component
				NonNullList<ItemStack> items = NonNullList.withSize(LogicArrayMenu.SLOT_COUNT, ItemStack.EMPTY);
				storage.copyInto(items);
				return new LogicArrayMenu(containerId, playerInventory, new SimpleContainer(items.toArray(ItemStack[]::new)));
			}
		});
		
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP) ?
				Optional.ofNullable(stack.get(LBRComponents.LOGIC_ARRAY_STORAGE)).map(LogicArrayTooltipData::new) :
				Optional.empty();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag)
	{
		// TODO Logic Array:
	}
	
	private static void playRemoveOneSound(Entity entity)
	{
		// TODO Logic Array:
	}
	
	private static void playInsertSound(Entity entity)
	{
		// TODO Logic Array:
	}
	
	private static void playDropContentsSound(Entity entity)
	{
		// TODO Logic Array:
	}
}
