package net.swedz.little_big_redstone.gui.noteboard;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRAttachments;
import net.swedz.little_big_redstone.LBRClient;
import net.swedz.little_big_redstone.client.StickyNoteViewRenderer;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteView;
import net.swedz.little_big_redstone.gui.noteboard.contents.NoteBoardContents;
import net.swedz.little_big_redstone.gui.noteboard.contents.NoteBoardStickyNote;
import net.swedz.little_big_redstone.gui.stickynote.reference.NoteBoardStickyNoteReference;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.network.packet.MoveNoteBoardStickyNotePacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeNoteBoardStickyNotePacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.tesseract.neoforge.proxy.Proxies;

public final class NoteBoardScreen extends AbstractContainerScreen<NoteBoardMenu>
{
	private static final ResourceLocation BACKGROUND   = LBR.id("textures/gui/container/note_board/inventory_background.png");
	private static final ResourceLocation OVERLAY_NOTE = LBR.id("textures/gui/container/note_board/overlay_note.png");
	
	private NoteBoardContents contents;
	
	private int carriedNoteSize = NoteBoardStickyNote.DEFAULT_NOTE_SIZE;
	
	private boolean holdingHudNote = false;
	
	public NoteBoardScreen(NoteBoardMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		contents = Minecraft.getInstance().player.getData(LBRAttachments.NOTE_BOARD);
		
		imageHeight = 90;
	}
	
	public NoteBoardContents contents()
	{
		return contents;
	}
	
	public void setContents(NoteBoardContents contents)
	{
		this.contents = contents;
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		topPos = height - imageHeight - 24;
	}
	
	private boolean isOutsideInventory(double mouseX, double mouseY)
	{
		return this.hasClickedOutside(mouseX, mouseY, leftPos, topPos, 0);
	}
	
	private void renderNote(TesseractGuiGraphics graphics, int x, int y, int size, ItemStack stack, int mouseX, int mouseY)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(x, y, 0);
		float scale = size / ((float) NoteBoardStickyNote.FULL_NOTE_SIZE);
		graphics.pose().scale(scale, scale, 1);
		
		var view = new StickyNoteView(stack);
		StickyNoteViewRenderer.renderBackground(graphics, view, true);
		StickyNoteViewRenderer.renderText(graphics, view, true);
		
		graphics.pose().popPose();
	}
	
	private boolean isHoveringHudNote(int mouseX, int mouseY)
	{
		int size = LBRClient.config().stickyNoteInWorldView().size();
		float scaledX = (float) LBRClient.config().stickyNoteInWorldView().x();
		float scaledY = (float) LBRClient.config().stickyNoteInWorldView().y();
		int x = NoteBoardStickyNote.toPixelCoord(scaledX, Minecraft.getInstance().getWindow().getGuiScaledWidth(), size);
		int y = NoteBoardStickyNote.toPixelCoord(scaledY, Minecraft.getInstance().getWindow().getGuiScaledHeight(), size);
		return mouseX >= x &&
			   mouseX <= x + size &&
			   mouseY >= y &&
			   mouseY <= y + size;
	}
	
	private void renderHudNote(TesseractGuiGraphics graphics, int mouseX, int mouseY)
	{
		int size = holdingHudNote ? carriedNoteSize : LBRClient.config().stickyNoteInWorldView().size();
		
		graphics.pose().pushPose();
		
		float scaledX = holdingHudNote ?
				Mth.clamp(NoteBoardStickyNote.toPercentageCoord(mouseX - (size / 2), width, size), 0, 1) :
				(float) LBRClient.config().stickyNoteInWorldView().x();
		float scaledY = holdingHudNote ?
				Mth.clamp(NoteBoardStickyNote.toPercentageCoord(mouseY - (size / 2), height, size), 0, 1) :
				(float) LBRClient.config().stickyNoteInWorldView().y();
		int x = NoteBoardStickyNote.toPixelCoord(scaledX, width, size);
		int y = NoteBoardStickyNote.toPixelCoord(scaledY, height, size);
		graphics.pose().translate(x, y, 0);
		
		float scale = size / ((float) NoteBoardStickyNote.FULL_NOTE_SIZE);
		graphics.pose().scale(scale, scale, 1);
		
		graphics.setColor(1, 1, 1, 0.5f);
		graphics.setTexture(OVERLAY_NOTE);
		graphics.blit(0, 0, 0, 0, 180, 180, 180, 180);
		graphics.resetColor();
		
		graphics.pose().popPose();
	}
	
	private void renderNotes(TesseractGuiGraphics graphics, int mouseX, int mouseY)
	{
		for(var note : contents)
		{
			int size = note.size();
			var stack = note.stack();
			this.renderNote(graphics, note.x(width), note.y(height), size, stack, mouseX, mouseY);
		}
		
		this.renderHudNote(graphics, mouseX, mouseY);
	}
	
	private boolean placeNote(int x, int y, int button)
	{
		if(button == InputConstants.MOUSE_BUTTON_LEFT)
		{
			var carried = menu.getCarried();
			if(holdingHudNote ||
			   StickyNoteItem.hasRelevantComponents(carried))
			{
				int placeXRaw = x - (carriedNoteSize / 2);
				int placeYRaw = y - (carriedNoteSize / 2);
				float placeX = Mth.clamp(NoteBoardStickyNote.toPercentageCoord(placeXRaw, width, carriedNoteSize), 0, 1);
				float placeY = Mth.clamp(NoteBoardStickyNote.toPercentageCoord(placeYRaw, height, carriedNoteSize), 0, 1);
				if(holdingHudNote)
				{
					LBRClient.config().stickyNoteInWorldView().x(placeX);
					LBRClient.config().stickyNoteInWorldView().y(placeY);
					LBRClient.config().stickyNoteInWorldView().size(carriedNoteSize);
					holdingHudNote = false;
				}
				else
				{
					var stack = carried.copyWithCount(1);
					carried.shrink(1);
					contents = contents.add(new NoteBoardStickyNote(placeX, placeY, carriedNoteSize, stack));
					PlaceTakeNoteBoardStickyNotePacket.place(menu.containerId, placeX, placeY, carriedNoteSize).sendToServer();
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean takeNote(int x, int y, int button)
	{
		if(button == InputConstants.MOUSE_BUTTON_LEFT)
		{
			if(this.isHoveringHudNote(x, y))
			{
				carriedNoteSize = LBRClient.config().stickyNoteInWorldView().size();
				holdingHudNote = true;
				return true;
			}
			int clickedNoteIndex = contents.findAt(x, y, width, height);
			if(clickedNoteIndex != -1)
			{
				var note = contents.get(clickedNoteIndex);
				var noteStack = note.stack();
				var carried = menu.getCarried();
				if(carried.isEmpty() ||
				   ItemStack.isSameItemSameComponents(carried, noteStack))
				{
					carriedNoteSize = note.size();
					if(carried.isEmpty())
					{
						menu.setCarried(noteStack.copy());
					}
					else
					{
						carried.grow(1);
					}
					contents = contents.remove(clickedNoteIndex);
					PlaceTakeNoteBoardStickyNotePacket.take(menu.containerId, clickedNoteIndex).sendToServer();
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean editNote(int x, int y, int button)
	{
		if(button == InputConstants.MOUSE_BUTTON_RIGHT &&
		   menu.getCarried().isEmpty())
		{
			int clickedNoteIndex = contents.findAt(x, y, width, height);
			if(clickedNoteIndex != -1)
			{
				var note = contents.get(clickedNoteIndex);
				Proxies.get(LBRProxy.class).openStickyNote(NoteBoardStickyNoteReference.from(clickedNoteIndex, note), note.isEditable());
				return true;
			}
		}
		return false;
	}
	
	private boolean mouseClickedOutside(int x, int y, int button)
	{
		return this.placeNote(x, y, button) ||
			   this.takeNote(x, y, button) ||
			   this.editNote(x, y, button);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(this.isOutsideInventory(mouseX, mouseY) &&
		   this.mouseClickedOutside((int) mouseX, (int) mouseY, button))
		{
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(this.isOutsideInventory(mouseX, mouseY) &&
		   StickyNoteItem.hasRelevantComponents(menu.getCarried()))
		{
			return false;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type)
	{
		carriedNoteSize = NoteBoardStickyNote.DEFAULT_NOTE_SIZE;
		
		super.slotClicked(slot, slotId, mouseButton, type);
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	public void renderFloatingItem(GuiGraphics vanilla, ItemStack stack, int x, int y, String text)
	{
		int mouseX = x + 8;
		int mouseY = y + 8;
		
		// mouseX and mouseY are relative to the leftPos and topPos position, so we need to add here
		if(this.shouldRenderFloatingNote(mouseX + leftPos, mouseY + topPos, stack))
		{
			// This is handled in renderBg so that it is underneath the slots
			return;
		}
		
		super.renderFloatingItem(vanilla, stack, x, y, text);
	}
	
	private boolean shouldRenderFloatingNote(int mouseX, int mouseY, ItemStack stack)
	{
		return this.isOutsideInventory(mouseX, mouseY) &&
			   StickyNoteItem.hasRelevantComponents(stack);
	}
	
	private void renderFloatingNote(TesseractGuiGraphics graphics, int mouseX, int mouseY, ItemStack stack)
	{
		if(this.shouldRenderFloatingNote(mouseX, mouseY, stack))
		{
			int atXRaw = mouseX - (carriedNoteSize / 2);
			int atYRaw = mouseY - (carriedNoteSize / 2);
			float atXScaled = Mth.clamp(NoteBoardStickyNote.toPercentageCoord(atXRaw, width, carriedNoteSize), 0, 1);
			float atYScaled = Mth.clamp(NoteBoardStickyNote.toPercentageCoord(atYRaw, height, carriedNoteSize), 0, 1);
			int atX = NoteBoardStickyNote.toPixelCoord(atXScaled, width, carriedNoteSize);
			int atY = NoteBoardStickyNote.toPixelCoord(atYScaled, height, carriedNoteSize);
			
			this.renderNote(graphics, atX, atY, carriedNoteSize, stack, -1, -1);
		}
	}
	
	private int resize(int originalSize, double scrollY)
	{
		int step = scrollY > 0 ? NoteBoardStickyNote.STEP_NOTE_SIZE : (scrollY < 0 ? -NoteBoardStickyNote.STEP_NOTE_SIZE : 0);
		return Mth.clamp(originalSize + step, NoteBoardStickyNote.MIN_NOTE_SIZE, NoteBoardStickyNote.MAX_NOTE_SIZE);
	}
	
	private boolean scrollHudNote(int mouseX, int mouseY, double scrollY)
	{
		if(this.isHoveringHudNote(mouseX, mouseY))
		{
			int noteSize = LBRClient.config().stickyNoteInWorldView().size();
			int size = this.resize(noteSize, scrollY);
			int sizeDifference = noteSize - size;
			
			int toXRaw = LBRClient.config().stickyNoteInWorldView().x(width, noteSize) + (sizeDifference / 2);
			int toYRaw = LBRClient.config().stickyNoteInWorldView().y(height, noteSize) + (sizeDifference / 2);
			float toX = Mth.clamp(NoteBoardStickyNote.toPercentageCoord(toXRaw, width, size), 0, 1);
			float toY = Mth.clamp(NoteBoardStickyNote.toPercentageCoord(toYRaw, height, size), 0, 1);
			
			LBRClient.config().stickyNoteInWorldView().x(toX);
			LBRClient.config().stickyNoteInWorldView().y(toY);
			LBRClient.config().stickyNoteInWorldView().size(size);
			return true;
		}
		return false;
	}
	
	private boolean scrollHoveredNote(int mouseX, int mouseY, double scrollY)
	{
		int hoveredNoteIndex = contents.findAt(mouseX, mouseY, width, height);
		if(hoveredNoteIndex >= 0)
		{
			var hoveredNote = contents.get(hoveredNoteIndex);
			int noteSize = hoveredNote.size();
			
			int size = this.resize(noteSize, scrollY);
			int sizeDifference = noteSize - size;
			
			int toXRaw = hoveredNote.x(width) + (sizeDifference / 2);
			int toYRaw = hoveredNote.y(height) + (sizeDifference / 2);
			float toX = Mth.clamp(NoteBoardStickyNote.toPercentageCoord(toXRaw, width, size), 0, 1);
			float toY = Mth.clamp(NoteBoardStickyNote.toPercentageCoord(toYRaw, height, size), 0, 1);
			
			contents = contents.update(hoveredNoteIndex, hoveredNote.moveTo(toX, toY, size));
			new MoveNoteBoardStickyNotePacket(menu.containerId, hoveredNoteIndex, toX, toY, size).sendToServer();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY)
	{
		if(this.isOutsideInventory(mx, my))
		{
			var carried = menu.getCarried();
			if(holdingHudNote ||
			   StickyNoteItem.hasRelevantComponents(carried))
			{
				carriedNoteSize = this.resize(carriedNoteSize, scrollY);
				return true;
			}
			else if(carried.isEmpty())
			{
				int mouseX = (int) Math.round(mx);
				int mouseY = (int) Math.round(my);
				return this.scrollHudNote(mouseX, mouseY, scrollY) ||
					   this.scrollHoveredNote(mouseX, mouseY, scrollY);
			}
		}
		return super.mouseScrolled(mx, my, scrollX, scrollY);
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		// Do not render the inventory label
	}
	
	@Override
	protected void renderBg(GuiGraphics vanilla, float partialTick, int mouseX, int mouseY)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		this.renderNotes(graphics, mouseX, mouseY);
		
		this.renderFloatingNote(graphics, mouseX, mouseY, menu.getCarried());
		
		vanilla.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
