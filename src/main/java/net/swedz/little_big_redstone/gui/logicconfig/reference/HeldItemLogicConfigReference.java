package net.swedz.little_big_redstone.gui.logicconfig.reference;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;

public record HeldItemLogicConfigReference(
		InteractionHand hand
) implements LogicConfigReference
{
	@Override
	public void save(Player player, LogicConfig config)
	{
		var stack = player.getItemInHand(hand);
		var stackLogicConfig = stack.get(LBRComponents.LOGIC_CONFIG);
		if(stackLogicConfig != null && stackLogicConfig.type().equals(config.type()))
		{
			stack.set(LBRComponents.LOGIC_CONFIG, config);
		}
		else
		{
			LBR.LOGGER.warn("Logic was attempted to be updated by {} targetting mismatching or non-existent component in hand, discarding", player.getGameProfile().name());
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
