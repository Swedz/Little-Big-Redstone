package net.swedz.little_big_redstone.item.stickynote;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.gui.stickynote.reference.HeldItemStickyNoteReference;
import net.swedz.little_big_redstone.item.stickynote.tooltip.StickyNoteTooltipData;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.helper.DirectionHelper;
import net.swedz.tesseract.neoforge.item.ItemStackInstance;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.Optional;
import java.util.function.Consumer;

public final class StickyNoteItem extends Item
{
	public static DyeColor getDefaultTextColor(DyeColor color)
	{
		return switch (color)
		{
			case GRAY, BLACK -> DyeColor.WHITE;
			default -> DyeColor.BLACK;
		};
	}
	
	public static boolean hasRelevantComponents(ItemStack stack)
	{
		return stack.has(LBRComponents.STICKY_NOTE) &&
			   stack.has(LBRComponents.STICKY_NOTE_COLOR) &&
			   stack.has(LBRComponents.STICKY_NOTE_TEXT_COLOR);
	}
	
	public StickyNoteItem(Properties properties, DyeColor color)
	{
		super(properties
				.component(LBRComponents.STICKY_NOTE, StickyNote.EMPTY)
				.component(LBRComponents.STICKY_NOTE_COLOR, color)
				.component(LBRComponents.STICKY_NOTE_TEXT_COLOR, getDefaultTextColor(color))
				.component(LBRComponents.STICKY_NOTE_EDITABLE, true)
				.component(LBRComponents.STICKY_NOTE_DISPLAY_ITEM, ItemStackInstance.EMPTY));
	}
	
	private boolean mayPlace(Player player, Direction direction, ItemStack itemStack, BlockPos pos)
	{
		return !player.level().isOutsideBuildHeight(pos) &&
			   player.mayUseItemAt(pos, direction, itemStack);
	}
	
	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);
		if(!stack.get(LBRComponents.STICKY_NOTE_EDITABLE))
		{
			return InteractionResult.PASS;
		}
		if(level.isClientSide())
		{
			Proxies.get(LBRProxy.class).openStickyNote(new HeldItemStickyNoteReference(hand, stack), player.isCrouching());
		}
		return InteractionResult.SUCCESS;
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
		var color = stack.get(LBRComponents.STICKY_NOTE_COLOR);
		var textColor = stack.get(LBRComponents.STICKY_NOTE_TEXT_COLOR);
		var editable = stack.get(LBRComponents.STICKY_NOTE_EDITABLE);
		
		var level = context.getLevel();
		var entity = new StickyNoteEntity(level, placePos, direction, facing, quadrant, color, textColor, editable);
		
		EntityType.createDefaultStackConfig(level, stack, player).accept(entity);
		
		var note = stack.getOrDefault(LBRComponents.STICKY_NOTE, StickyNote.EMPTY);
		if(!note.isEmpty())
		{
			entity.setNote(note);
		}
		
		var displayItem = stack.get(LBRComponents.STICKY_NOTE_DISPLAY_ITEM);
		entity.setDisplayItem(displayItem.asStack());
		
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
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.CONSUME;
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		var note = stack.get(LBRComponents.STICKY_NOTE);
		return note.isEmpty() ?
				Optional.empty() :
				Optional.of(new StickyNoteTooltipData(new StickyNoteView(stack)));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag)
	{
		if(display.shows(LBRComponents.STICKY_NOTE_EDITABLE.get()) &&
		   !stack.get(LBRComponents.STICKY_NOTE_EDITABLE))
		{
			lines.accept(LBR.text().sealed());
		}
	}
}
