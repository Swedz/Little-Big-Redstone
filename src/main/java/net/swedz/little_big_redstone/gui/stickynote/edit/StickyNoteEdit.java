package net.swedz.little_big_redstone.gui.stickynote.edit;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringUtil;
import net.swedz.little_big_redstone.LBR;
import net.swedz.tesseract.neoforge.api.tuple.Pair;

import java.util.List;

public final class StickyNoteEdit
{
	private final Font font;
	private final int  width, height;
	
	private final TextFieldHelper editor;
	
	private String text;
	private boolean dirty;
	
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
				(input) -> font.wordWrapHeight(input, width) <= height
		);
	}
	
	public TextFieldHelper editor()
	{
		return editor;
	}
	
	public int getHighlightStartPos()
	{
		return Math.min(editor.getCursorPos(), editor.getSelectionPos());
	}
	
	public int getHighlightEndPos()
	{
		return Math.max(editor.getCursorPos(), editor.getSelectionPos());
	}
	
	public String text()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
		this.markDirty();
	}
	
	public void markDirty()
	{
		dirty = true;
	}
	
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void selectAll()
	{
		editor.selectAll();
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
	}
	
	public void moveRight(boolean shift, boolean ctrl)
	{
		editor.moveBy(1, shift, this.getStep(ctrl));
	}
	
	private void moveVertically(int direction, boolean shift)
	{
		editor.setCursorPos(this.findNewCursorPosForLineDifference(direction), shift);
	}
	
	public void moveUp(boolean shift)
	{
		this.moveVertically(-1, shift);
	}
	
	public void moveDown(boolean shift)
	{
		this.moveVertically(1, shift);
	}
	
	public boolean type(char character)
	{
		if(StringUtil.isAllowedChatCharacter(character))
		{
			editor.insertText(Character.toString(character));
			return true;
		}
		return false;
	}
	
	public void jumpTo(int mouseX, int mouseY)
	{
		int cursorPos = this.findNewCursorPosForMouseClick(mouseX, mouseY);
		if(cursorPos != -1)
		{
			editor.setCursorPos(cursorPos, Screen.hasShiftDown());
		}
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
		font.getSplitter().splitLines(text, width, Style.EMPTY, false, (__, start, end) -> lines.add(new Pair<>(start, end)));
		
		int index = 0;
		for(var line : lines)
		{
			int start = line.a();
			int end = line.b();
			
			String lineText = text.substring(start, end);
			int lineWidth = font.width(lineText);
			
			if(cursorPos >= start && cursorPos <= end)
			{
				if(index == 0 && lineChange < 0)
				{
					return 0;
				}
				if(index == lines.size() - 1 && lineChange > 0)
				{
					return text.length();
				}
				var targetLine = lines.get(index + lineChange);
				int lineCursorX = font.width(lineText.substring(0, cursorPos - start));
				String targetText = text.substring(targetLine.a(), targetLine.b());
				int targetTextIndex = this.findClosestCharCursorPos(targetText, lineCursorX);
				return targetLine.a() + targetTextIndex;
			}
			
			index++;
		}
		
		LBR.LOGGER.warn("Failed to find desirable cursor pos");
		return cursorPos;
	}
	
	private int findNewCursorPosForMouseClick(int mouseX, int mouseY)
	{
		List<Pair<Integer, Integer>> lines = Lists.newArrayList();
		font.getSplitter().splitLines(text, width, Style.EMPTY, false, (__, start, end) -> lines.add(new Pair<>(start, end)));
		
		int targetLineIndex = mouseY / font.lineHeight;
		if(targetLineIndex < 0)
		{
			return 0;
		}
		if(targetLineIndex >= lines.size())
		{
			return text.length();
		}
		if(targetLineIndex >= 0 && targetLineIndex < lines.size())
		{
			var line = lines.get(targetLineIndex);
			String lineText = text.substring(line.a(), line.b());
			int lineCursorIndex = this.findClosestCharCursorPos(lineText, mouseX);
			return line.a() + lineCursorIndex;
		}
		
		return -1;
	}
}
