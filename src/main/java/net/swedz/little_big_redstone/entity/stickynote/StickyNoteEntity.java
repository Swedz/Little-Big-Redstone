package net.swedz.little_big_redstone.entity.stickynote;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
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
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBREntities;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.client.model.stickynote.StickyNoteModelData;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;
import net.swedz.tesseract.neoforge.api.Assert;
import net.swedz.tesseract.neoforge.helper.DirectionHelper;

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
	
	private static final EntityDataAccessor<Integer> DATA_FACING     = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_QUADRANT   = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_COLOR      = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_TEXT_COLOR = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> DATA_HAS_TEXT = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_EDITABLE = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.BOOLEAN);
	
	private Direction facing   = Direction.SOUTH;
	private Quadrant  quadrant = Quadrant.TOP_LEFT;
	
	private DyeColor color     = DyeColor.WHITE;
	private DyeColor textColor = StickyNoteItem.getDefaultTextColor(DyeColor.WHITE);
	
	private StickyNote note = StickyNote.EMPTY;
	
	private boolean editable = true;
	
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
	
	public ModelData getModelData()
	{
		return ModelData.builder()
				.with(StickyNoteModelData.KEY, new StickyNoteModelData(color, this.getTextColor(), entityData.get(DATA_HAS_TEXT)))
				.build();
	}
	
	@Override
	protected AABB calculateBoundingBox(BlockPos pos, Direction direction)
	{
		Vec3 center = Vec3.atCenterOf(pos).relative(direction, -boundsPositionOffset());
		center = quadrant.relative(this, center, (boundsWidth() / 2) + (0.5 / 16));
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
		var stack = LBRItems.stickyNote(color).get().getDefaultInstance();
		if(includeData)
		{
			stack.set(LBRComponents.STICKY_NOTE, note);
			stack.set(LBRComponents.STICKY_NOTE_TEXT_COLOR, textColor);
			stack.set(LBRComponents.STICKY_NOTE_EDITABLE, editable);
			if(itemName != null)
			{
				stack.set(DataComponents.CUSTOM_NAME, itemName);
			}
		}
		return stack;
	}
	
	@Override
	public void dropItem(Entity breaker)
	{
		if(this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
		{
			this.playSound(SoundEvents.WOOL_BREAK, 1, 1);
			if(breaker instanceof Player player && player.hasInfiniteMaterials())
			{
				return;
			}
			this.spawnAtLocation(this.asItem(true));
		}
	}
	
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		builder.define(DATA_FACING, Direction.SOUTH.get2DDataValue());
		builder.define(DATA_QUADRANT, 0);
		builder.define(DATA_COLOR, DyeColor.WHITE.getId());
		builder.define(DATA_TEXT_COLOR, StickyNoteItem.getDefaultTextColor(DyeColor.WHITE).getId());
		builder.define(DATA_HAS_TEXT, false);
		builder.define(DATA_EDITABLE, true);
	}
	
	@Override
	protected void setDirection(Direction direction)
	{
		Assert.notNull(direction);
		
		this.direction = direction;
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
		int offset = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
		return (float) Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + offset);
	}
	
	public Direction directionRelativeUp()
	{
		return DirectionHelper.relativeUp(direction, facing);
	}
	
	public Direction directionRelativeDown()
	{
		return DirectionHelper.relativeDown(direction, facing);
	}
	
	public Direction directionRelativeLeft()
	{
		return DirectionHelper.relativeLeft(direction, facing);
	}
	
	public Direction directionRelativeRight()
	{
		return DirectionHelper.relativeRight(direction, facing);
	}
	
	public Direction getFacing()
	{
		return facing;
	}
	
	public void setFacing(Direction facing)
	{
		Assert.notNull(facing);
		Assert.that(facing.getAxis().isHorizontal());
		
		this.facing = facing;
		
		entityData.set(DATA_FACING, facing.get2DDataValue());
	}
	
	public Quadrant getQuadrant()
	{
		return quadrant;
	}
	
	public void setQuadrant(Quadrant quadrant)
	{
		Assert.notNull(quadrant);
		
		this.quadrant = quadrant;
		
		entityData.set(DATA_QUADRANT, quadrant.id());
	}
	
	public DyeColor getColor()
	{
		return color;
	}
	
	public void setColor(DyeColor color)
	{
		Assert.notNull(color);
		
		this.color = color;
		
		entityData.set(DATA_COLOR, color.getId());
	}
	
	public DyeColor getTextColor()
	{
		return textColor;
	}
	
	public void setTextColor(DyeColor textColor)
	{
		this.textColor = textColor == null ? StickyNoteItem.getDefaultTextColor(color) : textColor;
		
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
	
	public boolean isEditable()
	{
		return editable;
	}
	
	public void setEditable(boolean editable)
	{
		this.editable = editable;
		
		entityData.set(DATA_EDITABLE, editable);
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
				.filter((pos) -> !Block.canSupportCenter(this.level(), pos, this.direction))
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
							return direction == otherNote.getDirection() &&
								   facing == otherNote.getFacing() &&
								   quadrant == otherNote.getQuadrant();
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
	
	@Override
	public InteractionResult interact(Player player, InteractionHand hand)
	{
		if(!this.level().isClientSide())
		{
			var stack = player.getItemInHand(hand);
			if(editable)
			{
				if(stack.getItem() instanceof DyeItem dyeItem &&
				   dyeItem.getDyeColor() != this.getTextColor())
				{
					player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
					this.playSound(SoundEvents.DYE_USE);
					this.setTextColor(dyeItem.getDyeColor());
					stack.consume(1, player);
					return InteractionResult.CONSUME;
				}
				else if(stack.is(LBRTags.Items.DYE_WASHER))
				{
					var defaultColor = StickyNoteItem.getDefaultTextColor(color);
					if(textColor != defaultColor)
					{
						player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
						this.playSound(SoundEvents.BUCKET_EMPTY);
						this.setTextColor(defaultColor);
						if(stack.is(LBRTags.Items.DYE_WASHER_CONSUMED))
						{
							stack.consume(1, player);
						}
						return InteractionResult.CONSUME;
					}
				}
				else if(stack.is(Items.HONEYCOMB))
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
			}
			
			var action = player.isShiftKeyDown() ? StickyNotePacket.Action.OPEN_EDIT : StickyNotePacket.Action.OPEN_VIEW;
			new StickyNotePacket(StickyNotePacket.ReferenceType.ENTITY, this.getId(), action, note.text()).sendToClient((ServerPlayer) player);
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
		int data = (direction.get3DDataValue() & 0x7) |
				   ((facing.get2DDataValue() & 0x7) << 3) |
				   ((quadrant.id() & 0x3) << 6) |
				   ((color.getId() & 0xF) << 8) |
				   ((this.getTextColor().getId() & 0xF) << 12) |
				   ((!note.isEmpty() ? 1 : 0) << 16) |
				   ((editable ? 1 : 0) << 17);
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
		this.setColor(DyeColor.byId((data >> 8) & 0xF));
		this.setTextColor(DyeColor.byId((data >> 12) & 0xF));
		entityData.set(DATA_HAS_TEXT, ((data >> 16) & 0x1) != 0);
		this.setEditable(((data >> 17) & 0x1) != 0);
		
		this.recalculateBoundingBox();
	}
	
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key)
	{
		if(key.equals(DATA_TEXT_COLOR))
		{
			textColor = DyeColor.byId(this.getEntityData().get(DATA_TEXT_COLOR));
		}
		else if(key.equals(DATA_EDITABLE))
		{
			editable = this.getEntityData().get(DATA_EDITABLE);
		}
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);
		
		compound.putByte("AttachedFace", (byte) direction.get3DDataValue());
		compound.putByte("Facing", (byte) facing.get2DDataValue());
		compound.putByte("Quadrant", (byte) quadrant.id());
		compound.putByte("Color", (byte) color.getId());
		compound.putByte("TextColor", (byte) textColor.getId());
		if(itemName != null)
		{
			compound.putString("ItemName", Component.Serializer.toJson(itemName, this.registryAccess()));
		}
		compound.put("StickyNote", StickyNote.CODEC.encodeStart(NbtOps.INSTANCE, note).getOrThrow());
		compound.putBoolean("Editable", editable);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);
		
		this.setDirection(Direction.from3DDataValue(compound.getByte("AttachedFace")));
		this.setFacing(Direction.from2DDataValue(compound.getByte("Facing")));
		this.setQuadrant(Quadrant.byId(compound.getByte("Quadrant")));
		this.setColor(DyeColor.byId(compound.getByte("Color")));
		this.setTextColor(DyeColor.byId(compound.getByte("TextColor")));
		if(compound.contains("ItemName"))
		{
			this.setItemName(Component.Serializer.fromJson(compound.getString("ItemName"), this.registryAccess()));
		}
		StickyNote.CODEC.parse(NbtOps.INSTANCE, compound.get("StickyNote"))
				.ifSuccess(this::setNote)
				.ifError((error) ->
						LBR.LOGGER.error("Failed to load sticky note data at {}: {}", pos.toShortString(), error.message()));
		this.setEditable(compound.getBoolean("Editable"));
		
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
