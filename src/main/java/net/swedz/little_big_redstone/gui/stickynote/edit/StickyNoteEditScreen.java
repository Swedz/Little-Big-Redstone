package net.swedz.little_big_redstone.gui.stickynote.edit;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
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
		editWidget = this.addRenderableWidget(this.createNoteEditWidget((width / 2) - (180 / 2) + 5, 27, 170, font.lineHeight * 14, () -> getTextColor(color)));
		
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
	
	private static int getBackgroundColor(DyeColor color)
	{
		return switch (color)
		{
			case WHITE -> 0xFFCED4D5;
			case ORANGE -> 0xFFE06100;
			case MAGENTA -> 0xFFA9309F;
			case LIGHT_BLUE -> 0xFF2389C7;
			case YELLOW -> 0xFFEFAE15;
			case LIME -> 0xFF5FA919;
			case PINK -> 0xFFD5668F;
			case GRAY -> 0xFF36393D;
			case LIGHT_GRAY -> 0xFF7D7D73;
			case CYAN -> 0xFF167889;
			case PURPLE -> 0xFF641F9C;
			case BLUE -> 0xFF2C2E8F;
			case BROWN -> 0xFF5F3B1F;
			case GREEN -> 0xFF495B24;
			case RED -> 0xFF8E2121;
			case BLACK -> 0xFF080A0F;
		};
	}
	
	private static int getPinColor(DyeColor color)
	{
		return switch (color)
		{
			case WHITE -> 0xFF696868;
			default -> 0xFFD1CFCF;
		};
	}
	
	private static int getTextColor(DyeColor color)
	{
		return switch (color)
		{
			case GRAY, BLACK -> 0xFFFFFFFF;
			default -> 0xFF000000;
		};
	}
	
	@Override
	public void renderBackground(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.renderBackground(vanilla, mouseX, mouseY, partialTick);
		
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.pose().pushPose();
		graphics.pose().translate(width / 2f - 180 / 2f, 2, 0);
		
		graphics.setColor(getBackgroundColor(color));
		graphics.setTexture(LBR.id("textures/gui/sticky_note.png"));
		graphics.blit(0, 0, 0, 0, 256, 256);
		
		graphics.setColor(getPinColor(color));
		graphics.setTexture(LBR.id("textures/gui/sticky_note_pin.png"));
		graphics.blit(0, 0, 0, 0, 256, 256);
		
		graphics.resetColor();
		
		graphics.pose().popPose();
	}
	
	@Override
	public void render(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.render(vanilla, mouseX, mouseY, partialTick);
	}
}
