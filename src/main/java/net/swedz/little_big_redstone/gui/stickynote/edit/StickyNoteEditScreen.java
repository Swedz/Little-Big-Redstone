package net.swedz.little_big_redstone.gui.stickynote.edit;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.gui.stickynote.StickyNoteScreen;
import net.swedz.little_big_redstone.gui.stickynote.view.StickyNoteViewScreen;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;

import java.util.function.Supplier;

public final class StickyNoteEditScreen extends StickyNoteScreen
{
	private final boolean shouldReturnToView;
	
	public StickyNoteEditScreen(int entityId, DyeColor color, DyeColor textColor, String text, boolean shouldReturnToView)
	{
		super(entityId, color, textColor, text);
		
		this.shouldReturnToView = shouldReturnToView;
	}
	
	private StickyNoteEditWidget editWidget;
	private Button               doneButton;
	
	@Override
	protected void init()
	{
		super.init();
		
		editWidget = this.addRenderableWidget(this.createNoteEditWidget(leftPos + contentLeftPos, topPos + contentTopPos, maxContentWidth, maxContentHeight, textColor::getTextColor));
		
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
		minecraft.setScreen(shouldReturnToView ? new StickyNoteViewScreen(entityId, color, textColor, editWidget.note().text()) : null);
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
		
		var entity = minecraft.level.getEntity(entityId);
		if(entity == null || entity.distanceTo(minecraft.player) > 16)
		{
			minecraft.setScreen(null);
		}
	}
}
