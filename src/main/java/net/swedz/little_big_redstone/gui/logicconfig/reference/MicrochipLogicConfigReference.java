package net.swedz.little_big_redstone.gui.logicconfig.reference;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipViewPosition;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;

public record MicrochipLogicConfigReference(
		BlockPos pos,
		int slot,
		MicrochipViewPosition returnViewPosition
) implements LogicConfigReference
{
	@Override
	public void save(Player player, LogicComponent component)
	{
		var playerName = player.getGameProfile().name();
		
		if(player.level().getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity)
		{
			var microchip = blockEntity.microchip();
			var targetEntry = microchip.components().get(slot);
			if(targetEntry != null && targetEntry.component().type().equals(component.type()))
			{
				targetEntry.component().config().loadFrom(component.config());
				microchip.components().updateValidity();
				int wiresPopped = microchip.wires().cleanup(targetEntry);
				microchip.markDirty(false);
				
				blockEntity.openMenu(player, returnViewPosition);
				
				if(wiresPopped > 0 && !player.hasInfiniteMaterials())
				{
					if(player.containerMenu instanceof MicrochipMenu menu)
					{
						var destination = menu.getDestinationInventoryItemHandler(player);
						int givenAmount = ResourceHandlerUtil.insertStacking(destination, ItemResource.of(LBRItems.REDSTONE_BIT), wiresPopped, null);
						if(givenAmount != wiresPopped)
						{
							int remainderAmount = wiresPopped - givenAmount;
							player.drop(new ItemStack(LBRItems.REDSTONE_BIT, remainderAmount), false);
						}
					}
					else
					{
						player.getInventory().placeItemBackInInventory(new ItemStack(LBRItems.REDSTONE_BIT, wiresPopped));
					}
				}
			}
			else
			{
				LBR.LOGGER.warn("Logic was attempted to be updated by {} targetting mismatching or non-existent component (slot {}), discarding", playerName, slot);
			}
		}
	}
	
	@Override
	public void cancel(Player player)
	{
		if(player.level().getBlockEntity(pos) instanceof MicrochipBlockEntity blockEntity)
		{
			blockEntity.openMenu(player, returnViewPosition);
		}
	}
	
	@Override
	public boolean isStillValid(Player player)
	{
		return player.blockPosition().closerThan(pos, 10);
	}
}
