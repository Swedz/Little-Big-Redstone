package net.swedz.little_big_redstone.item;

import com.google.common.collect.Maps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.logic.LogicType;
import net.swedz.tesseract.neoforge.helper.TransferHelper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public final class FloppyDiskItem extends Item
{
	public FloppyDiskItem(Properties properties)
	{
		super(properties.stacksTo(1).component(LBRComponents.FLOPPY_DISK, null));
	}
	
	private static boolean consumeItems(Player player, Microchip.Immutable microchip, boolean simulate)
	{
		if(player.hasInfiniteMaterials())
		{
			return true;
		}
		
		int extracted = TransferHelper.extractAny(player.getInventory(), (item) -> item.is(LBRItems.REDSTONE_BIT.get()), microchip.wireCount(), true, simulate);
		if(extracted != microchip.wireCount())
		{
			return false;
		}
		
		Map<LogicType<?>, Map<Optional<DyeColor>, Integer>> componentsNeeded = Maps.newHashMap();
		for(var entry : microchip.components())
		{
			var component = entry.component();
			componentsNeeded.compute(component.type(), (__, value) ->
			{
				if(value == null)
				{
					value = Maps.newHashMap();
				}
				value.compute((Optional<DyeColor>) component.color(), (___, count) ->
						count == null ? 1 : count + 1);
				return value;
			});
		}
		for(var typeEntry : componentsNeeded.entrySet())
		{
			var type = typeEntry.getKey();
			for(var dyeEntry : typeEntry.getValue().entrySet())
			{
				var dye = dyeEntry.getKey();
				int count = dyeEntry.getValue();
				Predicate<ItemStack> predicate = (item) ->
				{
					if(item.has(LBRComponents.LOGIC))
					{
						var component = item.get(LBRComponents.LOGIC);
						return component.type().equals(type) &&
							   component.color().equals(dye);
					}
					return false;
				};
				extracted = TransferHelper.extractAny(player.getInventory(), predicate, count, true, simulate);
				if(extracted != count)
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	private static void dropAll(Player player, Microchip microchip)
	{
		for(var entry : microchip.components())
		{
			ItemHandlerHelper.giveItemToPlayer(player, entry.toStack());
		}
		var redstoneBits = new ItemStack(LBRItems.REDSTONE_BIT, microchip.wires().values().size());
		ItemHandlerHelper.giveItemToPlayer(player, redstoneBits);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	private static void onPlaceMicrochipWithFloppyDisk(BlockEvent.EntityPlaceEvent event)
	{
		if(event.getEntity() instanceof Player player)
		{
			var offhand = player.getItemInHand(InteractionHand.OFF_HAND);
			if(offhand.has(LBRComponents.FLOPPY_DISK))
			{
				var microchip = offhand.get(LBRComponents.FLOPPY_DISK);
				if(microchip != null)
				{
					var blockEntity = event.getLevel().getBlockEntity(event.getPos());
					if(blockEntity instanceof MicrochipBlockEntity microchipBlockEntity)
					{
						if(consumeItems(player, microchip, true))
						{
							consumeItems(player, microchip, false);
							dropAll(player, microchipBlockEntity.microchip());
							microchipBlockEntity.microchip().loadFrom(microchip);
							player.displayClientMessage(LBRText.FLOPPY_DISK_APPLY_SUCCESS.text(), true);
						}
						else
						{
							player.displayClientMessage(LBRText.FLOPPY_DISK_APPLY_FAILURE.text(), true);
						}
					}
				}
			}
		}
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		Player player = context.getPlayer();
		if(player != null)
		{
			var usedHand = context.getHand();
			var itemStack = player.getItemInHand(usedHand);
			var hitBlockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
			if(hitBlockEntity instanceof MicrochipBlockEntity microchipBlockEntity)
			{
				if(!context.getLevel().isClientSide())
				{
					if(player.isShiftKeyDown())
					{
						itemStack.set(LBRComponents.FLOPPY_DISK, microchipBlockEntity.microchip().immutable());
						player.displayClientMessage(LBRText.FLOPPY_DISK_SAVE.text(), true);
					}
					else
					{
						if(itemStack.has(LBRComponents.FLOPPY_DISK))
						{
							var microchip = itemStack.get(LBRComponents.FLOPPY_DISK);
							if(microchip != null)
							{
								if(consumeItems(player, microchip, true))
								{
									consumeItems(player, microchip, false);
									dropAll(player, microchipBlockEntity.microchip());
									microchipBlockEntity.microchip().loadFrom(microchip);
									player.displayClientMessage(LBRText.FLOPPY_DISK_APPLY_SUCCESS.text(), true);
								}
								else
								{
									player.displayClientMessage(LBRText.FLOPPY_DISK_APPLY_FAILURE.text(), true);
								}
							}
						}
					}
				}
				return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(player.isShiftKeyDown())
		{
			player.getItemInHand(usedHand).remove(LBRComponents.FLOPPY_DISK);
			player.displayClientMessage(LBRText.FLOPPY_DISK_CLEAR.text(), true);
			return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
		}
		return super.use(level, player, usedHand);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(oldStack.getItem());
	}
}
