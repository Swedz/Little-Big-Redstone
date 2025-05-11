package net.swedz.little_big_redstone.gui.stickynote.edit;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;

public final class StickyNoteEditScreen extends Screen
{
	private final int    entityId;
	private final String initialText;
	
	public StickyNoteEditScreen(int entityId, String text)
	{
		super(GameNarrator.NO_TITLE);
		
		this.entityId = entityId;
		this.initialText = text;
	}
	
	private StickyNoteEditWidget editWidget;
	
	@Override
	protected void init()
	{
		this.addRenderableWidget(editWidget = this.createNoteEditWidget(60, 60, 100, font.lineHeight * 10));
	}
	
	private StickyNoteEditWidget createNoteEditWidget(int x, int y, int width, int height)
	{
		return editWidget != null ?
				new StickyNoteEditWidget(font, x, y, width, height, editWidget) :
				new StickyNoteEditWidget(font, x, y, width, height, initialText);
	}
	
	@Override
	public void tick()
	{
		editWidget.tick();
	}
	
	@Override
	public void renderBackground(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.renderBackground(vanilla, mouseX, mouseY, partialTick);
		
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.pose().pushPose();
		graphics.pose().translate(50, 50, 0);
		
		graphics.setTexture(LBR.id("textures/gui/sticky_note.png"));
		graphics.blit(0, 0, 0, 0, 256, 256);
		
		graphics.pose().popPose();
	}
	
	@Override
	public void render(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.render(vanilla, mouseX, mouseY, partialTick);
	}
}
