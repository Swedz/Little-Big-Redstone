package net.swedz.little_big_redstone.gui.microchip.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.microchip.MicrochipViewPosition;
import net.swedz.little_big_redstone.gui.microchip.logic.DyeComponentResult;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.gui.microchip.panel.MicrochipRenderBoardPanel;
import net.swedz.little_big_redstone.gui.stickynote.reference.MicrochipStickyNoteReference;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicSelectedPort;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.little_big_redstone.network.packet.DyeMicrochipObjectPacket;
import net.swedz.little_big_redstone.network.packet.OpenLogicConfigPacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipObjectPacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipWirePacket;
import net.swedz.little_big_redstone.network.packet.QuickGrabMicrochipWireItemPacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.api.Bounds;
import net.swedz.tesseract.neoforge.gui.ExtendedClientTooltipPositioner;
import net.swedz.tesseract.neoforge.helper.gui.SpriteGraphics;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class MicrochipWidget implements GuiEventListener, Renderable, NarratableEntry
{
	final int x, y, width, height;
	
	private boolean focused;
	
	private final MicrochipScreen screen;
	private final Microchip       microchip;
	
	private final MicrochipRenderBoardPanel panel;
	
	private MicrochipWidgetContext context = new MicrochipWidgetContext(this, 0, 0);
	
	private final MicrochipViewPosition viewPosition;
	
	private LogicSelectedPort selectedPort;
	
	private boolean allowDragging = true;
	
	public MicrochipWidget(int x, int y, MicrochipScreen screen, MicrochipViewPosition viewPosition)
	{
		this.screen = screen;
		this.microchip = screen.getMenu().microchip();
		
		panel = new MicrochipRenderBoardPanel(this.color(), microchip, () -> context);
		
		var bounds = microchip.size().bounds();
		this.x = x + microchip.size().scale(bounds.minX());
		this.y = y + microchip.size().scale(bounds.minY());
		this.width = bounds.width();
		this.height = bounds.height();
		
		this.viewPosition = viewPosition;
		this.viewPosition.init(microchip.size(), this.x, this.y);
	}
	
	public int x()
	{
		return x;
	}
	
	public int y()
	{
		return y;
	}
	
	public int width()
	{
		return width;
	}
	
	public int height()
	{
		return height;
	}
	
	public MicrochipMenu menu()
	{
		return screen.getMenu();
	}
	
	public DyeColor color()
	{
		return this.menu().color();
	}
	
	public Microchip microchip()
	{
		return microchip;
	}
	
	public MicrochipRenderBoardPanel panel()
	{
		return panel;
	}
	
	public MicrochipWidgetContext context()
	{
		return context;
	}
	
	public MicrochipViewPosition viewPosition()
	{
		return viewPosition;
	}
	
	public boolean hasSelectedPort()
	{
		return selectedPort != null;
	}
	
	public LogicSelectedPort getSelectedPort()
	{
		return selectedPort;
	}
	
	public void handleUpdate(boolean rerouteWires)
	{
		if(this.hasSelectedPort())
		{
			var component = microchip.components().get(selectedPort.slot());
			if(component == null || selectedPort.index() >= component.component().outputs())
			{
				selectedPort = null;
				LBR.LOGGER.info("Cleared selected port because it doesn't exist anymore");
			}
		}
		
		if(rerouteWires)
		{
			panel.wires().rebuildPaths();
		}
	}
	
	private boolean dyeComponent(int x, int y, MouseButtonEvent event)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var entry = context.object();
		
		if(event.button() == InputConstants.MOUSE_BUTTON_RIGHT &&
		   context.shouldDyeObject())
		{
			int slot = entry.slot();
			var result = DyeComponentResult.test(carried, entry.color());
			if(result.success())
			{
				if(entry.setColor(result.color()))
				{
					microchip.markDirty(false);
					if(result.consume())
					{
						carried.consume(1, screen.getMinecraft().player);
					}
					result.playSound(screen.getMinecraft().player);
					new DyeMicrochipObjectPacket(menu.containerId, entry.containerType(), slot).sendToServer();
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean pickupNote(int x, int y, MouseButtonEvent event)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var note = context.note();
		
		if(event.button() == InputConstants.MOUSE_BUTTON_LEFT &&
		   context.shouldInteractNote() &&
		   carried.isEmpty())
		{
			microchip.stickyNotes().remove(note);
			microchip.markDirty(false);
			var stack = note.toStack();
			boolean shift = event.hasShiftDown();
			var destination = menu.getDestinationInventoryItemHandler(Minecraft.getInstance().player);
			if(!shift || ResourceHandlerUtil.insertStacking(destination, ItemResource.of(stack), stack.getCount(), null) <= 0)
			{
				menu.setCarried(stack);
			}
			new PlaceTakeMicrochipObjectPacket(menu.containerId, x, y, false, true, shift).sendToServer();
			return true;
		}
		
		return false;
	}
	
	private boolean pickupWire(Wire wire)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		
		if(wire != null && microchip.wires().remove(wire))
		{
			microchip.markDirty(false);
			if(carried.isEmpty())
			{
				menu.setCarried(LBRItems.REDSTONE_BIT.asItem().getDefaultInstance());
			}
			else if(!screen.getMinecraft().player.hasInfiniteMaterials())
			{
				carried.grow(1);
			}
			selectedPort = new LogicSelectedPort(microchip.components().get(wire.output().slot()), wire.output().index(), false);
			new PlaceTakeMicrochipWirePacket(menu.containerId, wire, false).sendToServer();
			return true;
		}
		return false;
	}
	
	private boolean pickupWire(int x, int y, MouseButtonEvent event)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		
		if(event.button() == InputConstants.MOUSE_BUTTON_LEFT &&
		   MicrochipWidgetContext.canInteractWire(carried) &&
		   !this.hasSelectedPort())
		{
			if(carried.isEmpty() && context.shouldInteractWire())
			{
				var wire = context.wire();
				return this.pickupWire(wire);
			}
			
			if(context.shouldInteractPort())
			{
				if(context.isPortOutput())
				{
					if(carried.isEmpty())
					{
						var extracted = ResourceHandlerUtil.extractFirst(menu.getLogicArrayItemHandler(), (stack) -> stack.is(LBRItems.REDSTONE_BIT.asItem()), 1, null);
						if(!extracted.isEmpty())
						{
							menu.setCarried(extracted.resource().toStack(extracted.amount()));
							selectedPort = context.port();
							new QuickGrabMicrochipWireItemPacket(menu.containerId).sendToServer();
							return true;
						}
					}
					else
					{
						selectedPort = context.port();
						return true;
					}
				}
				if(context.isPortInput() &&
				   (carried.isEmpty() || carried.getCount() < carried.getMaxStackSize()))
				{
					var wire = microchip.wires().getByInputSlot(context.port());
					return this.pickupWire(wire);
				}
			}
			
			if(!carried.isEmpty() && context.shouldInteractWire())
			{
				var wire = context.wire();
				return this.pickupWire(wire);
			}
		}
		
		return false;
	}
	
	private boolean pickupLogic(int x, int y, MouseButtonEvent event)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var logic = context.logic();
		
		if(event.button() == InputConstants.MOUSE_BUTTON_LEFT &&
		   context.shouldInteractLogic() &&
		   carried.isEmpty())
		{
			var wiresPopped = microchip.components().remove(logic);
			microchip.markDirty(true);
			var stack = logic.toStack();
			boolean shift = event.hasShiftDown();
			var destination = menu.getDestinationInventoryItemHandler(Minecraft.getInstance().player);
			if(!shift || ResourceHandlerUtil.insertStacking(destination, ItemResource.of(stack), stack.getCount(), null) <= 0)
			{
				menu.setCarried(stack);
				menu.setCarriedWires(logic.slot(), wiresPopped);
			}
			new PlaceTakeMicrochipObjectPacket(menu.containerId, x, y, false, true, shift).sendToServer();
			return true;
		}
		
		return false;
	}
	
	private boolean placeNote(int x, int y, MouseButtonEvent event)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var player = screen.getMinecraft().player;
		
		boolean leftClick = event.button() == InputConstants.MOUSE_BUTTON_LEFT;
		boolean rightClick = event.button() == InputConstants.MOUSE_BUTTON_RIGHT;
		if((leftClick || rightClick) &&
		   carried.getItem() instanceof StickyNoteItem &&
		   context.shouldInteractBoard())
		{
			int placeX = event.hasControlDown() ? MicrochipScreen.getGridSnappedCoord(x) : (x - 8);
			int placeY = event.hasControlDown() ? MicrochipScreen.getGridSnappedCoord(y) : (y - 8);
			
			if(microchip.size().bounds().normalize().contains(new Bounds(placeX, placeY, 16, 16)))
			{
				var stickyNote = microchip.stickyNotes().add(placeX, placeY, carried);
				if(stickyNote != null)
				{
					microchip.markDirty(false);
					if(!player.hasInfiniteMaterials() || leftClick)
					{
						carried.shrink(1);
					}
					new PlaceTakeMicrochipObjectPacket(menu.containerId, placeX, placeY, true, leftClick, event.hasShiftDown()).sendToServer();
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean placeWire(int x, int y, MouseButtonEvent event)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var port = context.port();
		
		if(carried.is(LBRItems.REDSTONE_BIT.asItem()) &&
		   this.hasSelectedPort())
		{
			if(event.button() == InputConstants.MOUSE_BUTTON_LEFT &&
			   context.shouldInteractPort() &&
			   context.isPortInput() &&
			   context.isPortEmpty() &&
			   microchip.wires().add(selectedPort, port))
			{
				microchip.markDirty(false);
				carried.consume(1, screen.getMinecraft().player);
				new PlaceTakeMicrochipWirePacket(menu.containerId, selectedPort, port, true).sendToServer();
				if(carried.isEmpty())
				{
					selectedPort = null;
				}
			}
			else
			{
				selectedPort = null;
			}
			return true;
		}
		
		return false;
	}
	
	private boolean placeLogic(int x, int y, MouseButtonEvent event)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var player = screen.getMinecraft().player;
		
		boolean leftClick = event.button() == InputConstants.MOUSE_BUTTON_LEFT;
		boolean rightClick = event.button() == InputConstants.MOUSE_BUTTON_RIGHT;
		if((leftClick || rightClick) &&
		   carried.has(LBRComponents.LOGIC) &&
		   context.shouldInteractBoard())
		{
			var component = carried.get(LBRComponents.LOGIC);
			int placeX = event.hasControlDown() ? MicrochipScreen.getGridSnappedCoord(component.size().topLeftCornerX(x) + 8) : component.size().topLeftCornerX(x);
			int placeY = event.hasControlDown() ? MicrochipScreen.getGridSnappedCoord(component.size().topLeftCornerY(y) + 8) : component.size().topLeftCornerY(y);
			
			if(microchip.size().bounds().normalize().contains(component.size().toBounds(placeX, placeY)))
			{
				var logic = microchip.components().add(placeX, placeY, component);
				if(logic != null)
				{
					menu.placeCarriedWires(logic.slot());
					microchip.markDirty(true);
					if(!player.hasInfiniteMaterials() || leftClick)
					{
						carried.shrink(1);
					}
					new PlaceTakeMicrochipObjectPacket(menu.containerId, placeX, placeY, true, leftClick, event.hasShiftDown()).sendToServer();
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean openLogicConfig(int x, int y, MouseButtonEvent event)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var logic = context.logic();
		
		if(event.button() == InputConstants.MOUSE_BUTTON_RIGHT &&
		   context.shouldInteractLogic() &&
		   carried.isEmpty() &&
		   logic.component().config().hasMenu())
		{
			new OpenLogicConfigPacket(menu.containerId, logic.slot(), viewPosition).sendToServer();
			return true;
		}
		
		return false;
	}
	
	private boolean openNote(int x, int y, MouseButtonEvent event)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var player = screen.getMinecraft().player;
		var note = context.note();
		
		if(event.button() == InputConstants.MOUSE_BUTTON_RIGHT &&
		   context.shouldInteractNote() &&
		   carried.isEmpty())
		{
			Proxies.get(LBRProxy.class).openStickyNote(new MicrochipStickyNoteReference(note), note.isEditable());
			return true;
		}
		
		return false;
	}
	
	private boolean mouseClickedOnBoard(int mouseX, int mouseY, int boardMouseX, int boardMouseY, MouseButtonEvent event)
	{
		context = MicrochipWidgetContext.test(this, panel, mouseX, mouseY, boardMouseX, boardMouseY, context);
		
		return this.dyeComponent(boardMouseX, boardMouseY, event) ||
			   this.pickupNote(boardMouseX, boardMouseY, event) ||
			   this.pickupWire(boardMouseX, boardMouseY, event) ||
			   this.pickupLogic(boardMouseX, boardMouseY, event) ||
			   this.placeNote(boardMouseX, boardMouseY, event) ||
			   this.placeWire(boardMouseX, boardMouseY, event) ||
			   this.placeLogic(boardMouseX, boardMouseY, event) ||
			   this.openLogicConfig(boardMouseX, boardMouseY, event) ||
			   this.openNote(boardMouseX, boardMouseY, event);
	}
	
	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY)
	{
		if(allowDragging)
		{
			viewPosition.pan(dragX, dragY);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
	{
		int mouseX = (int) event.x();
		int mouseY = (int) event.y();
		if(this.isMouseOver(mouseX, mouseY))
		{
			var size = microchip.size();
			int boardMouseX = size.boardCoord(this.toLocalX(mouseX), viewPosition.zoom(), viewPosition.x());
			int boardMouseY = size.boardCoord(this.toLocalY(mouseY), viewPosition.zoom(), viewPosition.y());
			return allowDragging = event.button() == InputConstants.MOUSE_BUTTON_MIDDLE ||
								   !this.mouseClickedOnBoard(mouseX, mouseY, boardMouseX, boardMouseY, event);
		}
		else
		{
			if(this.hasSelectedPort())
			{
				selectedPort = null;
				return true;
			}
			return false;
		}
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
	{
		if(context.isOnBoard())
		{
			viewPosition.zoom(scrollY > 0 ? 0.25f : -0.25f, mouseX, mouseY);
			return true;
		}
		return false;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
	{
		var size = microchip.size();
		int boardMouseX = size.boardCoord(this.toLocalX(mouseX), viewPosition.zoom(), viewPosition.x());
		int boardMouseY = size.boardCoord(this.toLocalY(mouseY), viewPosition.zoom(), viewPosition.y());
		
		context = MicrochipWidgetContext.test(this, panel, mouseX, mouseY, boardMouseX, boardMouseY, context);
		
		graphics.enableScissor(x, y, x + MicrochipBlockEntity.CIRCUIT_BOUNDS.width(), y + MicrochipBlockEntity.CIRCUIT_BOUNDS.height());
		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		graphics.pose().scale(microchip.size().scale(), microchip.size().scale());
		graphics.pose().scale(viewPosition.zoom(), viewPosition.zoom());
		graphics.pose().translate((int) -viewPosition.x(), (int) -viewPosition.y());
		panel.render(graphics);
		graphics.pose().popMatrix();
		graphics.disableScissor();
	}
	
	private void renderTooltip(GuiGraphicsExtractor graphics, List<Component> lines, boolean dropShadow, int x, int y, Identifier style, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom)
	{
		int maxWidth = graphics.guiWidth() - x - 8;
		graphics.tooltip(
				Minecraft.getInstance().font,
				lines,
				dropShadow,
				Optional.empty(),
				(screenWidth, screenHeight, x1, y1, tooltipWidth, tooltipHeight) ->
						new ExtendedClientTooltipPositioner.Result(x, y, paddingLeft, paddingTop, paddingRight, paddingBottom),
				x,
				y,
				maxWidth,
				true,
				style
		);
	}
	
	private void renderTooltipLogic(GuiGraphicsExtractor graphics, List<Component> lines, int x, int y, DyeColor dyeColor)
	{
		this.renderTooltip(
				graphics,
				lines,
				true,
				x,
				y,
				LBR.id("logic/" + dyeColor.getName()),
				0,
				0,
				0,
				0
		);
	}
	
	private void renderTooltipStickyNote(GuiGraphicsExtractor graphics, List<Component> lines, int x, int y, DyeColor dyeColor)
	{
		int paddingBottom = 0;
		var tooltipBackgroundSprite = SpriteGraphics.getSpriteScaling(SpriteGraphics.getSprite(LBR.id("tooltip/sticky_note/" + dyeColor.getName() + "_background")));
		if(tooltipBackgroundSprite instanceof GuiSpriteScaling.NineSlice nineSlice)
		{
			paddingBottom = nineSlice.border().bottom();
		}
		this.renderTooltip(
				graphics,
				lines,
				false,
				x,
				y,
				LBR.id("sticky_note/" + dyeColor.getName()),
				0,
				0,
				0,
				paddingBottom
		);
	}
	
	private void renderTooltipStickyNote(GuiGraphicsExtractor graphics, int x, int y)
	{
		var entry = context.note();
		var note = entry.note();
		if(!note.isEmpty())
		{
			int minWidth = graphics.guiWidth() - x - 6;
			this.renderTooltipStickyNote(graphics, List.of(note.parsed()), x, y, entry.noteColor());
		}
	}
	
	private void renderTooltipLogic(GuiGraphicsExtractor graphics, int x, int y)
	{
		var component = context.logic().component();
		List<Component> lines = Lists.newArrayList();
		lines.add(component.type().displayName().withStyle(Style.EMPTY.withUnderlined(true)));
		component.type().tooltip(component, false, true, false).ifPresent((Consumer<List<Component>>) lines::addAll);
		if(component.config().hasMenu())
		{
			lines.add(Component.empty());
			lines.add(LBR.text().logicConfigTooltipClickToOpen());
		}
		
		var dyeColor = (DyeColor) component.color().orElse(this.color());
		this.renderTooltipLogic(graphics, lines, x, y, dyeColor);
		
		if(microchip.isDebug())
		{
			graphics.pose().pushMatrix();
			graphics.pose().translate(this.x + 211, this.y + 139);
			graphics.pose().scale(2, 2);
			var context = LogicRenderer.Context.create(this.color(), component, this.menu().getCarriedWires() != null, this.hasSelectedPort(), false);
			LogicRenderers.render(context, graphics, component, 0, 0);
			graphics.pose().popMatrix();
		}
	}
	
	private void renderTooltipWire(GuiGraphicsExtractor graphics, int x, int y)
	{
		var component = context.logic().component();
		
		List<Component> lines = Lists.newArrayList();
		lines.add(LBRItems.REDSTONE_BIT.asItem().getDefaultInstance().getHoverName().copy().withStyle(Style.EMPTY.withUnderlined(true)));
		lines.add(LBR.text().logicConfigTooltipSignal(context.wireSignal()));
		
		var dyeColor = (DyeColor) component.color().orElse(this.color());
		this.renderTooltipLogic(graphics, lines, x, y, dyeColor);
	}
	
	public void renderTooltip(GuiGraphicsExtractor graphics)
	{
		if(context.shouldRenderTooltip())
		{
			int x = this.x + microchip.size().scale(microchip.size().bounds().width()) + 10 + 4;
			int y = this.y + 4;
			if(context.hasObject() && context.hasNote())
			{
				this.renderTooltipStickyNote(graphics, x, y);
			}
			else if(context.hasObject() && context.hasLogic())
			{
				this.renderTooltipLogic(graphics, x, y);
			}
			else if(!context.hasObject() && context.hasWire() && context.hasLogic())
			{
				this.renderTooltipWire(graphics, x, y);
			}
		}
	}
	
	@Override
	public void setFocused(boolean focused)
	{
		this.focused = focused;
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
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		return this.getRectangle().containsPoint((int) mouseX, (int) mouseY);
	}
	
	@Override
	public ScreenRectangle getRectangle()
	{
		return new ScreenRectangle(x, y, microchip.size().scale(width), microchip.size().scale(height));
	}
	
	public int toLocalX(int x)
	{
		return x - this.x;
	}
	
	public int toLocalY(int y)
	{
		return y - this.y;
	}
}
