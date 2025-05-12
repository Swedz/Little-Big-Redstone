package net.swedz.little_big_redstone.gui.stickynote.edit;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.network.packet.StickyNotePacket;

import java.util.function.Supplier;

public final class StickyNoteEditScreen extends Screen
{
	private final int      entityId;
	private final DyeColor color;
	private final String   initialText;
	
	public StickyNoteEditScreen(int entityId, DyeColor color, String text)
	{
		super(GameNarrator.NO_TITLE);
		
		this.entityId = entityId;
		this.color = color;
		this.initialText = text;
	}
	
	private StickyNoteEditWidget editWidget;
	private Button               doneButton;
	
	@Override
	protected void init()
	{
		editWidget = this.addRenderableWidget(this.createNoteEditWidget((width / 2) - (180 / 2) + 5, 27, 170, font.lineHeight * 14, () -> LBRColors.stickyNoteText(color)));
		
		doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> this.done()).bounds(width / 2 - 98 / 2, 196, 98, 20).build());
	}
	
	private StickyNoteEditWidget createNoteEditWidget(int x, int y, int width, int height, Supplier<Integer> color)
	{
		return editWidget != null ?
				new StickyNoteEditWidget(font, x, y, width, height, editWidget, color) :
				new StickyNoteEditWidget(font, x, y, width, height, initialText, color);
	}
	
	private void close()
	{
		minecraft.setScreen(null);
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
		graphics.pose().translate(width / 2f - 180 / 2f, 2, 0);
		
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
