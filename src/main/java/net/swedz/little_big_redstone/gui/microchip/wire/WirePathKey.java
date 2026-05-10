package net.swedz.little_big_redstone.gui.microchip.wire;

import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidgetContext;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.little_big_redstone.microchip.wire.WirePort;
import net.swedz.tesseract.neoforge.api.Assert;
import net.swedz.tesseract.neoforge.api.Bounds;

import java.util.List;
import java.util.Optional;

public record WirePathKey(
		Optional<Wire> wire,
		int startX,
		int startY,
		int endX,
		int endY,
		Optional<List<Bounds>> additionalAvoidBounds
)
{
	public static WirePathKey placed(Microchip microchip, Wire wire)
	{
		var output = microchip.components().get(wire.output().slot());
		int outputX = output.x();
		int outputY = output.y();
		int outputIndex = wire.output().index();
		
		var input = microchip.components().get(wire.input().slot());
		int inputX = input.x();
		int inputY = input.y();
		int inputIndex = wire.input().index();
		
		return new WirePathKey(
				Optional.of(wire),
				output.size().wireOutStartX(outputX),
				output.size().wireOutStartY(outputY, outputIndex, output.component().outputs()),
				input.size().wireInEndX(inputX),
				input.size().wireInEndY(inputY, inputIndex, input.component().inputs()),
				Optional.empty()
		);
	}
	
	public static WirePathKey held(MicrochipWidgetContext context)
	{
		Assert.that(context.widget().hasSelectedPort(), "Cannot create path of held wire with no selected port");
		
		var selectedPort = context.widget().getSelectedPort();
		var outputLogic = selectedPort.entry();
		
		if(context.shouldInsertWireToPort())
		{
			var inputLogic = context.logic();
			var wire = new Wire(
					new WirePort(selectedPort),
					new WirePort(context.port())
			);
			return placed(context.widget().microchip(), wire);
		}
		
		int startX = outputLogic.size().wireOutStartX(outputLogic.x());
		int startY = outputLogic.size().wireOutStartY(outputLogic.y(), selectedPort.index(), outputLogic.component().outputs());
		
		return new WirePathKey(
				Optional.empty(),
				startX,
				startY,
				context.boardMouseX() - 4,
				context.boardMouseY() - 1,
				Optional.empty()
		);
	}
	
	public static WirePathKey carried(
			MicrochipWidgetContext context,
			int carriedComponentSlot,
			LogicComponent<?, ?> component,
			Wire wire,
			int logicX,
			int logicY
	)
	{
		var microchip = context.widget().microchip();
		
		boolean isOutput = wire.output().slot() == carriedComponentSlot;
		var outputLogic = isOutput ? null : microchip.components().get(wire.output().slot());
		var outputLogicComponent = isOutput ? component : outputLogic.component();
		int outputX = isOutput ? logicX : outputLogic.x();
		int outputY = isOutput ? logicY : outputLogic.y();
		int outputIndex = wire.output().index();
		
		boolean isInput = wire.input().slot() == carriedComponentSlot;
		var inputLogic = isInput ? null : microchip.components().get(wire.input().slot());
		var inputLogicComponent = isInput ? component : inputLogic.component();
		int inputX = isInput ? logicX : inputLogic.x();
		int inputY = isInput ? logicY : inputLogic.y();
		int inputIndex = wire.input().index();
		
		List<Bounds> additionalAvoidBounds = List.of(context.widget().panel().wires().pathing().mutateComponentBounds(component.size().toBounds(logicX, logicY)));
		
		return new WirePathKey(
				Optional.empty(),
				outputLogicComponent.size().wireOutStartX(outputX),
				outputLogicComponent.size().wireOutStartY(outputY, outputIndex, outputLogicComponent.outputs()),
				inputLogicComponent.size().wireInEndX(inputX),
				inputLogicComponent.size().wireInEndY(inputY, inputIndex, inputLogicComponent.inputs()),
				Optional.of(additionalAvoidBounds)
		);
	}
}
