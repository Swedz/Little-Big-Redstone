package net.swedz.little_big_redstone.item.stickynote;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.gameevent.GameEvent;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;

public final class StickyNoteItem extends Item
{
	private final DyeColor color;
	
	public StickyNoteItem(Properties properties, DyeColor color)
	{
		super(properties.component(LBRComponents.STICKY_NOTE, StickyNote.EMPTY));
		this.color = color;
	}
	
	private boolean mayPlace(Player player, Direction direction, ItemStack itemStack, BlockPos pos)
	{
		return !player.level().isOutsideBuildHeight(pos) &&
			   player.mayUseItemAt(pos, direction, itemStack);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		var direction = context.getClickedFace();
		var placePos = context.getClickedPos().relative(direction);
		var player = context.getPlayer();
		var stack = context.getItemInHand();
		
		if(player != null && !this.mayPlace(player, direction, stack, placePos))
		{
			return InteractionResult.FAIL;
		}
		
		var level = context.getLevel();
		var entity = new StickyNoteEntity(level, placePos, direction, direction.getAxis().isVertical() ? Direction.fromYRot(player.getYRot()).getOpposite() : Direction.SOUTH, color);
		
		CustomData customData = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
		if(!customData.isEmpty())
		{
			EntityType.updateCustomEntityTag(level, player, entity, customData);
		}
		
		var note = stack.getOrDefault(LBRComponents.STICKY_NOTE, StickyNote.EMPTY);
		if(!note.isEmpty())
		{
			entity.setNote(note);
		}
		
		if(entity.survives())
		{
			if(!level.isClientSide())
			{
				entity.playPlacementSound();
				level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
				level.addFreshEntity(entity);
			}
			
			stack.consume(1, player);
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		
		return InteractionResult.CONSUME;
	}
}
