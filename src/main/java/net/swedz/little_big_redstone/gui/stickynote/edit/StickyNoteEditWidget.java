package net.swedz.little_big_redstone.gui.stickynote.edit;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

import static com.mojang.blaze3d.platform.InputConstants.*;

public final class StickyNoteEditWidget implements GuiEventListener, Renderable, NarratableEntry
{
	private final Font font;
	
	private final int x, y, width, height;
	
	private final StickyNoteEdit note;
	
	private int     tick;
	private boolean focused;
	
	public StickyNoteEditWidget(Font font, int x, int y, int width, int height,
								String text)
	{
		this.font = font;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		note = new StickyNoteEdit(font, width, height, text);
	}
	
	public StickyNoteEditWidget(Font font, int x, int y, int width, int height,
								StickyNoteEditWidget previous)
	{
		this.font = font;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		note = previous.note;
	}
	
	public StickyNoteEdit note()
	{
		return note;
	}
	
	public void tick()
	{
		tick++;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(!this.isMouseOver(mouseX, mouseY))
		{
			return false;
		}
		
		if(button == 0)
		{
			int localMouseX = (int) (mouseX - x);
			int localMouseY = (int) (mouseY - y);
			note.jumpTo(localMouseX, localMouseY);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if(Screen.isSelectAll(keyCode))
		{
			note.selectAll();
			return true;
		}
		else if(Screen.isCopy(keyCode))
		{
			note.copy();
			return true;
		}
		else if(Screen.isPaste(keyCode))
		{
			note.paste();
			return true;
		}
		else if(Screen.isCut(keyCode))
		{
			note.cut();
			return true;
		}
		else
		{
			boolean ctrl = Screen.hasControlDown();
			boolean shift = Screen.hasShiftDown();
			return switch (keyCode)
			{
				case KEY_RETURN, KEY_NUMPADENTER ->
				{
					note.insertNewLine();
					yield true;
				}
				case KEY_BACKSPACE ->
				{
					note.backspace(ctrl);
					yield true;
				}
				case KEY_DELETE ->
				{
					note.delete(ctrl);
					yield true;
				}
				case KEY_LEFT ->
				{
					note.moveLeft(shift, ctrl);
					yield true;
				}
				case KEY_RIGHT ->
				{
					note.moveRight(shift, ctrl);
					yield true;
				}
				case KEY_UP ->
				{
					note.moveUp(shift);
					yield true;
				}
				case KEY_DOWN ->
				{
					note.moveDown(shift);
					yield true;
				}
				default -> false;
			};
		}
	}
	
	@Override
	public boolean charTyped(char character, int modifiers)
	{
		return note.type(character);
	}
	
	@Override
	public void render(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.pose().pushPose();
		graphics.pose().translate(x, y, 0);
		
		this.renderLines(graphics);
		
		graphics.setColor(1, 0, 0, 0.25f);
		graphics.fill(0, 0, width, height);
		
		graphics.pose().popPose();
	}
	
	private void renderLines(TesseractGuiGraphics graphics)
	{
		graphics.setColor(0, 0, 0, 1);
		if(note.text().isEmpty())
		{
			if(this.isFocused())
			{
				this.renderCursor(graphics, 0, 0, true);
			}
		}
		else
		{
			var index = new MutableInt();
			var cursorRenderer = new MutableObject<Runnable>();
			font.getSplitter().splitLines(note.text(), width, Style.EMPTY, false, (__, start, end) ->
					this.renderLine(graphics, index.getAndIncrement(), start, end, cursorRenderer));
			if(cursorRenderer.getValue() != null)
			{
				cursorRenderer.getValue().run();
			}
		}
		graphics.resetColor();
	}
	
	private void renderLine(TesseractGuiGraphics graphics, int index, int start, int end, MutableObject<Runnable> cursorRenderer)
	{
		int cursorPos = note.editor().getCursorPos();
		
		int y = index * font.lineHeight;
		
		String text = note.text().substring(start, end);
		
		int lineWidth = graphics.drawString(text, 0, y, false);
		
		if(this.isFocused() && cursorPos >= start && cursorPos <= end)
		{
			int lineCursorPos = cursorPos - start;
			int lineCursorX = font.width(text.substring(0, lineCursorPos));
			cursorRenderer.setValue(() -> this.renderCursor(graphics, lineCursorX, y, cursorPos == end));
		}
		
		if(note.editor().isSelecting())
		{
			this.renderHighight(graphics, 0, y, text, lineWidth, start, end);
		}
	}
	
	private void renderCursor(TesseractGuiGraphics graphics, int x, int y, boolean endOfLine)
	{
		if((tick / 6) % 2 == 0)
		{
			if(endOfLine)
			{
				graphics.drawString("_", x, y, false);
			}
			else
			{
				graphics.fill(x, y - 1, x + 1, y + font.lineHeight);
			}
		}
	}
	
	private void renderHighight(TesseractGuiGraphics graphics, int x, int y, String text, int lineWidth, int start, int end)
	{
		int highlightStartPos = note.getHighlightStartPos();
		int highlightEndPos = note.getHighlightEndPos();
		
		// End point is before this line
		if(highlightEndPos < start ||
		   // Start point is after this line
		   highlightStartPos > end)
		{
			return;
		}
		
		int highlightStartX = 0;
		// Starting point is on this line
		if(highlightStartPos >= start && highlightStartPos <= end)
		{
			highlightStartX = font.width(text.substring(0, highlightStartPos - start));
		}
		// End point is on or after this line
		if(highlightEndPos >= start)
		{
			int highlightEndX = highlightEndPos <= end ? font.width(text.substring(0, highlightEndPos - start)) : lineWidth;
			
			graphics.setColor(0, 0, 1, 1);
			graphics.fill(RenderType.guiTextHighlight(), x + highlightStartX, y, x + highlightEndX, y + font.lineHeight);
			graphics.revertColor();
		}
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		return this.getRectangle().containsPoint((int) mouseX, (int) mouseY);
	}
	
	@Override
	public ScreenRectangle getRectangle()
	{
		return new ScreenRectangle(x, y, width, height);
	}
	
	@Override
	public void setFocused(boolean value)
	{
		focused = value;
	}
	
	@Override
	public boolean isFocused()
	{
		return focused;
	}
	
	@Override
	public NarrationPriority narrationPriority()
	{
		return NarrationPriority.NONE;
	}
	
	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput)
	{
	}
}
