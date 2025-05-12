package net.swedz.little_big_redstone.entity.stickynote;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBREntities;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;
import net.swedz.tesseract.neoforge.api.Assert;

public final class StickyNoteEntity extends HangingEntity
{
	public static final double DEPTH           = 1D / 16D;
	public static final double WIDTH           = 10D / 16D;
	public static final double POSITION_OFFSET = 0.5 - (DEPTH / 2);
	
	private static final EntityDataAccessor<Integer> DATA_FACING = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_COLOR  = SynchedEntityData.defineId(StickyNoteEntity.class, EntityDataSerializers.INT);
	
	private Direction facing = Direction.SOUTH;
	private DyeColor  color  = DyeColor.WHITE;
	
	private StickyNote note = StickyNote.EMPTY;
	
	public StickyNoteEntity(EntityType<? extends StickyNoteEntity> type, Level level)
	{
		super(type, level);
	}
	
	public StickyNoteEntity(Level level, BlockPos pos, Direction direction, Direction facing, DyeColor color)
	{
		super(LBREntities.STICKY_NOTE.get(), level, pos);
		this.setDirection(direction);
		this.setFacing(facing);
		this.setColor(color);
	}
	
	@Override
	protected AABB calculateBoundingBox(BlockPos pos, Direction direction)
	{
		Vec3 center = Vec3.atCenterOf(pos).relative(direction, -POSITION_OFFSET);
		Direction.Axis axis = direction.getAxis();
		double dx = axis == Direction.Axis.X ? DEPTH : WIDTH;
		double dy = axis == Direction.Axis.Y ? DEPTH : WIDTH;
		double dz = axis == Direction.Axis.Z ? DEPTH : WIDTH;
		return AABB.ofSize(center, dx, dy, dz);
	}
	
	@Override
	public void playPlacementSound()
	{
		this.playSound(SoundEvents.WOOL_PLACE, 1, 1);
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
			var stack = LBRItems.stickyNote(color).get().getDefaultInstance();
			stack.set(LBRComponents.STICKY_NOTE, note);
			this.spawnAtLocation(stack);
		}
	}
	
	@Override
	public ItemStack getPickResult()
	{
		return LBRItems.stickyNote(color).get().getDefaultInstance();
	}
	
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		builder.define(DATA_FACING, Direction.SOUTH.get2DDataValue());
		builder.define(DATA_COLOR, DyeColor.WHITE.getId());
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
		this.recalculateBoundingBox();
	}
	
	@Override
	public float getVisualRotationYInDegrees()
	{
		int offset = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
		return (float) Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + offset);
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
	
	public StickyNote getNote()
	{
		return note;
	}
	
	public void setNote(StickyNote note)
	{
		Assert.notNull(note);
		
		this.note = note;
	}
	
	@Override
	public InteractionResult interact(Player player, InteractionHand hand)
	{
		if(!this.level().isClientSide())
		{
			var action = player.isShiftKeyDown() ? StickyNotePacket.Action.OPEN_EDIT : StickyNotePacket.Action.OPEN_VIEW;
			new StickyNotePacket(this.getId(), action, note.text()).sendToClient((ServerPlayer) player);
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
		int data = (direction.get3DDataValue() & 0xFFF) |
				   ((facing.get2DDataValue() & 0xFF) << 8) |
				   ((color.getId() & 0xFF) << 16);
		return new ClientboundAddEntityPacket(this, data, this.getPos());
	}
	
	@Override
	public void recreateFromPacket(ClientboundAddEntityPacket packet)
	{
		super.recreateFromPacket(packet);
		int data = packet.getData();
		this.setDirection(Direction.from3DDataValue(data & 0xFF));
		this.setFacing(Direction.from2DDataValue((data >> 8) & 0xFF));
		this.setColor(DyeColor.byId((data >> 16) & 0xFF));
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);
		
		compound.putByte("AttachedFace", (byte) direction.get3DDataValue());
		compound.putByte("Facing", (byte) facing.get2DDataValue());
		compound.putByte("Color", (byte) color.getId());
		compound.put("StickyNote", StickyNote.CODEC.encodeStart(NbtOps.INSTANCE, note).getOrThrow());
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);
		
		this.setDirection(Direction.from3DDataValue(compound.getByte("AttachedFace")));
		this.setFacing(Direction.from2DDataValue(compound.getByte("Facing")));
		this.setColor(DyeColor.byId(compound.getByte("Color")));
		StickyNote.CODEC.parse(NbtOps.INSTANCE, compound.get("StickyNote"))
				.ifSuccess(this::setNote)
				.ifError((error) ->
						LBR.LOGGER.error("Failed to load sticky note data at {}: {}", pos.toShortString(), error.message()));
	}
}
