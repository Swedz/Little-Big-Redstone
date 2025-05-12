package net.swedz.little_big_redstone.gui.stickynote;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.gui.stickynote.edit.StickyNoteEditScreen;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;

public final class StickyNoteViewScreen extends StickyNoteScreen
{
	private final Component text;
	
	public StickyNoteViewScreen(int entityId, DyeColor color, String text)
	{
		super(entityId, color, text);
		
		this.text = StickyNote.parse(text);
	}
	
	private Button doneButton;
	private Button editButton;
	
	@Override
	protected void init()
	{
		super.init();
		
		doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> this.close()).bounds(leftPos, topPos + uiHeight - 20, 87, 20).build());
		
		editButton = this.addRenderableWidget(Button.builder(LBRText.STICKY_NOTE_EDIT.text(), (b) -> this.edit()).bounds(leftPos + 87 + 6, topPos + uiHeight - 20, 87, 20).build());
	}
	
	private void close()
	{
		minecraft.setScreen(null);
	}
	
	private void edit()
	{
		minecraft.setScreen(new StickyNoteEditScreen(entityId, color, initialText, true));
	}
	
	@Override
	public void renderBackground(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.renderBackground(vanilla, mouseX, mouseY, partialTick);
		
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos, topPos, 0);
		
		graphics.setColor(LBRColors.stickyNoteBackground(color));
		graphics.setTexture(LBR.id("textures/gui/sticky_note.png"));
		graphics.blit(0, 0, 0, 0, 256, 256);
		
		graphics.setColor(LBRColors.stickyNotePin(color));
		graphics.setTexture(LBR.id("textures/gui/sticky_note_pin.png"));
		graphics.blit(0, 0, 0, 0, 256, 256);
		
		graphics.resetColor();
		
		graphics.pose().popPose();
	}
	
	@Override
	public void render(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.render(vanilla, mouseX, mouseY, partialTick);
		
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos, topPos, 0);
		graphics.pose().translate(contentLeftPos, contentTopPos, 0);
		
		graphics.setColor(LBRColors.stickyNoteText(color));
		int index = 0;
		for(var line : font.split(text, maxContentWidth))
		{
			int y = index * font.lineHeight;
			graphics.drawString(line, 0, y, false);
			index++;
		}
		graphics.resetColor();
		
		graphics.pose().popPose();
	}
}
