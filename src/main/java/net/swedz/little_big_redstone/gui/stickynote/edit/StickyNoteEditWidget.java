package net.swedz.little_big_redstone.gui.stickynote.edit;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.swedz.tesseract.neoforge.api.Bounds;

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
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
	{
		var mouseX = event.x();
		var mouseY = event.y();
		
		if(!this.isMouseOver(mouseX, mouseY))
		{
			return false;
		}
		
		if(event.button() == InputConstants.MOUSE_BUTTON_LEFT)
		{
			int localMouseX = (int) (mouseX - x);
			int localMouseY = (int) (mouseY - y);
			note.jumpTo(localMouseX, localMouseY, event.hasShiftDown());
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy)
	{
		var mouseX = event.x();
		var mouseY = event.y();
		
		if(!this.isMouseOver(mouseX, mouseY))
		{
			return false;
		}
		
		if(event.button() == InputConstants.MOUSE_BUTTON_LEFT)
		{
			int localMouseX = (int) (mouseX - x);
			int localMouseY = (int) (mouseY - y);
			note.jumpTo(localMouseX, localMouseY, true);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean keyPressed(KeyEvent event)
	{
		if(event.isSelectAll())
		{
			note.selectAll();
			return true;
		}
		else if(event.isCopy())
		{
			note.copy();
			return true;
		}
		else if(event.isPaste())
		{
			note.paste();
			return true;
		}
		else if(event.isCut())
		{
			note.cut();
			return true;
		}
		else
		{
			boolean ctrl = event.hasControlDown();
			boolean shift = event.hasShiftDown();
			return switch (event.key())
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
	public boolean charTyped(CharacterEvent event)
	{
		return note.type(event);
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
	{
		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		
		this.renderLines(graphics);
		this.renderHighlights(graphics);
		this.renderCursor(graphics);
		
		graphics.pose().popMatrix();
	}
	
	private void renderLines(GuiGraphicsExtractor graphics)
	{
		int color = this.color.get();
		var display = note.getDisplay();
		for(var line : display.lines())
		{
			graphics.text(Minecraft.getInstance().font, line.text(), 0, line.y(), color, false);
		}
	}
	
	private void renderCursor(GuiGraphicsExtractor graphics)
	{
		if(this.isFocused() && (tick / 6) % 2 == 0)
		{
			int color = this.color.get();
			var display = note.getDisplay();
			int x = display.cursorScreenX();
			int y = display.cursorScreenY();
			if(display.isCursorAtEndOfLine())
			{
				graphics.text(Minecraft.getInstance().font, "_", x, y, color, false);
			}
			else
			{
				graphics.fill(x - 1, y - 1, x, y + display.lineHeight(), color);
			}
		}
	}
	
	private void renderHighlights(GuiGraphicsExtractor graphics)
	{
		for(var area : note.getDisplay().highlightedAreas())
		{
			this.renderHighlight(graphics, area);
		}
	}
	
	private void renderHighlight(GuiGraphicsExtractor graphics, Bounds area)
	{
		graphics.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, area.minX(), area.minY(), area.maxX(), area.maxY(), 0xFF0000FF);
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
