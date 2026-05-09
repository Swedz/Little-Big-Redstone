package net.swedz.little_big_redstone.gui.logicconfig.reference;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;

public record HeldItemLogicConfigReference(
		InteractionHand hand
) implements LogicConfigReference
{
	@Override
	public void save(Player player, LogicComponent component)
	{
		var stack = player.getItemInHand(hand);
		var stackLogicComponent = stack.get(LBRComponents.LOGIC);
		if(stackLogicComponent != null && stackLogicComponent.type().equals(component.type()))
		{
			var newLogicComponent = stackLogicComponent.copy();
			newLogicComponent.config().loadFrom(component.config());
			stack.set(LBRComponents.LOGIC, newLogicComponent);
		}
		else
		{
			LBR.LOGGER.warn("Logic was attempted to be updated by {} targetting mismatching or non-existent component in hand, discarding", player.getGameProfile().getName());
		}
	}
	
	@Override
	public void cancel(Player player)
	{
	}
	
	@Override
	public boolean isStillValid(Player player)
	{
		return player.getItemInHand(hand).is(LBRTags.Items.LOGIC_COMPONENTS);
	}
}
