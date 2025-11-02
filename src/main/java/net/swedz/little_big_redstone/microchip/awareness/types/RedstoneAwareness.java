package net.swedz.little_big_redstone.microchip.awareness.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.little_big_redstone.LBRBlocks;
import net.swedz.little_big_redstone.block.channel.ChannelBlock;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlock;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessContext;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessType;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.awareness.MicrochipAwareness;
import net.swedz.little_big_redstone.microchip.object.logic.io.LogicIO;

public final class RedstoneAwareness extends MicrochipAwareness<RedstoneAwareness>
{
	private boolean initialized;
	
	private boolean[][] inputSides  = new boolean[16][6];
	private boolean[][] outputSides = new boolean[16][6];
	
	private int[][] inputPower           = new int[16][6];
	private int[][] outputPower          = new int[16][6];
	private int[][] outputPowerEvaluated = new int[16][6];
	
	public boolean[] getSides(int channel)
	{
		boolean[] sides = new boolean[6];
		for(int index = 0; index < sides.length; index++)
		{
			sides[index] = inputSides[channel][index] || outputSides[channel][index];
		}
		return sides;
	}
	
	public boolean isInput(Direction direction, int channel)
	{
		return inputSides[channel][direction.ordinal()];
	}
	
	public int getInputPower(Direction direction, int channel)
	{
		return inputPower[channel][direction.ordinal()];
	}
	
	public boolean isOutput(Direction direction, int channel)
	{
		return outputSides[channel][direction.ordinal()];
	}
	
	public int getOutputPower(Direction direction, int channel)
	{
		return outputPower[channel][direction.ordinal()];
	}
	
	public void setInputPowered(Direction direction, int channel, int signal)
	{
		int index = direction.ordinal();
		if(signal > 0)
		{
			outputPower[channel][index] = 0;
			inputPower[channel][index] = signal;
		}
		else
		{
			inputPower[channel][index] = 0;
		}
	}
	
	public boolean setOutputPowered(Direction direction, int channel, int signal)
	{
		int index = direction.ordinal();
		if(signal > outputPowerEvaluated[channel][index])
		{
			inputPower[channel][index] = 0;
			outputPowerEvaluated[channel][index] = signal;
			return true;
		}
		return false;
	}
	
	public int outputRedstoneSignal(BlockState state, Direction direction, int channel)
	{
		direction = direction.getOpposite();
		int index = direction.ordinal();
		boolean powered = outputSides[channel][index] && state.getValue(MicrochipBlock.getDirectionalState(direction));
		return powered ? outputPower[channel][index] : 0;
	}
	
	@Override
	public AwarenessType<RedstoneAwareness> type()
	{
		return AwarenessTypes.REDSTONE;
	}
	
	@Override
	public void load(Microchip microchip)
	{
		boolean[][] inputSides = new boolean[16][6];
		boolean[][] outputSides = new boolean[16][6];
		for(var entry : microchip.components())
		{
			if(entry.component() instanceof LogicIO io)
			{
				int channel = io.config().channel;
				var direction = io.config().direction.ordinal();
				if(inputSides[channel][direction] || outputSides[channel][direction])
				{
					continue;
				}
				if(io.config().input)
				{
					inputSides[channel][direction] = true;
				}
				else
				{
					outputSides[channel][direction] = true;
				}
			}
		}
		this.inputSides = inputSides;
		this.outputSides = outputSides;
	}
	
	@Override
	public void neighborChanged(AwarenessContext context, Block neighborBlock, BlockPos neighborPos, Direction neighborDirection, boolean movedByPiston)
	{
		var state = this.updateSignal(context.level(), context.pos(), context.state(), neighborPos, neighborDirection, 0, neighborDirection);
		context.level().setBlock(context.pos(), state, Block.UPDATE_ALL);
	}
	
	public BlockState updateSignal(Level level, BlockPos pos, BlockState state, BlockPos neighborPos, Direction neighborDirection, int channel, Direction channelDirection)
	{
		boolean isMicrochip = state.getBlock() instanceof MicrochipBlock;
		boolean isChannel = state.is(LBRBlocks.CHANNEL.get());
		
		if(!isMicrochip && !isChannel)
		{
			throw new IllegalStateException("Cannot update signal at block (" + pos.toShortString() + ") " + state.getBlockHolder().getKey().location());
		}
		
		int channelDirectionIndex = channelDirection.ordinal();
		if(inputSides[channel][channelDirectionIndex] && !outputSides[channel][channelDirectionIndex])
		{
			int signal = level.getSignal(neighborPos, neighborDirection);
			
			this.setInputPowered(channelDirection, channel, signal);
			
			if(isMicrochip)
			{
				state = state.setValue(MicrochipBlock.getDirectionalState(neighborDirection), signal > 0);
			}
			else if(isChannel)
			{
				state = state.setValue(ChannelBlock.POWER, signal);
			}
		}
		
		return state;
	}
	
	@Override
	public void preTick(AwarenessContext context)
	{
		var level = context.level();
		var pos = context.pos();
		
		if(!initialized)
		{
			int[][] inputPower = new int[16][6];
			for(var entry : context.microchip().components())
			{
				if(entry.component() instanceof LogicIO io)
				{
					int channel = io.config().channel;
					var direction = io.config().direction;
					int directionIndex = direction.ordinal();
					if(inputSides[channel][directionIndex])
					{
						int signal = level.getSignal(pos.relative(direction), direction);
						inputPower[channel][directionIndex] = signal;
					}
				}
			}
			this.inputPower = inputPower;
			initialized = true;
		}
		
		outputPowerEvaluated = new int[16][6];
	}
	
	@Override
	public void postTick(AwarenessContext context, boolean microchipDirty, boolean contextDirty)
	{
		var level = context.level();
		var pos = context.pos();
		
		boolean[] powerChanged = new boolean[16];
		for(int channel = 0; channel < outputPowerEvaluated.length; channel++)
		{
			int[] signals = outputPowerEvaluated[channel];
			for(int index = 0; index < signals.length; index++)
			{
				int signal = signals[index];
				if(outputPower[channel][index] != signal)
				{
					powerChanged[channel] = true;
					break;
				}
			}
		}
		outputPower = outputPowerEvaluated;
		
		// Update the microchip block itself
		{
			var state = context.state();
			var newState = state;
			for(var direction : Direction.values())
			{
				int index = direction.ordinal();
				newState = newState.setValue(MicrochipBlock.getDirectionalState(direction), inputPower[0][index] > 0 || outputPower[0][index] > 0);
			}
			if(powerChanged[0] || newState != state)
			{
				level.setBlock(pos, newState, Block.UPDATE_ALL);
				level.updateNeighborsAt(pos, state.getBlock());
			}
		}
		
		// Update the channel blocks
		{
			for(int channel = 1; channel < 16; channel++)
			{
				for(var direction : Direction.values())
				{
					var relative = pos.relative(direction, channel);
					// TODO cache the BlockStates using LocalizedListeners
					var state = level.getBlockState(relative);
					if(!ChannelBlock.is(state, direction.getOpposite(), channel))
					{
						continue;
					}
					
					var newState = state;
					int power = 0;
					boolean input = true;
					if(this.isInput(direction, channel))
					{
						power = this.getInputPower(direction, channel);
					}
					else if(this.isOutput(direction, channel))
					{
						power = this.getOutputPower(direction, channel);
						input = false;
					}
					newState = newState
							.setValue(ChannelBlock.INPUT, input)
							.setValue(ChannelBlock.POWER, power);
					
					if(powerChanged[channel] || newState != state)
					{
						level.setBlock(relative, newState, Block.UPDATE_ALL);
						level.updateNeighborsAt(relative, state.getBlock());
					}
				}
			}
		}
	}
}
