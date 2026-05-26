package net.swedz.little_big_redstone.gui.logicconfig.widget.iconcycle;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.swedz.little_big_redstone.gui.logicconfig.widget.LogicConfigButtonHelper;

import java.util.Collections;
import java.util.List;

public class IconCycleLogicConfigButton<T extends IconCycleLogicConfigButtonIcon> extends AbstractButton implements LogicConfigButtonHelper
{
	private final int color;
	
	private final Identifier atlas;
	private final List<T>          allowedValues;
	private final OnValueChange<T> onValueChange;
	
	private int valueIndex;
	private T   value;
	
	public IconCycleLogicConfigButton(int x, int y,
									  int color,
									  Identifier atlas,
									  T initialValue,
									  List<T> allowedValues,
									  OnValueChange<T> onValueChange)
	{
		super(x, y, 18, 18, Component.empty());
		
		this.color = color;
		
		this.atlas = atlas;
		this.allowedValues = Collections.unmodifiableList(allowedValues);
		this.onValueChange = onValueChange;
		
		this.setValue(initialValue);
	}
	
	public T value()
	{
		return value;
	}
	
	public void setValue(T value)
	{
		int index = allowedValues.indexOf(value);
		if(index == -1)
		{
			throw new IllegalArgumentException("Button does not support value " + value);
		}
		valueIndex = index;
		this.value = value;
		onValueChange.onValueChange(this, value);
	}
	
	private void step(int step)
	{
		valueIndex = (valueIndex + step) % allowedValues.size();
		value = allowedValues.get(valueIndex);
		onValueChange.onValueChange(this, value);
	}
	
	private void previous()
	{
		this.step(allowedValues.size() - 1);
	}
	
	private void next()
	{
		this.step(1);
	}
	
	@Override
	public void onPress(InputWithModifiers input)
	{
		// We use the onClick(MouseButtonEvent, boolean) method instead
	}
	
	@Override
	protected boolean isValidClickButton(MouseButtonInfo button)
	{
		return button.isLeft() || button.isRight();
	}
	
	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick)
	{
		if(event.isLeft())
		{
			this.next();
		}
		else if(event.isRight())
		{
			this.previous();
		}
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
	{
	}
	
	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
	{
		this.extractBackground(graphics, partialTick, this.getX(), this.getY(), width, height, color, active && this.isHoveredOrFocused());
		
		this.extractBorder(graphics, this.getX(), this.getY(), width, height, color);
		
		int u = value.u();
		int v = value.v();
		if(!active)
		{
			v += height * 2;
		}
		else if(this.isHoveredOrFocused())
		{
			v += height;
		}
		graphics.blit(RenderPipelines.GUI_TEXTURED, atlas, this.getX(), this.getY(), u, v, width, height, 256, 256, color);
	}
	
	public interface OnValueChange<T extends IconCycleLogicConfigButtonIcon>
	{
		void onValueChange(IconCycleLogicConfigButton checkbox, T value);
	}
}
