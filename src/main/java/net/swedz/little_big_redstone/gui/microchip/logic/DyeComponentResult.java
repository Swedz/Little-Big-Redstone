package net.swedz.little_big_redstone.gui.microchip.logic;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.microchip.LogicEntry;

import java.util.Optional;

public record DyeComponentResult(Result result, Action action, Optional<DyeColor> color, boolean consume)
{
	public boolean success()
	{
		return result == Result.SUCCESS;
	}
	
	public void playSound(Entity entity)
	{
		if(action != null)
		{
			entity.playSound(action.sound());
		}
	}
	
	public static DyeComponentResult test(MicrochipMenu menu, ItemStack carried, LogicEntry entry)
	{
		Result result;
		Action action;
		Optional<DyeColor> color = Optional.empty();
		boolean consume;
		if(carried.is(Tags.Items.DYES) && carried.getItem() instanceof DyeItem dyeItem)
		{
			result = Result.SUCCESS;
			color = Optional.of(dyeItem.getDyeColor());
			action = Action.DYE;
			consume = true;
		}
		else if(carried.is(LBRTags.Items.DYE_WASHER))
		{
			result = Result.SUCCESS;
			action = Action.WASH;
			consume = carried.is(LBRTags.Items.DYE_WASHER_CONSUMED);
		}
		else
		{
			return new DyeComponentResult(Result.WRONG_ITEM, null, Optional.empty(), false);
		}
		if(entry.component().color().equals(color))
		{
			return new DyeComponentResult(Result.NO_CHANGE, null, Optional.empty(), false);
		}
		return new DyeComponentResult(result, action, color, consume);
	}
	
	public enum Result
	{
		SUCCESS,
		WRONG_ITEM,
		NO_CHANGE
	}
	
	public enum Action
	{
		DYE(SoundEvents.DYE_USE),
		WASH(SoundEvents.BUCKET_EMPTY);
		
		private final SoundEvent sound;
		
		Action(SoundEvent sound)
		{
			this.sound = sound;
		}
		
		public SoundEvent sound()
		{
			return sound;
		}
	}
}
