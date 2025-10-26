package net.swedz.little_big_redstone.gui.stickynote.view;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.gui.stickynote.StickyNoteScreen;
import net.swedz.little_big_redstone.gui.stickynote.edit.StickyNoteEditScreen;
import net.swedz.little_big_redstone.gui.stickynote.reference.StickyNoteReference;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

public final class StickyNoteViewScreen extends StickyNoteScreen
{
	private final Component text;
	
	public StickyNoteViewScreen(StickyNoteReference reference)
	{
		super(reference);
		
		this.text = StickyNote.parse(reference.text());
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
		minecraft.setScreen(new StickyNoteEditScreen(reference, true));
	}
	
	@Override
	public void render(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.render(vanilla, mouseX, mouseY, partialTick);
		
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos, topPos, 0);
		
		StickyNoteViewRenderer.renderText(graphics, new StickyNoteView(color, textColor, text));
		
		graphics.pose().popPose();
	}
}
