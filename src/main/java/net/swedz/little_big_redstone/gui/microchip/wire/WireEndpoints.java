package net.swedz.little_big_redstone.gui.microchip.wire;

import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.client.model.logic.LogicBakingModelData;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidgetContext;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.tesseract.neoforge.api.Assert;

public record WireEndpoints(
		boolean valid,
		int startX, int startY,
		int endX, int endY,
		boolean usePadding, boolean powered, int argb
)
{
	public WireEndpoints(
			int startX, int startY,
			int endX, int endY,
			boolean usePadding, boolean powered, int argb
	)
	{
		this(true, startX, startY, endX, endY, usePadding, powered, argb);
	}
	
	public WireEndpoints()
	{
		this(false, 0, 0, 0, 0, false, false, 0);
	}
	
	private static int getColor(LogicComponent<?, ?> output, DyeColor fallback)
	{
		return LogicBakingModelData.get(output).getColorSet(output.color().orElse(fallback)).foreground();
	}
	
	private static WireEndpoints of(int outputX, int outputY, LogicComponent<?, ?> output, int outputIndex,
									int inputX, int inputY, LogicComponent<?, ?> input, int inputIndex,
									boolean usePadding,
									DyeColor fallbackColor)
	{
		if(output == null || input == null)
		{
			return new WireEndpoints();
		}
		return new WireEndpoints(
				output.size().wireOutStartX(outputX),
				output.size().wireOutStartY(outputY, outputIndex, output.outputs()),
				input.size().wireInEndX(inputX),
				input.size().wireInEndY(inputY, inputIndex, input.inputs()),
				usePadding,
				output.output(outputIndex) > 0,
				getColor(output, fallbackColor)
		);
	}
	
	private static WireEndpoints of(int outputX, int outputY, LogicComponent<?, ?> output,
									int inputX, int inputY, LogicComponent<?, ?> input,
									Wire wire,
									boolean usePadding,
									DyeColor fallbackColor)
	{
		return of(
				outputX, outputY, output, wire.output().index(),
				inputX, inputY, input, wire.input().index(),
				usePadding, fallbackColor
		);
	}
	
	private static WireEndpoints of(LogicEntry output, int outputIndex,
									LogicEntry input, int inputIndex,
									boolean usePadding,
									DyeColor fallbackColor)
	{
		if(output == null || input == null)
		{
			return new WireEndpoints();
		}
		return of(
				output.x(), output.y(), output.component(), outputIndex,
				input.x(), input.y(), input.component(), inputIndex,
				usePadding,
				fallbackColor
		);
	}
	
	public static WireEndpoints of(MicrochipWidgetContext context, Wire wire)
	{
		return of(context.widget().color(), context.widget().microchip(), wire);
	}
	
	public static WireEndpoints of(DyeColor color, Microchip microchip, Wire wire)
	{
		LogicEntry output = microchip.components().get(wire.output().slot());
		LogicEntry input = microchip.components().get(wire.input().slot());
		return of(
				output, wire.output().index(),
				input, wire.input().index(),
				true,
				color
		);
	}
	
	public static WireEndpoints heldWire(MicrochipWidgetContext context)
	{
		Assert.that(context.widget().hasSelectedPort(), "Cannot create path of held wire with no selected port");
		
		var selectedPort = context.widget().getSelectedPort();
		var outputLogic = selectedPort.entry();
		
		if(context.shouldInsertWireToPort())
		{
			var inputLogic = context.logic();
			return WireEndpoints.of(
					outputLogic, selectedPort.index(),
					inputLogic, context.port().index(),
					true,
					context.widget().color()
			);
		}
		
		int startX = outputLogic.size().wireOutStartX(outputLogic.x());
		int startY = outputLogic.size().wireOutStartY(outputLogic.y(), selectedPort.index(), outputLogic.component().outputs());
		
		return new WireEndpoints(
				startX, startY,
				context.boardMouseX() + 1, context.boardMouseY() - 1,
				false,
				outputLogic.component().output(selectedPort.index()) > 0,
				getColor(outputLogic.component(), context.widget().color())
		);
	}
	
	public static WireEndpoints carried(MicrochipWidgetContext context,
										int carriedComponentSlot, LogicComponent<?, ?> component,
										Wire wire,
										int logicX, int logicY)
	{
		var microchip = context.widget().microchip();
		
		boolean isOutput = wire.output().slot() == carriedComponentSlot;
		var outputLogic = isOutput ? null : microchip.components().get(wire.output().slot());
		var outputLogicComponent = isOutput ? component : outputLogic.component();
		int outputX = isOutput ? logicX : outputLogic.x();
		int outputY = isOutput ? logicY : outputLogic.y();
		
		boolean isInput = wire.input().slot() == carriedComponentSlot;
		var inputLogic = isInput ? null : microchip.components().get(wire.input().slot());
		var inputLogicComponent = isInput ? component : inputLogic.component();
		int inputX = isInput ? logicX : inputLogic.x();
		int inputY = isInput ? logicY : inputLogic.y();
		
		return of(
				outputX, outputY, outputLogicComponent,
				inputX, inputY, inputLogicComponent,
				wire,
				true,
				context.widget().color()
		);
	}
}
