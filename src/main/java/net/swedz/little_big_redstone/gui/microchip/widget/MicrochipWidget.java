package net.swedz.little_big_redstone.gui.microchip.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.gui.microchip.MicrochipMenu;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.microchip.logic.DyeComponentResult;
import net.swedz.little_big_redstone.item.logicarray.LogicArrayItem;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicSelectedPort;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.little_big_redstone.network.packet.DyeMicrochipObjectPacket;
import net.swedz.little_big_redstone.network.packet.OpenLogicConfigPacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipObjectPacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipWirePacket;

public final class MicrochipWidget implements GuiEventListener, Renderable, NarratableEntry
{
	final int x, y, width, height;
	
	private final MicrochipScreen screen;
	private final Microchip       microchip;
	
	private final MicrochipWidgetRenderer renderer;
	private final MicrochipWidgetWires    wires;
	
	private MicrochipWidgetContext context = new MicrochipWidgetContext(this, 0, 0);
	
	private LogicSelectedPort selectedPort;
	
	public MicrochipWidget(int x, int y, MicrochipScreen screen)
	{
		this.screen = screen;
		this.microchip = screen.getMenu().microchip();
		
		this.renderer = new MicrochipWidgetRenderer(this);
		this.wires = new MicrochipWidgetWires(this);
		
		var bounds = microchip.size().bounds();
		this.x = x + microchip.size().scale(bounds.minX());
		this.y = y + microchip.size().scale(bounds.minY());
		this.width = bounds.width();
		this.height = bounds.height();
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
	
	public MicrochipWidgetWires wireRenderer()
	{
		return wires;
	}
	
	public MicrochipWidgetContext context()
	{
		return context;
	}
	
	public boolean hasSelectedPort()
	{
		return selectedPort != null;
	}
	
	public LogicSelectedPort getSelectedPort()
	{
		return selectedPort;
	}
	
	public void handleUpdate()
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
		
		wires.rebuildPaths();
	}
	
	private boolean dyeComponent(int x, int y, int button)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var entry = context.object();
		
		if(button == InputConstants.MOUSE_BUTTON_RIGHT &&
		   context.shouldDyeObject())
		{
			int slot = entry.slot();
			var result = DyeComponentResult.test(carried, entry.color());
			if(result.success())
			{
				if(entry.setColor(result.color()))
				{
					microchip.markDirty();
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
	
	private boolean pickupNote(int x, int y, int button)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var note = context.note();
		
		boolean shift = Screen.hasShiftDown();
		if(button == InputConstants.MOUSE_BUTTON_LEFT &&
		   context.shouldInteractNote() &&
		   carried.isEmpty())
		{
			microchip.stickyNotes().remove(note);
			microchip.markDirty();
			var stack = note.toStack();
			if(!shift || !menu.moveItemStackTo(stack, LogicArrayItem.MAX_SLOTS, menu.slots.size(), true))
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
			microchip.markDirty();
			if(carried.isEmpty())
			{
				menu.setCarried(LBRItems.REDSTONE_BIT.asItem().getDefaultInstance());
			}
			else if(!screen.getMinecraft().player.hasInfiniteMaterials())
			{
				carried.grow(1);
			}
			selectedPort = new LogicSelectedPort(microchip.components().get(wire.output().slot()), wire.output().index());
			new PlaceTakeMicrochipWirePacket(menu.containerId, wire, false).sendToServer();
			return true;
		}
		return false;
	}
	
	private boolean pickupWire(int x, int y, int button)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		
		if(button == InputConstants.MOUSE_BUTTON_LEFT &&
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
				if(context.isPortOutput() && !carried.isEmpty())
				{
					selectedPort = context.port();
					return true;
				}
				else if(context.isPortInput() && (carried.isEmpty() || carried.getCount() < carried.getMaxStackSize()))
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
	
	private boolean pickupLogic(int x, int y, int button)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var logic = context.logic();
		
		boolean shift = Screen.hasShiftDown();
		if(button == InputConstants.MOUSE_BUTTON_LEFT &&
		   context.shouldInteractLogic() &&
		   carried.isEmpty())
		{
			var wiresPopped = microchip.components().remove(logic);
			microchip.markDirty();
			wires.rebuildPaths();
			var stack = logic.toStack();
			if(!shift || !menu.moveItemStackTo(stack, LogicArrayItem.MAX_SLOTS, menu.slots.size(), true))
			{
				menu.setCarried(stack);
				menu.setCarriedWires(logic.slot(), wiresPopped);
			}
			new PlaceTakeMicrochipObjectPacket(menu.containerId, x, y, false, true, shift).sendToServer();
			return true;
		}
		
		return false;
	}
	
	private boolean placeNote(int x, int y, int button)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var player = screen.getMinecraft().player;
		
		boolean leftClick = button == InputConstants.MOUSE_BUTTON_LEFT;
		boolean rightClick = button == InputConstants.MOUSE_BUTTON_RIGHT;
		if((leftClick || rightClick) &&
		   carried.getItem() instanceof StickyNoteItem &&
		   context.shouldInteractBoard())
		{
			int placeX = Screen.hasControlDown() ? MicrochipScreen.getGridSnappedCoord(x) : (x - 8);
			int placeY = Screen.hasControlDown() ? MicrochipScreen.getGridSnappedCoord(y) : (y - 8);
			
			if(microchip.size().bounds().normalize().contains(new Bounds(placeX, placeY, 16, 16)))
			{
				var stickyNote = microchip.stickyNotes().add(placeX, placeY, carried);
				if(stickyNote != null)
				{
					microchip.markDirty();
					if(!player.hasInfiniteMaterials() || leftClick)
					{
						carried.shrink(1);
					}
					new PlaceTakeMicrochipObjectPacket(menu.containerId, placeX, placeY, true, leftClick, Screen.hasShiftDown()).sendToServer();
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean placeWire(int x, int y, int button)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var port = context.port();
		
		if(carried.is(LBRItems.REDSTONE_BIT.asItem()) &&
		   this.hasSelectedPort())
		{
			if(button == InputConstants.MOUSE_BUTTON_LEFT &&
			   context.shouldInteractPort() &&
			   context.isPortInput() &&
			   context.isPortEmpty() &&
			   microchip.wires().add(selectedPort, port))
			{
				microchip.markDirty();
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
	
	private boolean placeLogic(int x, int y, int button)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var player = screen.getMinecraft().player;
		
		boolean leftClick = button == InputConstants.MOUSE_BUTTON_LEFT;
		boolean rightClick = button == InputConstants.MOUSE_BUTTON_RIGHT;
		if((leftClick || rightClick) &&
		   carried.has(LBRComponents.LOGIC) &&
		   context.shouldInteractBoard())
		{
			var component = carried.get(LBRComponents.LOGIC);
			int placeX = Screen.hasControlDown() ? MicrochipScreen.getGridSnappedCoord(component.size().topLeftCornerX(x) + 8) : component.size().topLeftCornerX(x);
			int placeY = Screen.hasControlDown() ? MicrochipScreen.getGridSnappedCoord(component.size().topLeftCornerY(y) + 8) : component.size().topLeftCornerY(y);
			
			if(microchip.size().bounds().normalize().contains(component.size().toBounds(placeX, placeY)))
			{
				var logic = microchip.components().add(placeX, placeY, component);
				if(logic != null)
				{
					menu.placeCarriedWires(logic.slot());
					microchip.markDirty();
					wires.rebuildPaths();
					if(!player.hasInfiniteMaterials() || leftClick)
					{
						carried.shrink(1);
					}
					new PlaceTakeMicrochipObjectPacket(menu.containerId, placeX, placeY, true, leftClick, Screen.hasShiftDown()).sendToServer();
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean openLogicConfig(int x, int y, int button)
	{
		var menu = this.menu();
		var carried = menu.getCarried();
		var logic = context.logic();
		
		if(button == InputConstants.MOUSE_BUTTON_RIGHT &&
		   context.shouldInteractLogic() &&
		   carried.isEmpty() &&
		   logic.component().config().hasMenu())
		{
			new OpenLogicConfigPacket(menu.containerId, logic.slot()).sendToServer();
			return true;
		}
		
		return false;
	}
	
	private boolean mouseClickedOnBoard(int mouseX, int mouseY, int boardMouseX, int boardMouseY, int button)
	{
		context = MicrochipWidgetContext.test(this, wires, mouseX, mouseY, boardMouseX, boardMouseY, context);
		
		if(this.dyeComponent(boardMouseX, boardMouseY, button))
		{
			return false;
		}
		else if(this.pickupNote(boardMouseX, boardMouseY, button))
		{
			return false;
		}
		else if(this.pickupWire(boardMouseX, boardMouseY, button))
		{
			return false;
		}
		else if(this.pickupLogic(boardMouseX, boardMouseY, button))
		{
			return false;
		}
		else if(this.placeNote(boardMouseX, boardMouseY, button))
		{
			return false;
		}
		else if(this.placeWire(boardMouseX, boardMouseY, button))
		{
			return false;
		}
		else if(this.placeLogic(boardMouseX, boardMouseY, button))
		{
			return false;
		}
		else if(this.openLogicConfig(boardMouseX, boardMouseY, button))
		{
			return false;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseClicked(double mx, double my, int button)
	{
		int mouseX = (int) mx;
		int mouseY = (int) my;
		if(this.isMouseOver(mouseX, mouseY))
		{
			var size = microchip.size();
			int boardMouseX = size.boardCoord(this.toLocalX(mouseX));
			int boardMouseY = size.boardCoord(this.toLocalY(mouseY));
			
			return this.mouseClickedOnBoard(mouseX, mouseY, boardMouseX, boardMouseY, button);
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
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		var size = microchip.size();
		int boardMouseX = size.boardCoord(this.toLocalX(mouseX));
		int boardMouseY = size.boardCoord(this.toLocalY(mouseY));
		
		context = MicrochipWidgetContext.test(this, wires, mouseX, mouseY, boardMouseX, boardMouseY, context);
		
		renderer.render(graphics, boardMouseX, boardMouseY, partialTick);
	}
	
	@Override
	public void setFocused(boolean focused)
	{
	}
	
	@Override
	public boolean isFocused()
	{
		return false;
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
