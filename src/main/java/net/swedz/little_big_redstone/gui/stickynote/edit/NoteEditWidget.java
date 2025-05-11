package net.swedz.little_big_redstone.gui.stickynote.edit;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringUtil;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.tesseract.neoforge.api.tuple.Pair;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;

import static com.mojang.blaze3d.platform.InputConstants.*;

public final class NoteEditWidget implements GuiEventListener, Renderable, NarratableEntry
{
	private final Font font;
	
	private final int x, y, width, height;
	
	private final StickyNoteEdit note;
	
	private final TextFieldHelper editor;
	
	private int     tick;
	private boolean focused;
	
	public NoteEditWidget(Font font, int x, int y, int width, int height,
						  String text)
	{
		this.font = font;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		note = new StickyNoteEdit(text);
		editor = new TextFieldHelper(
				note::text, note::setText,
				NoteEditWidget::getClipboard, NoteEditWidget::setClipboard,
				(input) -> font.wordWrapHeight(input, width) <= height
		);
	}
	
	public NoteEditWidget(Font font, int x, int y, int width, int height,
						  NoteEditWidget previous)
	{
		this.font = font;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		note = previous.note;
		editor = previous.editor;
	}
	
	private static String getClipboard()
	{
		return TextFieldHelper.getClipboardContents(Minecraft.getInstance());
	}
	
	private static void setClipboard(String text)
	{
		TextFieldHelper.setClipboardContents(Minecraft.getInstance(), text);
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
			this.jumpTo(localMouseX, localMouseY);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if(Screen.isSelectAll(keyCode))
		{
			editor.selectAll();
			return true;
		}
		else if(Screen.isCopy(keyCode))
		{
			editor.copy();
			return true;
		}
		else if(Screen.isPaste(keyCode))
		{
			editor.paste();
			return true;
		}
		else if(Screen.isCut(keyCode))
		{
			editor.cut();
			return true;
		}
		else
		{
			var step = Screen.hasControlDown() ? TextFieldHelper.CursorStep.WORD : TextFieldHelper.CursorStep.CHARACTER;
			return switch (keyCode)
			{
				case KEY_RETURN, KEY_NUMPADENTER ->
				{
					editor.insertText("\n");
					yield true;
				}
				case KEY_BACKSPACE ->
				{
					editor.removeFromCursor(-1, step);
					yield true;
				}
				case KEY_DELETE ->
				{
					editor.removeFromCursor(1, step);
					yield true;
				}
				case KEY_RIGHT ->
				{
					editor.moveBy(1, Screen.hasShiftDown(), step);
					yield true;
				}
				case KEY_LEFT ->
				{
					editor.moveBy(-1, Screen.hasShiftDown(), step);
					yield true;
				}
				case KEY_UP ->
				{
					this.moveLine(-1);
					yield true;
				}
				case KEY_DOWN ->
				{
					this.moveLine(1);
					yield true;
				}
				default -> false;
			};
		}
	}
	
	@Override
	public boolean charTyped(char codePoint, int modifiers)
	{
		if(StringUtil.isAllowedChatCharacter(codePoint))
		{
			editor.insertText(Character.toString(codePoint));
			return true;
		}
		return false;
	}
	
	private int findClosestCharCursorPos(String text, int desiredWidth)
	{
		if(desiredWidth == 0)
		{
			return 0;
		}
		
		int lineWidth = font.width(text);
		
		if(lineWidth <= desiredWidth)
		{
			return text.length();
		}
		
		int lastWidth = 0;
		String traversed = "";
		for(int i = 0; i < text.length(); i++)
		{
			traversed += text.charAt(i);
			int width = font.width(traversed);
			if(desiredWidth == width)
			{
				return i + 1;
			}
			else if(desiredWidth < width && desiredWidth > lastWidth)
			{
				int distToWidth = Math.abs(width - desiredWidth);
				int distToLastWidth = Math.abs(lastWidth - desiredWidth);
				if(distToWidth <= distToLastWidth)
				{
					return i + 1;
				}
				else
				{
					return i;
				}
			}
			lastWidth = width;
		}
		
		return 0;
	}
	
	private int findNewCursorPosForLineDifference(int lineChange)
	{
		int cursorPos = editor.getCursorPos();
		
		List<Pair<Integer, Integer>> lines = Lists.newArrayList();
		font.getSplitter().splitLines(note.text(), width, Style.EMPTY, false, (__, start, end) -> lines.add(new Pair<>(start, end)));
		
		int index = 0;
		for(var line : lines)
		{
			int start = line.a();
			int end = line.b();
			
			String text = note.text().substring(start, end);
			int lineWidth = font.width(text);
			
			if(cursorPos >= start && cursorPos <= end)
			{
				if(index == 0 && lineChange < 0)
				{
					return 0;
				}
				if(index == lines.size() - 1 && lineChange > 0)
				{
					return note.text().length();
				}
				var targetLine = lines.get(index + lineChange);
				int lineCursorX = font.width(text.substring(0, cursorPos - start));
				String targetText = note.text().substring(targetLine.a(), targetLine.b());
				int targetTextIndex = this.findClosestCharCursorPos(targetText, lineCursorX);
				return targetLine.a() + targetTextIndex;
			}
			
			index++;
		}
		
		LBR.LOGGER.warn("Failed to find desirable cursor pos");
		return cursorPos;
	}
	
	private void moveLine(int lineChange)
	{
		editor.setCursorPos(this.findNewCursorPosForLineDifference(lineChange), Screen.hasShiftDown());
	}
	
	private int findNewCursorPosForMouseClick(int mouseX, int mouseY)
	{
		List<Pair<Integer, Integer>> lines = Lists.newArrayList();
		font.getSplitter().splitLines(note.text(), width, Style.EMPTY, false, (__, start, end) -> lines.add(new Pair<>(start, end)));
		
		int targetLineIndex = mouseY / font.lineHeight;
		if(targetLineIndex < 0)
		{
			return 0;
		}
		if(targetLineIndex >= lines.size())
		{
			return note.text().length();
		}
		if(targetLineIndex >= 0 && targetLineIndex < lines.size())
		{
			var line = lines.get(targetLineIndex);
			String text = note.text().substring(line.a(), line.b());
			int lineCursorIndex = this.findClosestCharCursorPos(text, mouseX);
			return line.a() + lineCursorIndex;
		}
		
		return -1;
	}
	
	private void jumpTo(int mouseX, int mouseY)
	{
		int cursorPos = this.findNewCursorPosForMouseClick(mouseX, mouseY);
		if(cursorPos != -1)
		{
			editor.setCursorPos(cursorPos, Screen.hasShiftDown());
		}
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
		int cursorPos = editor.getCursorPos();
		
		int y = index * font.lineHeight;
		
		String text = note.text().substring(start, end);
		
		int lineWidth = graphics.drawString(text, 0, y, false);
		
		if(this.isFocused() && cursorPos >= start && cursorPos <= end)
		{
			int lineCursorPos = cursorPos - start;
			int lineCursorX = font.width(text.substring(0, lineCursorPos));
			cursorRenderer.setValue(() -> this.renderCursor(graphics, lineCursorX, y, cursorPos == end));
		}
		
		if(editor.isSelecting())
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
				graphics.drawString("_", x, y);
			}
			else
			{
				graphics.fill(x, y - 1, x + 1, y + font.lineHeight);
			}
		}
	}
	
	private void renderHighight(TesseractGuiGraphics graphics, int x, int y, String text, int lineWidth, int start, int end)
	{
		int cursorPos = editor.getCursorPos();
		int selectionPos = editor.getSelectionPos();
		
		int highlightStartPos = Math.min(cursorPos, selectionPos);
		int highlightEndPos = Math.max(cursorPos, selectionPos);
		
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
