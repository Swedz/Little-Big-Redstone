package net.swedz.little_big_redstone.gui.logicconfig.widget.slider;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.swedz.little_big_redstone.gui.logicconfig.widget.LogicConfigButtonHelper;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;
import org.lwjgl.glfw.GLFW;

public class SliderLogicConfigWidget extends ExtendedSlider implements LogicConfigButtonHelper
{
	private final int color;
	
	private final boolean isInteger;
	
	private final ValueStringifier valueStringifier;
	private final OnValueChange    onChange;
	
	private String typed = "";
	
	public SliderLogicConfigWidget(int x, int y, int width, int height, int color, Component prefix, Component suffix,
								   double minValue, double maxValue, double initialValue, double stepSize, int precision, boolean drawString,
								   ValueStringifier valueStringifier, OnValueChange onChange)
	{
		super(x, y, width, height, prefix, suffix, minValue, maxValue, initialValue, stepSize, precision, drawString);
		
		this.color = color;
		
		this.isInteger = Mth.equal(stepSize, Math.floor(stepSize));
		
		this.valueStringifier = valueStringifier;
		this.onChange = onChange;
		
		this.updateMessage();
	}
	
	public SliderLogicConfigWidget(int x, int y, int width, int height, int color, Component prefix, Component suffix,
								   double minValue, double maxValue, double initialValue, boolean drawString,
								   ValueStringifier valueStringifier, OnValueChange onChange)
	{
		super(x, y, width, height, prefix, suffix, minValue, maxValue, initialValue, drawString);
		
		this.color = color;
		
		this.isInteger = Mth.equal(stepSize, Math.floor(stepSize));
		
		this.valueStringifier = valueStringifier;
		this.onChange = onChange;
		
		this.updateMessage();
	}
	
	@Override
	protected void updateMessage()
	{
		// We skip updates when the value stringifier has not been set yet
		if(valueStringifier != null)
		{
			if(drawString)
			{
				this.setMessage(Component.literal("").append(prefix).append(valueStringifier.stringify(this.getValue(), this.getValueString())).append(suffix));
			}
			else
			{
				this.setMessage(Component.empty());
			}
			onChange.onValueChange(this, minValue + (value * (maxValue - minValue)));
		}
	}
	
	@Override
	public void onClick(double mouseX, double mouseY)
	{
		super.onClick(mouseX, mouseY);
		typed = this.getValueString();
	}
	
	@Override
	protected void onDrag(double mouseX, double mouseY, double dragX, double dragY)
	{
		super.onDrag(mouseX, mouseY, dragX, dragY);
		typed = this.getValueString();
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		// We cannot support stepSizes of <= 0 because ExtendedSlider#setSliderValue is private
		if(stepSize <= 0D)
		{
			return false;
		}
		
		boolean left = keyCode == GLFW.GLFW_KEY_LEFT;
		if(left || keyCode == GLFW.GLFW_KEY_RIGHT)
		{
			if(minValue > maxValue)
			{
				left = !left;
			}
			float step = left ? -1 : 1;
			if(Screen.hasShiftDown())
			{
				step *= 10;
			}
			this.setValue(this.getValue() + step * stepSize);
			typed = this.getValueString();
			return false;
		}
		
		if(isInteger)
		{
			if(keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9)
			{
				int number = keyCode - GLFW.GLFW_KEY_0;
				typed += number;
				this.setValue(Integer.parseInt(typed));
				typed = this.getValueString();
				return false;
			}
			else if(keyCode == GLFW.GLFW_KEY_BACKSPACE && !typed.isEmpty())
			{
				typed = typed.substring(0, typed.length() - 1);
				this.setValue(typed.isEmpty() ? 0 : Integer.parseInt(typed));
				return false;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
	{
		// We cannot support stepSizes of <= 0 because ExtendedSlider#setSliderValue is private
		if(stepSize <= 0)
		{
			return false;
		}
		
		boolean left = scrollY < 0;
		if(minValue > maxValue)
		{
			left = !left;
		}
		float step = left ? -1 : 1;
		if(Screen.hasShiftDown())
		{
			step *= 10;
		}
		this.setValue(this.getValue() + step * stepSize);
		typed = "";
		
		return true;
	}
	
	@Override
	public void renderWidget(GuiGraphics internal, int mouseX, int mouseY, float partialTick)
	{
		var graphics = new TesseractGuiGraphics(internal);
		
		graphics.setColor(1, 1, 1, alpha);
		
		this.renderBackground(graphics, partialTick, this.getX() + (int) (value * (width - 8D)), this.getY(), 8, height, color, active && this.isHoveredOrFocused());
		
		this.renderBorder(graphics, this.getX(), this.getY(), width, height, color);
		this.renderBorder(graphics, this.getX() + (int) (value * (width - 8D)), this.getY(), 8, height, color);
		
		graphics.setColor(color);
		graphics.setStringDropShadow(false);
		graphics.drawCenteredString(this.getMessage(), this.getX() + (width / 2f), this.getY() + (height / 2f));
		graphics.resetColor();
	}
	
	public interface OnValueChange
	{
		void onValueChange(SliderLogicConfigWidget button, double value);
	}
	
	public interface ValueStringifier
	{
		Component stringify(double value, String stringValue);
	}
}
