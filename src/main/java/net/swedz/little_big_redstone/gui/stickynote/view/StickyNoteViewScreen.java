package net.swedz.little_big_redstone.gui.stickynote.view;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.gui.stickynote.StickyNoteScreen;
import net.swedz.little_big_redstone.gui.stickynote.edit.StickyNoteEditScreen;
import net.swedz.little_big_redstone.gui.stickynote.reference.StickyNoteReference;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;

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
		
		if(reference.canEdit())
		{
			doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> this.close()).bounds(leftPos, topPos + uiHeight - 20, 87, 20).build());
			
			editButton = this.addRenderableWidget(Button.builder(LBR.text().stickyNoteEdit(), (b) -> this.edit()).bounds(leftPos + 87 + 6, topPos + uiHeight - 20, 87, 20).build());
		}
		else
		{
			doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> this.close()).bounds(leftPos, topPos + uiHeight - 20, uiWidth, 20).build());
		}
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
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
	{
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		
		graphics.pose().pushMatrix();
		graphics.pose().translate(leftPos, topPos);
		
		StickyNoteViewRenderer.extractText(graphics, new StickyNoteView(color, textColor, text));
		
		graphics.pose().popMatrix();
	}
}
