package net.swedz.little_big_redstone.gui.stickynote.edit;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringUtil;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.tesseract.neoforge.api.Bounds;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public final class StickyNoteEdit
{
	private final Font font;
	private final int  width, height;
	
	private final TextFieldHelper editor;
	
	private String  text;
	private boolean textModified = false;
	
	private Display display;
	
	public StickyNoteEdit(Font font, int width, int height, String text)
	{
		this.font = font;
		this.text = text;
		this.width = width;
		this.height = height;
		this.editor = new TextFieldHelper(
				this::text, this::setText,
				() -> TextFieldHelper.getClipboardContents(Minecraft.getInstance()),
				(copy) -> TextFieldHelper.setClipboardContents(Minecraft.getInstance(), copy),
				(input) -> font.wordWrapHeight(FormattedText.of(input), width) <= height
		);
	}
	
	public TextFieldHelper editor()
	{
		return editor;
	}
	
	public String text()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
		textModified = true;
		this.markDirty();
	}
	
	public boolean isTextModified()
	{
		return textModified;
	}
	
	public Display getDisplay()
	{
		if(display == null)
		{
			display = new Display();
		}
		return display;
	}
	
	private void markDirty()
	{
		display = null;
	}
	
	public void selectAll()
	{
		editor.selectAll();
		this.markDirty();
	}
	
	public void copy()
	{
		editor.copy();
	}
	
	public void paste()
	{
		editor.paste();
	}
	
	public void cut()
	{
		editor.cut();
	}
	
	public void insertNewLine()
	{
		editor.insertText("\n");
	}
	
	private TextFieldHelper.CursorStep getStep(boolean ctrl)
	{
		return ctrl ? TextFieldHelper.CursorStep.WORD : TextFieldHelper.CursorStep.CHARACTER;
	}
	
	public void backspace(boolean ctrl)
	{
		editor.removeFromCursor(-1, this.getStep(ctrl));
	}
	
	public void delete(boolean ctrl)
	{
		editor.removeFromCursor(1, this.getStep(ctrl));
	}
	
	public void moveLeft(boolean shift, boolean ctrl)
	{
		editor.moveBy(-1, shift, this.getStep(ctrl));
		this.markDirty();
	}
	
	public void moveRight(boolean shift, boolean ctrl)
	{
		editor.moveBy(1, shift, this.getStep(ctrl));
		this.markDirty();
	}
	
	private void moveVertically(int direction, boolean shift)
	{
		var display = this.getDisplay();
		int targetLineX = display.cursorX();
		int targetLineY = display.cursorY() + direction;
		if(targetLineY >= 0 && targetLineY < display.lines().length)
		{
			var line = display.lines()[targetLineY];
			targetLineX = line.findIndex(font, display.cursorScreenX());
		}
		int newCursorPos = display.toGlobalCursorPos(targetLineX, targetLineY);
		editor.setCursorPos(newCursorPos, shift);
		this.markDirty();
	}
	
	public void moveUp(boolean shift)
	{
		this.moveVertically(-1, shift);
	}
	
	public void moveDown(boolean shift)
	{
		this.moveVertically(1, shift);
	}
	
	public boolean type(CharacterEvent event)
	{
		var character = event.codepoint();
		if(StringUtil.isAllowedChatCharacter(character))
		{
			editor.insertText(Character.toString(character));
			return true;
		}
		return false;
	}
	
	private int cursorPosAt(int mouseX, int mouseY)
	{
		var display = this.getDisplay();
		int targetLineX = display.cursorX();
		int targetLineY = mouseY / display.lineHeight();
		if(targetLineY >= 0 && targetLineY < display.lines().length)
		{
			var line = display.lines()[targetLineY];
			targetLineX = line.findIndex(font, mouseX);
		}
		return display.toGlobalCursorPos(targetLineX, targetLineY);
	}
	
	public void jumpTo(int mouseX, int mouseY, boolean shift)
	{
		editor.setCursorPos(this.cursorPosAt(mouseX, mouseY), shift);
		this.markDirty();
	}
	
	public final class Display
	{
		private final String        fullText;
		private final DisplayLine[] lines;
		private final int           lineHeight;
		
		private final Component parsed;
		
		private final int cursorX, cursorY;
		private final int cursorScreenX, cursorScreenY;
		private final boolean cursorAtEndOfLine;
		
		private final Bounds[] highlightedAreas;
		
		private Display()
		{
			this.fullText = text;
			this.lineHeight = font.lineHeight;
			
			this.parsed = StickyNote.parse(fullText);
			
			int cursorIndex = editor.getCursorPos();
			int selectionIndex = editor.getSelectionPos();
			
			int highlightStartIndex = Math.min(cursorIndex, selectionIndex);
			int highlightEndIndex = Math.max(cursorIndex, selectionIndex);
			boolean hasHighlight = editor.isSelecting();
			
			var lineIndex = new MutableInt();
			List<DisplayLine> lines = Lists.newArrayList();
			font.getSplitter().splitLines(fullText, width, Style.EMPTY, false, (__, start, end) ->
			{
				int index = lineIndex.getAndIncrement();
				String lineText = fullText.substring(start, end);
				lines.add(new DisplayLine(index, lineText, index * lineHeight, font.width(lineText), start, end));
			});
			if(fullText.endsWith("\n"))
			{
				int index = lineIndex.getAndIncrement();
				int length = fullText.length();
				lines.add(new DisplayLine(index, "", index * lineHeight, 0, length, length));
			}
			
			int cursorX = 0;
			int cursorY = 0;
			int cursorScreenX = 0;
			int cursorScreenY = 0;
			boolean cursorAtEndOfLine = false;
			List<Bounds> highlightedAreas = Lists.newArrayList();
			for(int index = 0; index < lines.size(); index++)
			{
				var line = lines.get(index);
				int start = line.startIndex();
				int end = line.endIndex();
				
				if(cursorIndex >= start && cursorIndex <= end)
				{
					cursorX = cursorIndex - start;
					cursorY = index;
					cursorScreenX = font.width(line.text().substring(0, cursorX));
					cursorScreenY = line.y();
					cursorAtEndOfLine = cursorIndex == end;
				}
				
				if(hasHighlight)
				{
					// Highlight involves this line
					if(highlightStartIndex <= end && highlightEndIndex >= start)
					{
						int highlightStartX = 0;
						// Starting point is on this line
						if(highlightStartIndex >= start && highlightStartIndex <= end)
						{
							highlightStartX = font.width(line.text().substring(0, highlightStartIndex - start));
						}
						// End point is on or after this line
						if(highlightEndIndex >= start)
						{
							int highlightEndX = highlightEndIndex <= end ? font.width(line.text().substring(0, highlightEndIndex - start)) : line.width();
							if(highlightStartX != highlightEndX)
							{
								highlightedAreas.add(new Bounds(highlightStartX, line.y(), highlightEndX - highlightStartX, lineHeight));
							}
						}
					}
				}
			}
			
			this.lines = lines.toArray(DisplayLine[]::new);
			this.cursorX = cursorX;
			this.cursorY = cursorY;
			this.cursorScreenX = cursorScreenX;
			this.cursorScreenY = cursorScreenY;
			this.cursorAtEndOfLine = cursorAtEndOfLine;
			this.highlightedAreas = highlightedAreas.toArray(Bounds[]::new);
		}
		
		public String fullText()
		{
			return fullText;
		}
		
		public DisplayLine[] lines()
		{
			return lines;
		}
		
		public int lineHeight()
		{
			return lineHeight;
		}
		
		public Component parsed()
		{
			return parsed;
		}
		
		public int cursorX()
		{
			return cursorX;
		}
		
		public int cursorY()
		{
			return cursorY;
		}
		
		public int cursorScreenX()
		{
			return cursorScreenX;
		}
		
		public int cursorScreenY()
		{
			return cursorScreenY;
		}
		
		public boolean isCursorAtEndOfLine()
		{
			return cursorAtEndOfLine;
		}
		
		public Bounds[] highlightedAreas()
		{
			return highlightedAreas;
		}
		
		public int toGlobalCursorPos(int cursorX, int cursorY)
		{
			if(cursorY < 0)
			{
				return 0;
			}
			else if(cursorY < lines.length)
			{
				var line = lines[cursorY];
				return line.startIndex() + cursorX;
			}
			else
			{
				return fullText.length();
			}
		}
	}
	
	public record DisplayLine(int index, String text, int y, int width, int startIndex, int endIndex)
	{
		public int length()
		{
			return text.length();
		}
		
		public int findIndex(Font font, int desiredWidth)
		{
			if(desiredWidth == 0)
			{
				return 0;
			}
			if(width <= desiredWidth)
			{
				return text.length();
			}
			int lastWidth = 0;
			int traversedWidth = 0;
			for(int i = 0; i < text.length(); i++)
			{
				traversedWidth += font.width(String.valueOf(text.charAt(i)));
				if(desiredWidth == traversedWidth)
				{
					return i + 1;
				}
				else if(desiredWidth < traversedWidth && desiredWidth > lastWidth)
				{
					int distToWidth = Math.abs(traversedWidth - desiredWidth);
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
				lastWidth = traversedWidth;
			}
			return 0;
		}
	}
}
