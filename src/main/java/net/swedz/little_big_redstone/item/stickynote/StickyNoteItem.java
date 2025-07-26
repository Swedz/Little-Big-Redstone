package net.swedz.little_big_redstone.item.stickynote;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.little_big_redstone.gui.stickynote.reference.HeldItemStickyNoteReference;
import net.swedz.little_big_redstone.item.DyeColoredItem;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.helper.DirectionHelper;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public final class StickyNoteItem extends Item implements DyeColoredItem
{
	public static DyeColor getDefaultTextColor(DyeColor color)
	{
		return switch (color)
		{
			case GRAY, BLACK -> DyeColor.WHITE;
			default -> DyeColor.BLACK;
		};
	}
	
	private final DyeColor color;
	
	public StickyNoteItem(Properties properties, DyeColor color)
	{
		super(properties
				.component(LBRComponents.STICKY_NOTE, StickyNote.EMPTY)
				.component(LBRComponents.STICKY_NOTE_TEXT_COLOR, getDefaultTextColor(color)));
		this.color = color;
	}
	
	@Override
	public DyeColor color()
	{
		return color;
	}
	
	private boolean mayPlace(Player player, Direction direction, ItemStack itemStack, BlockPos pos)
	{
		return !player.level().isOutsideBuildHeight(pos) &&
			   player.mayUseItemAt(pos, direction, itemStack);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);
		if(level.isClientSide())
		{
			Proxies.get(LBRProxy.class).openStickyNote(new HeldItemStickyNoteReference(hand, stack), false);
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}
	
	private static StickyNoteEntity.Quadrant findClosestQuadrant(BlockPos blockPos, Direction side, Direction facing, Vec3 pos)
	{
		var faceCenter = blockPos.getCenter().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ()).relative(side, 0.5);
		pos = pos.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		
		var up = DirectionHelper.relativeUp(side, facing);
		var down = DirectionHelper.relativeDown(side, facing);
		var left = DirectionHelper.relativeLeft(side, facing);
		var right = DirectionHelper.relativeRight(side, facing);
		
		StickyNoteEntity.Quadrant closestQuadrant = null;
		double closestQuadrantDistance = Double.MAX_VALUE;
		for(var quadrant : StickyNoteEntity.Quadrant.values())
		{
			var corner = quadrant.relative(up, down, left, right, faceCenter, 0.5);
			var distance = pos.distanceTo(corner);
			if(distance < closestQuadrantDistance)
			{
				closestQuadrant = quadrant;
				closestQuadrantDistance = distance;
			}
		}
		
		return closestQuadrant;
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
		
		var facing = direction.getAxis().isVertical() ? Direction.fromYRot(player.getYRot()).getOpposite() : Direction.SOUTH;
		
		var quadrant = findClosestQuadrant(context.getClickedPos(), direction, facing, context.getClickLocation());
		
		var textColor = stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR);
		
		var level = context.getLevel();
		var entity = new StickyNoteEntity(level, placePos, direction, facing, quadrant, color, textColor);
		
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
		
		var itemName = stack.get(DataComponents.CUSTOM_NAME);
		if(itemName != null)
		{
			entity.setItemName(itemName);
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
