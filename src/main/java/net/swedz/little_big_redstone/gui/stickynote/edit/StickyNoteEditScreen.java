package net.swedz.little_big_redstone.gui.stickynote.edit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.stickynote.StickyNoteScreen;
import net.swedz.little_big_redstone.gui.stickynote.reference.MicrochipStickyNoteReference;
import net.swedz.little_big_redstone.gui.stickynote.reference.StickyNoteReference;
import net.swedz.little_big_redstone.gui.stickynote.view.StickyNoteViewScreen;

import java.util.function.Supplier;

public final class StickyNoteEditScreen extends StickyNoteScreen
{
	private final boolean shouldReturnToView;
	
	public StickyNoteEditScreen(StickyNoteReference reference, boolean shouldReturnToView)
	{
		super(reference);
		
		this.shouldReturnToView = shouldReturnToView;
	}
	
	private StickyNoteEditWidget editWidget;
	private Button               doneButton;
	
	@Override
	protected void init()
	{
		super.init();
		
		editWidget = this.addRenderableWidget(this.createNoteEditWidget(leftPos + contentLeftPos, topPos + contentTopPos, maxContentWidth, maxContentHeight, () -> LBRColors.stickyNoteText(textColor)));
		
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
		Screen screen = null;
		if(shouldReturnToView)
		{
			screen = new StickyNoteViewScreen(reference.withText(editWidget.note().text()));
		}
		else if(reference instanceof MicrochipStickyNoteReference &&
				minecraft.player.containerMenu instanceof MicrochipMenu menu)
		{
			screen = new MicrochipScreen(menu, minecraft.player.getInventory(), Component.empty());
		}
		minecraft.setScreen(screen);
	}
	
	private void done()
	{
		this.close();
		
		if(editWidget.note().isTextModified())
		{
			reference.withText(editWidget.note().text()).saveClient(minecraft.level, minecraft.player);
		}
	}
	
	@Override
	public void tick()
	{
		editWidget.tick();
		
		if(!reference.isStillValid(Minecraft.getInstance().level, Minecraft.getInstance().player))
		{
			minecraft.setScreen(null);
		}
	}
}
