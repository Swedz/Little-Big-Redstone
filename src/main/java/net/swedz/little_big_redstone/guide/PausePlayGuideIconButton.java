package net.swedz.little_big_redstone.guide;

import guideme.color.SymbolicColor;
import guideme.internal.GuideMEClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PausePlayGuideIconButton extends Button
{
	private static final int WIDTH  = 16;
	private static final int HEIGHT = 16;
	
	private boolean playing = true;
	
	public PausePlayGuideIconButton(int x, int y, Runnable callback)
	{
		this(x, y, (__) -> callback.run());
	}
	
	public PausePlayGuideIconButton(int x, int y, Consumer<PausePlayGuideIconButton> callback)
	{
		super(x, y, WIDTH, HEIGHT, LBRText.GUIDE_BUTTON_PAUSE.text(), (b) ->
		{
			var button = (PausePlayGuideIconButton) b;
			button.toggle();
			callback.accept(button);
		}, Supplier::get);
		this.setTooltip(Tooltip.create(this.getMessage()));
	}
	
	private void toggle()
	{
		playing = !playing;
		this.setMessage(playing ? LBRText.GUIDE_BUTTON_PAUSE.text() : LBRText.GUIDE_BUTTON_RESUME.text());
		this.setTooltip(Tooltip.create(this.getMessage()));
	}
	
	public boolean isPlaying()
	{
		return playing;
	}
	
	@Override
	public void renderWidget(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		var color = SymbolicColor.ICON_BUTTON_NORMAL;
		if(!this.isActive())
		{
			color = SymbolicColor.ICON_BUTTON_DISABLED;
		}
		else if(this.isHovered())
		{
			color = SymbolicColor.ICON_BUTTON_HOVER;
		}
		var resolved = color.resolve(GuideMEClient.currentLightDarkMode());
		
		graphics.setTextures(LBR.id("textures/gui/guide/buttons.png"));
		graphics.blit(this.getX(), this.getY(), playing ? 0f : 16f, 0f, WIDTH, HEIGHT, 64, 64, resolved);
	}
}
