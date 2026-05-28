package net.swedz.little_big_redstone.guide;

import guideme.color.SymbolicColor;
import guideme.internal.GuideMEClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.swedz.little_big_redstone.LBR;

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
		super(x, y, WIDTH, HEIGHT, LBR.text().guideButtonPause(), (b) ->
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
		this.setMessage(playing ? LBR.text().guideButtonPause() : LBR.text().guideButtonResume());
		this.setTooltip(Tooltip.create(this.getMessage()));
	}
	
	public boolean isPlaying()
	{
		return playing;
	}
	
	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
	{
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
		
		graphics.blit(LBR.id("textures/gui/guide/buttons.png"), this.getX(), this.getY(), playing ? 0 : 16, 0, WIDTH, HEIGHT, 64, 64, resolved);
	}
}
