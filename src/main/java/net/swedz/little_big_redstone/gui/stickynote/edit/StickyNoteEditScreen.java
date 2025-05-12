package net.swedz.little_big_redstone.gui.stickynote.edit;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.gui.stickynote.StickyNoteScreen;
import net.swedz.little_big_redstone.gui.stickynote.StickyNoteViewScreen;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;

import java.util.function.Supplier;

public final class StickyNoteEditScreen extends StickyNoteScreen
{
	private final boolean shouldReturnToView;
	
	public StickyNoteEditScreen(int entityId, DyeColor color, String text, boolean shouldReturnToView)
	{
		super(entityId, color, text);
		
		this.shouldReturnToView = shouldReturnToView;
	}
	
	private StickyNoteEditWidget editWidget;
	private Button               doneButton;
	
	@Override
	protected void init()
	{
		super.init();
		
		editWidget = this.addRenderableWidget(this.createNoteEditWidget(leftPos + contentLeftPos, topPos + contentTopPos, maxContentWidth, maxContentHeight, () -> LBRColors.stickyNoteText(color)));
		
		doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> this.done()).bounds(leftPos, topPos + uiHeight - 20, uiWidth, 20).build());
	}
	
	private StickyNoteEditWidget createNoteEditWidget(int x, int y, int width, int height, Supplier<Integer> color)
	{
		return editWidget != null ?
				new StickyNoteEditWidget(font, x, y, width, height, editWidget, color) :
				new StickyNoteEditWidget(font, x, y, width, height, initialText, color);
	}
	
	private void close()
	{
		minecraft.setScreen(shouldReturnToView ? new StickyNoteViewScreen(entityId, color, editWidget.note().text()) : null);
	}
	
	private void done()
	{
		this.close();
		
		if(editWidget.note().isTextModified())
		{
			new StickyNotePacket(entityId, StickyNotePacket.Action.DONE_EDIT, editWidget.note().text()).sendToServer();
		}
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
}
