package net.swedz.little_big_redstone.gui.logicconfig.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

public class LogicConfigButton extends AbstractButton implements LogicConfigButtonHelper
{
	private final int color;
	
	private final OnPress onPress;
	
	public LogicConfigButton(int x, int y, int width, int height, int color, Component message, OnPress onPress)
	{
		super(x, y, width, height, message);
		
		this.color = color;
		
		this.onPress = onPress;
	}
	
	@Override
	public void onPress()
	{
		onPress.onPress(this);
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
		graphics.drawCenteredString(this.getMessage(), this.getX() + (width / 2f), this.getY() + (height / 2f));
		graphics.resetColor();
	}
	
	public interface OnPress
	{
		void onPress(LogicConfigButton button);
	}
}
