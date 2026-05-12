package net.swedz.little_big_redstone.gui.logicconfig.widget.cycle;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.gui.logicconfig.widget.LogicConfigButtonHelper;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.List;

public class CycleLogicConfigButton<T> extends AbstractButton implements LogicConfigButtonHelper
{
	private final int color;
	
	private final List<T>             allowedValues;
	private final boolean             displayOnlyValue;
	private final ValueStringifier<T> stringifier;
	private final OnValueChange<T>    onValueChange;
	
	private int valueIndex;
	private T   value;
	
	public CycleLogicConfigButton(int x, int y, int width, int height, int color, Component message,
								  boolean displayOnlyValue,
								  T initialValue,
								  List<T> allowedValues,
								  ValueStringifier<T> stringifier,
								  OnValueChange<T> onValueChange)
	{
		super(x, y, width, height, message);
		
		this.color = color;
		
		this.displayOnlyValue = displayOnlyValue;
		this.allowedValues = allowedValues;
		this.stringifier = stringifier;
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
	public void onPress()
	{
		// We use the Neo onClick(double, double, int) method instead
	}
	
	@Override
	protected boolean isValidClickButton(int button)
	{
		return button == InputConstants.MOUSE_BUTTON_LEFT ||
			   button == InputConstants.MOUSE_BUTTON_RIGHT;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY, int button)
	{
		if(button == InputConstants.MOUSE_BUTTON_LEFT)
		{
			this.next();
		}
		else if(button == InputConstants.MOUSE_BUTTON_RIGHT)
		{
			this.previous();
		}
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
	{
	}
	
	@Override
	protected void renderWidget(GuiGraphics internal, int mouseX, int mouseY, float partialTick)
	{
		var graphics = new TesseractGuiGraphics(internal);
		
		this.renderBackground(graphics, partialTick, this.getX(), this.getY(), width, height, color, active && this.isHoveredOrFocused());
		
		this.renderBorder(graphics, this.getX(), this.getY(), width, height, color);
		
		graphics.setColor(color);
		graphics.setStringDropShadow(false);
		var valueText = stringifier.stringify(value);
		var text = displayOnlyValue ? valueText : CommonComponents.optionNameValue(this.getMessage(), valueText);
		graphics.drawCenteredString(text, this.getX() + (width / 2f), this.getY() + (height / 2f));
		graphics.resetColor();
	}
	
	public interface OnValueChange<T>
	{
		void onValueChange(CycleLogicConfigButton<T> checkbox, T value);
	}
	
	public interface ValueStringifier<T>
	{
		Component stringify(T value);
	}
}
