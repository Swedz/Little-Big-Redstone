package net.swedz.little_big_redstone.entity.stickynote;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBREntities;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.neoforge.helper.CodecHelper;
import net.swedz.tesseract.neoforge.helper.DirectionHelper;
import net.swedz.tesseract.neoforge.item.ItemStackInstance;

import java.util.function.IntFunction;

public final class StickyNoteEntity extends HangingEntity
{
	public static double boundsDepth()
	{
		return 1D / 16D;
	}
	
	public static double boundsWidth()
	{
		return 7D / 16D;
	}
	
	public static double boundsPositionOffset()
	{
		return 0.5 - (boundsDepth() / 2);
	}
	
	private static final EntityDataAccessor<Direction> DATA_FACING       = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.DIRECTION);
	private static final EntityDataAccessor<Integer>   DATA_QUADRANT     = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer>   DATA_COLOR        = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer>   DATA_TEXT_COLOR   = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean>   DATA_HAS_TEXT     = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean>   DATA_EDITABLE     = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<ItemStack> DATA_DISPLAY_ITEM = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.ITEM_STACK);
	
	private StickyNote note = StickyNote.EMPTY;
	
	private Component itemName;
	
	public StickyNoteEntity(EntityType<? extends StickyNoteEntity> type, Level level)
	{
		super(type, level);
	}
	
	public StickyNoteEntity(Level level, BlockPos pos, Direction direction, Direction facing, Quadrant quadrant, DyeColor color, DyeColor textColor, boolean editable)
	{
		super(LBREntities.STICKY_NOTE.get(), level, pos);
		
		this.setDirection(direction);
		this.setFacing(facing);
		this.setQuadrant(quadrant);
		this.setColor(color);
		this.setTextColor(textColor);
		this.setEditable(editable);
		
		this.recalculateBoundingBox();
	}
	
	@Override
	protected AABB calculateBoundingBox(BlockPos pos, Direction direction)
	{
		Vec3 center = Vec3.atCenterOf(pos).relative(direction, -boundsPositionOffset());
		center = this.getQuadrant().relative(this, center, (boundsWidth() / 2) + (0.5 / 16));
		Direction.Axis axis = direction.getAxis();
		double dx = axis == Direction.Axis.X ? boundsDepth() : boundsWidth();
		double dy = axis == Direction.Axis.Y ? boundsDepth() : boundsWidth();
		double dz = axis == Direction.Axis.Z ? boundsDepth() : boundsWidth();
		return AABB.ofSize(center, dx, dy, dz);
	}
	
	@Override
	public void playPlacementSound()
	{
		this.playSound(SoundEvents.WOOL_PLACE, 1, 1);
	}
	
	public ItemStack asItem(boolean includeData)
	{
		var stack = LBRItems.stickyNote(this.getColor()).get().getDefaultInstance();
		if(includeData)
		{
			stack.set(LBRComponents.STICKY_NOTE, note);
			stack.set(LBRComponents.STICKY_NOTE_TEXT_COLOR, this.getTextColor());
			stack.set(LBRComponents.STICKY_NOTE_EDITABLE, this.isEditable());
			stack.set(LBRComponents.STICKY_NOTE_DISPLAY_ITEM, new ItemStackInstance(this.getDisplayItem()));
			if(itemName != null)
			{
				stack.set(DataComponents.CUSTOM_NAME, itemName);
			}
		}
		return stack;
	}
	
	@Override
	public void dropItem(ServerLevel level, Entity breaker)
	{
		if(level.getGameRules().get(GameRules.ENTITY_DROPS))
		{
			this.playSound(SoundEvents.WOOL_BREAK, 1, 1);
			if(breaker instanceof Player player && player.hasInfiniteMaterials())
			{
				return;
			}
			this.spawnAtLocation(level, this.asItem(true));
		}
	}
	
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		super.defineSynchedData(builder);
		builder.define(DATA_FACING, Direction.SOUTH);
		builder.define(DATA_QUADRANT, Quadrant.TOP_LEFT.id());
		builder.define(DATA_COLOR, DyeColor.WHITE.getId());
		builder.define(DATA_TEXT_COLOR, StickyNoteItem.getDefaultTextColor(DyeColor.WHITE).getId());
		builder.define(DATA_HAS_TEXT, false);
		builder.define(DATA_EDITABLE, true);
		builder.define(DATA_DISPLAY_ITEM, ItemStack.EMPTY);
	}
	
	@Override
	protected void setDirection(Direction direction)
	{
		Assert.notNull(direction);
		
		this.setDirectionRaw(direction);
		if(direction.getAxis().isHorizontal())
		{
			this.setXRot(0);
			this.setYRot(direction.get2DDataValue() * 90f);
		}
		else
		{
			this.setXRot(-90f * direction.getAxisDirection().getStep());
			this.setYRot(0);
		}
		
		xRotO = this.getXRot();
		yRotO = this.getYRot();
	}
	
	@Override
	public float getVisualRotationYInDegrees()
	{
		var direction = this.getDirection();
		int offset = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
		return (float) Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + offset);
	}
	
	public Direction directionRelativeUp()
	{
		return DirectionHelper.relativeUp(this.getDirection(), this.getFacing());
	}
	
	public Direction directionRelativeDown()
	{
		return DirectionHelper.relativeDown(this.getDirection(), this.getFacing());
	}
	
	public Direction directionRelativeLeft()
	{
		return DirectionHelper.relativeLeft(this.getDirection(), this.getFacing());
	}
	
	public Direction directionRelativeRight()
	{
		return DirectionHelper.relativeRight(this.getDirection(), this.getFacing());
	}
	
	public Direction getFacing()
	{
		return entityData.get(DATA_FACING);
	}
	
	public void setFacing(Direction facing)
	{
		Assert.notNull(facing);
		Assert.that(facing.getAxis().isHorizontal());
		
		entityData.set(DATA_FACING, facing);
	}
	
	public Quadrant getQuadrant()
	{
		return Quadrant.byId(entityData.get(DATA_QUADRANT));
	}
	
	public void setQuadrant(Quadrant quadrant)
	{
		Assert.notNull(quadrant);
		
		entityData.set(DATA_QUADRANT, quadrant.id());
	}
	
	public DyeColor getColor()
	{
		return DyeColor.byId(entityData.get(DATA_COLOR));
	}
	
	public void setColor(DyeColor color)
	{
		Assert.notNull(color);
		
		entityData.set(DATA_COLOR, color.getId());
	}
	
	public DyeColor getTextColor()
	{
		return DyeColor.byId(entityData.get(DATA_TEXT_COLOR));
	}
	
	public boolean isDefaultTextColor()
	{
		return this.getTextColor() == StickyNoteItem.getDefaultTextColor(this.getColor());
	}
	
	public void setTextColor(DyeColor textColor)
	{
		textColor = textColor == null ? StickyNoteItem.getDefaultTextColor(this.getColor()) : textColor;
		
		entityData.set(DATA_TEXT_COLOR, textColor.getId());
	}
	
	public StickyNote getNote()
	{
		return note;
	}
	
	public void setNote(StickyNote note)
	{
		Assert.notNull(note);
		
		this.note = note;
		
		entityData.set(DATA_HAS_TEXT, !note.isEmpty());
	}
	
	public boolean hasText()
	{
		return entityData.get(DATA_HAS_TEXT);
	}
	
	public boolean isEditable()
	{
		return entityData.get(DATA_EDITABLE);
	}
	
	public void setEditable(boolean editable)
	{
		entityData.set(DATA_EDITABLE, editable);
	}
	
	public ItemStack getDisplayItem()
	{
		return entityData.get(DATA_DISPLAY_ITEM);
	}
	
	public void setDisplayItem(ItemStack stack)
	{
		Assert.notNull(stack);
		
		entityData.set(DATA_DISPLAY_ITEM, stack);
	}
	
	public void setItemName(Component itemName)
	{
		this.itemName = itemName;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean survives()
	{
		if(!this.level().noCollision(this))
		{
			return false;
		}
		boolean notInsideBlock = BlockPos.betweenClosedStream(this.calculateSupportBox())
				.filter((pos) -> !Block.canSupportCenter(this.level(), pos, this.getDirection()))
				.allMatch((pos) ->
				{
					BlockState blockstate = this.level().getBlockState(pos);
					return blockstate.isSolid() || DiodeBlock.isDiode(blockstate);
				});
		boolean notInsideHangingEntity = this.level().getEntities(
				this,
				this.getBoundingBox(),
				(other) ->
				{
					if(other instanceof HangingEntity)
					{
						if(other instanceof StickyNoteEntity otherNote)
						{
							return this.getDirection() == otherNote.getDirection() &&
								   this.getFacing() == otherNote.getFacing() &&
								   this.getQuadrant() == otherNote.getQuadrant();
						}
						else
						{
							return true;
						}
					}
					return false;
				}
		).isEmpty();
		return notInsideBlock && notInsideHangingEntity;
	}
	
	private InteractionResult interactDye(Player player, ItemStack stack)
	{
		if(player.isShiftKeyDown() &&
		   stack.is(ItemTags.DYES))
		{
			var dyeColor = stack.get(DataComponents.DYE);
			if(dyeColor != this.getTextColor())
			{
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
				this.playSound(SoundEvents.DYE_USE);
				this.setTextColor(dyeColor);
				stack.consume(1, player);
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}
	
	private InteractionResult interactDyeWash(Player player, ItemStack stack)
	{
		if(player.isShiftKeyDown() &&
		   stack.is(LBRTags.Items.DYE_WASHER))
		{
			var defaultTextColor = StickyNoteItem.getDefaultTextColor(this.getColor());
			if(this.getTextColor() != defaultTextColor)
			{
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
				this.playSound(SoundEvents.BUCKET_EMPTY);
				this.setTextColor(defaultTextColor);
				if(stack.is(LBRTags.Items.DYE_WASHER_CONSUMED))
				{
					stack.consume(1, player);
				}
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}
	
	private InteractionResult interactSeal(Player player, ItemStack stack)
	{
		if(player.isShiftKeyDown() &&
		   stack.is(LBRTags.Items.STICKY_NOTE_SEALANT))
		{
			this.setEditable(false);
			this.level().playSound(null, this.blockPosition(), SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS);
			((ServerLevel) this.level()).sendParticles(
					ParticleTypes.WAX_ON,
					this.getX(),
					this.getY(),
					this.getZ(),
					6,
					0.125,
					0.125,
					0.125,
					Mth.nextDouble(random, -0.5f, 0.5f)
			);
			stack.consume(1, player);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}
	
	private InteractionResult interactDisplayItem(Player player, ItemStack stack)
	{
		boolean place = !stack.isEmpty() &&
						!ItemStack.isSameItemSameComponents(stack, this.getDisplayItem());
		boolean remove = stack.isEmpty() &&
						 player.isShiftKeyDown() &&
						 !this.getDisplayItem().isEmpty();
		if(place || remove)
		{
			this.setDisplayItem(stack.copy());
			var sound = remove ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM;
			this.level().playSound(null, this.blockPosition(), sound, SoundSource.BLOCKS);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 location)
	{
		if(!this.level().isClientSide())
		{
			var stack = player.getItemInHand(hand);
			
			if(this.isEditable())
			{
				var result = this.interactDye(player, stack);
				if(!result.consumesAction())
				{
					result = this.interactDyeWash(player, stack);
				}
				if(!result.consumesAction())
				{
					result = this.interactSeal(player, stack);
				}
				if(!result.consumesAction())
				{
					result = this.interactDisplayItem(player, stack);
				}
				if(result.consumesAction())
				{
					return result;
				}
			}
			
			new StickyNotePacket(StickyNotePacket.ReferenceType.ENTITY, this.getId(), StickyNotePacket.Action.OPEN_VIEW, note.text()).sendToClient((ServerPlayer) player);
			
			return InteractionResult.CONSUME;
		}
		else
		{
			return InteractionResult.SUCCESS;
		}
	}
	
	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity)
	{
		int data = (this.getDirection().get3DDataValue() & 0x7) |
				   ((this.getFacing().get2DDataValue() & 0x7) << 3) |
				   ((this.getQuadrant().id() & 0x3) << 6);
		return new ClientboundAddEntityPacket(this, data, this.getPos());
	}
	
	@Override
	public void recreateFromPacket(ClientboundAddEntityPacket packet)
	{
		super.recreateFromPacket(packet);
		int data = packet.getData();
		
		this.setDirection(Direction.from3DDataValue(data & 0x7));
		this.setFacing(Direction.from2DDataValue((data >> 3) & 0x7));
		this.setQuadrant(Quadrant.byId((data >> 6) & 0x3));
		
		this.recalculateBoundingBox();
	}
	
	@Override
	public void addAdditionalSaveData(ValueOutput output)
	{
		super.addAdditionalSaveData(output);
		
		output.store("AttachedFace", Direction.CODEC, this.getDirection());
		output.store("Facing", Direction.CODEC, this.getFacing());
		output.store("Quadrant", Quadrant.CODEC, this.getQuadrant());
		output.store("Color", DyeColor.CODEC, this.getColor());
		output.store("TextColor", DyeColor.CODEC, this.getTextColor());
		output.storeNullable("ItemName", ComponentSerialization.CODEC, itemName);
		output.store("StickyNote", StickyNote.CODEC, note);
		output.putBoolean("Editable", this.isEditable());
		output.store("DisplayItem", ItemStackInstance.CODEC, new ItemStackInstance(this.getDisplayItem()));
	}
	
	@Override
	public void readAdditionalSaveData(ValueInput input)
	{
		super.readAdditionalSaveData(input);
		
		this.setDirection(input.read("AttachedFace", Direction.CODEC).orElseThrow());
		this.setFacing(input.read("Facing", Direction.CODEC).orElseThrow());
		this.setQuadrant(input.read("Quadrant", Quadrant.CODEC).orElseThrow());
		this.setColor(input.read("Color", DyeColor.CODEC).orElseThrow());
		this.setTextColor(input.read("TextColor", DyeColor.CODEC).orElseThrow());
		this.setItemName(input.read("ItemName", ComponentSerialization.CODEC).orElse(null));
		this.setNote(input.read("StickyNote", StickyNote.CODEC).orElseThrow());
		this.setEditable(input.getBooleanOr("Editable", true));
		this.setDisplayItem(input.read("DisplayItem", ItemStackInstance.CODEC).orElseThrow().asStack());
		
		this.recalculateBoundingBox();
	}
	
	public enum Quadrant
	{
		TOP_LEFT(0)
				{
					@Override
					public Vec3 relative(Direction up, Direction down, Direction left, Direction right, Vec3 pos, double distance)
					{
						pos = pos.add(up.getStepX() * distance, up.getStepY() * distance, up.getStepZ() * distance);
						pos = pos.add(left.getStepX() * distance, left.getStepY() * distance, left.getStepZ() * distance);
						return pos;
					}
				},
		TOP_RIGHT(1)
				{
					@Override
					public Vec3 relative(Direction up, Direction down, Direction left, Direction right, Vec3 pos, double distance)
					{
						pos = pos.add(up.getStepX() * distance, up.getStepY() * distance, up.getStepZ() * distance);
						pos = pos.add(right.getStepX() * distance, right.getStepY() * distance, right.getStepZ() * distance);
						return pos;
					}
				},
		BOTTOM_LEFT(2)
				{
					@Override
					public Vec3 relative(Direction up, Direction down, Direction left, Direction right, Vec3 pos, double distance)
					{
						pos = pos.add(down.getStepX() * distance, down.getStepY() * distance, down.getStepZ() * distance);
						pos = pos.add(left.getStepX() * distance, left.getStepY() * distance, left.getStepZ() * distance);
						return pos;
					}
				},
		BOTTOM_RIGHT(3)
				{
					@Override
					public Vec3 relative(Direction up, Direction down, Direction left, Direction right, Vec3 pos, double distance)
					{
						pos = pos.add(down.getStepX() * distance, down.getStepY() * distance, down.getStepZ() * distance);
						pos = pos.add(right.getStepX() * distance, right.getStepY() * distance, right.getStepZ() * distance);
						return pos;
					}
				};
		
		private static final IntFunction<Quadrant> BY_ID = ByIdMap.continuous(Quadrant::id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		
		public static final Codec<Quadrant> CODEC = CodecHelper.forLowercaseEnum(Quadrant.class);
		
		private final int id;
		
		Quadrant(int id)
		{
			this.id = id;
		}
		
		public int id()
		{
			return id;
		}
		
		public abstract Vec3 relative(Direction up, Direction down, Direction left, Direction right, Vec3 pos, double distance);
		
		public Vec3 relative(StickyNoteEntity entity, Vec3 pos, double distance)
		{
			return this.relative(entity.directionRelativeUp(), entity.directionRelativeDown(), entity.directionRelativeLeft(), entity.directionRelativeRight(), pos, distance);
		}
		
		public static Quadrant byId(int id)
		{
			return BY_ID.apply(id);
		}
	}
}
