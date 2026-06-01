package net.swedz.little_big_redstone.gui.logicconfig.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public class LogicConfigButton extends AbstractButton implements LogicConfigButtonHelper
{
	private final int color;
	
	private final OnPress onPress;
	
	public LogicConfigButton(
			int x,
			int y,
			int width,
			int height,
			int color,
			Component message,
			OnPress onPress
	)
	{
		super(x, y, width, height, message);
		
		this.color = color;
		
		this.onPress = onPress;
	}
	
	@Override
	public void onPress(InputWithModifiers input)
	{
		onPress.onPress(this);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
	{
	}
	
	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
	{
		this.extractBackground(graphics, this.getX(), this.getY(), width, height, color, active && this.isHoveredOrFocused());
		
		this.extractBorder(graphics, this.getX(), this.getY(), width, height, color);
		
		var font = Minecraft.getInstance().font;
		graphics.centeredText(
				font,
				this.getMessage(),
				Math.round(this.getX() + (width / 2f)),
				Math.round(this.getY() + (height / 2f) - (font.lineHeight / 2f)),
				color,
				false
		);
	}
	
	public interface OnPress
	{
		void onPress(LogicConfigButton button);
	}
}
