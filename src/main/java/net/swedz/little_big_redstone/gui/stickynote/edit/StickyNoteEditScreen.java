package net.swedz.little_big_redstone.gui.stickynote.edit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.noteboard.NoteBoardMenu;
import net.swedz.little_big_redstone.gui.noteboard.NoteBoardScreen;
import net.swedz.little_big_redstone.gui.stickynote.StickyNoteScreen;
import net.swedz.little_big_redstone.gui.stickynote.reference.MicrochipStickyNoteReference;
import net.swedz.little_big_redstone.gui.stickynote.reference.NoteBoardStickyNoteReference;
import net.swedz.little_big_redstone.gui.stickynote.reference.StickyNoteReference;
import net.swedz.little_big_redstone.gui.stickynote.view.StickyNoteViewScreen;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

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
		
		leftPos -= uiWidth / 2 + 10;
		
		editWidget = this.addRenderableWidget(this.createNoteEditWidget(leftPos + contentLeftPos, topPos + contentTopPos, maxContentWidth, maxContentHeight, () -> LBRColors.stickyNoteText(textColor)));
		editWidget.note().editor().setCursorToEnd();
		this.setFocused(editWidget);
		
		doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> this.done()).bounds(leftPos + uiWidth / 2 + 10, topPos + uiHeight - 20, uiWidth, 20).build());
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
		else if(reference instanceof NoteBoardStickyNoteReference noteBoardReference &&
				minecraft.player.containerMenu instanceof NoteBoardMenu menu)
		{
			screen = new NoteBoardScreen(menu, minecraft.player.getInventory(), Component.empty());
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
	
	private void renderPreview(TesseractGuiGraphics graphics)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos + uiWidth + 20, topPos, 0);
		
		StickyNoteViewRenderer.renderBackground(graphics, new StickyNoteView(color, textColor, Component.empty()));
		StickyNoteViewRenderer.renderText(graphics, new StickyNoteView(color, textColor, editWidget.note().getDisplay().parsed()));
		
		graphics.pose().popPose();
	}
	
	@Override
	public void render(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.render(vanilla, mouseX, mouseY, partialTick);
		
		var graphics = new TesseractGuiGraphics(vanilla);
		this.renderPreview(graphics);
	}
}
