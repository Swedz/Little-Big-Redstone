package net.swedz.little_big_redstone.gui.logicconfig.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;

public final class IconCycleButton<T extends Enum<T> & IconCycleButtonIcon> extends AbstractButton
{
	private final ResourceLocation atlas;
	private final List<T>          allowedValues;
	private final OnValueChange<T> onValueChange;
	
	private int valueIndex;
	private T   value;
	
	public IconCycleButton(int x, int y,
						   ResourceLocation atlas,
						   List<T> allowedValues,
						   T value, OnValueChange<T> onValueChange)
	{
		super(x, y, 18, 18, Component.empty());
		
		this.atlas = atlas;
		this.allowedValues = Collections.unmodifiableList(allowedValues);
		this.onValueChange = onValueChange;
		
		this.setValue(value);
	}
	
	public T value()
	{
		return value;
	}
	
	public void setValue(T value)
	{
		int index = 0;
		for(T other : allowedValues)
		{
			if(value == other)
			{
				valueIndex = index;
				this.value = value;
				return;
			}
			index++;
		}
		throw new IllegalArgumentException("Button does not support value " + value);
	}
	
	private void next()
	{
		valueIndex = (valueIndex + 1) % allowedValues.size();
		value = allowedValues.get(valueIndex);
	}
	
	@Override
	public void onPress()
	{
		this.next();
		onValueChange.onValueChange(this, value);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
	{
	}
	
	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		int u = value.u();
		int v = value.v();
		if(!active)
		{
			v += height * 2;
		}
		else if(this.isFocused())
		{
			v += height;
		}
		guiGraphics.blit(atlas, this.getX(), this.getY(), u, v, width, height);
	}
	
	public interface OnValueChange<T extends Enum<T> & IconCycleButtonIcon>
	{
		void onValueChange(IconCycleButton checkbox, T value);
	}
}
