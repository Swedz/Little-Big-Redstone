package net.swedz.little_big_redstone.blockentity;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.api.Tickable;
import net.swedz.little_big_redstone.block.MicrochipBlock;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.logic.LogicContext;

import java.util.Set;

public final class MicrochipBlockEntity extends BlockEntity implements MenuProvider, Tickable
{
	private final Microchip microchip;
	
	public MicrochipBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(LBRBlocks.MICROCHIP_ENTITY.get(), pos, blockState);
		
		microchip = new Microchip();
	}
	
	public Microchip microchip()
	{
		return microchip;
	}
	
	public boolean isFaceListeningForRedstoneInput(Direction direction)
	{
		return microchip.redstoneIOCache().isFaceListeningForRedstoneInput(direction);
	}
	
	public boolean isFaceCapableForRedstoneOutput(Direction direction)
	{
		return microchip.redstoneIOCache().isFaceCapableForRedstoneOutput(direction);
	}
	
	@Override
	public Component getDisplayName()
	{
		return Component.translatable(LBRBlocks.MICROCHIP.identifier().location().toLanguageKey("block"));
	}
	
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
	{
		return new MicrochipMenu(containerId, inventory, worldPosition, () -> !this.isRemoved(), microchip);
	}
	
	private LogicContext buildLogicContext()
	{
		Set<Direction> inputPower = Sets.newHashSet();
		Set<Direction> outputPower = Sets.newHashSet();
		for(Direction direction : Direction.values())
		{
			boolean powered = this.getBlockState().getValue(MicrochipBlock.getDirectionalState(direction));
			if(powered)
			{
				if(this.isFaceListeningForRedstoneInput(direction))
				{
					inputPower.add(direction);
				}
				else if(this.isFaceCapableForRedstoneOutput(direction))
				{
					outputPower.add(direction);
				}
			}
		}
		return new LogicContext(inputPower, outputPower);
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		LogicContext context = this.buildLogicContext();
		microchip.tickLogic(context);
		
		if(microchip.isDirty() || context.isDirty())
		{
			microchip.markClean();
			
			var originalState = this.getBlockState();
			var newState = context.applyPoweredState(this.getBlockState());
			if(newState != originalState)
			{
				level.setBlock(worldPosition, newState, Block.UPDATE_ALL);
			}
			
			this.setChanged();
		}
	}
	
	@Override
	public void setChanged()
	{
		super.setChanged();
		
		// TODO update listening clients
	}
	
	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		
		if(tag.contains("microchip", Tag.TAG_COMPOUND))
		{
			Microchip.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("microchip"))
					.ifSuccess(microchip::loadFrom)
					.ifError((error) ->
							LBR.LOGGER.error("Failed to load microchip data at {}: {}", worldPosition.toShortString(), error.message()));
		}
		else
		{
			microchip.clear();
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		
		tag.put("microchip", Microchip.CODEC.encodeStart(NbtOps.INSTANCE, microchip).getOrThrow());
	}
}
