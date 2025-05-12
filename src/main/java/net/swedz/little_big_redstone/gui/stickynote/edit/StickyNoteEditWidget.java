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
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;

import java.util.function.Supplier;

import static com.mojang.blaze3d.platform.InputConstants.*;

public final class StickyNoteEditWidget implements GuiEventListener, Renderable, NarratableEntry
{
	private final Font font;
	
	private final int x, y, width, height;
	
	private final StickyNoteEdit    note;
	private final Supplier<Integer> color;
	
	private int     tick;
	private boolean focused;
	
	public StickyNoteEditWidget(Font font, int x, int y, int width, int height,
								String text, Supplier<Integer> color)
	{
		this.font = font;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.note = new StickyNoteEdit(font, width, height, text);
		this.color = color;
	}
	
	public StickyNoteEditWidget(Font font, int x, int y, int width, int height,
								StickyNoteEditWidget previous, Supplier<Integer> color)
	{
		this.font = font;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.note = previous.note;
		this.color = color;
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
			note.jumpTo(localMouseX, localMouseY, Screen.hasShiftDown());
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
		this.renderCursor(graphics);
		this.renderHighlights(graphics);
		
		graphics.pose().popPose();
	}
	
	private void renderLines(TesseractGuiGraphics graphics)
	{
		graphics.setColor(color.get());
		var display = note.getDisplay();
		for(var line : display.lines())
		{
			graphics.drawString(line.text(), 0, line.y(), false);
		}
		graphics.resetColor();
	}
	
	private void renderCursor(TesseractGuiGraphics graphics)
	{
		if(this.isFocused() && (tick / 6) % 2 == 0)
		{
			graphics.setColor(color.get());
			var display = note.getDisplay();
			int x = display.cursorScreenX();
			int y = display.cursorScreenY();
			if(display.isCursorAtEndOfLine())
			{
				graphics.drawString("_", x, y, false);
			}
			else
			{
				graphics.fill(x, y - 1, x + 1, y + display.lineHeight());
			}
			graphics.resetColor();
		}
	}
	
	private void renderHighlights(TesseractGuiGraphics graphics)
	{
		for(var area : note.getDisplay().highlightedAreas())
		{
			this.renderHighlight(graphics, area);
		}
	}
	
	private void renderHighlight(TesseractGuiGraphics graphics, Bounds area)
	{
		graphics.setColor(0, 0, 1, 1);
		graphics.fill(RenderType.guiTextHighlight(), area.minX(), area.minY(), area.maxX(), area.maxY());
		graphics.revertColor();
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
