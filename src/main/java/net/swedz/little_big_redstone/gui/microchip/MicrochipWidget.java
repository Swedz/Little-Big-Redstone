package net.swedz.little_big_redstone.gui.microchip;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.logic.DyeComponentResult;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.gui.microchip.wire.WireRendering;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.LogicSelectedPort;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.packet.DyeMicrochipLogicPacket;
import net.swedz.little_big_redstone.network.packet.OpenLogicConfigPacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipLogicPacket;
import net.swedz.little_big_redstone.network.packet.PlaceTakeMicrochipWirePacket;

import java.util.List;
import java.util.function.Consumer;

public final class MicrochipWidget implements GuiEventListener, Renderable, NarratableEntry
{
	private static final ResourceLocation SHADOW_HOVER_OVERLAY = LBR.id("textures/gui/container/microchip/shadow_hover_overlay.png");
	private static final ResourceLocation CIRCUIT_BACKGROUND   = LBR.id("textures/gui/container/microchip/circuit_background.png");
	
	private final int x, y, width, height;
	
	private final MicrochipScreen screen;
	
	private final Microchip microchip;
	
	private final WireRendering wires;
	
	private LogicSelectedPort selectedPort;
	
	public MicrochipWidget(int x, int y, MicrochipScreen screen)
	{
		this.screen = screen;
		this.microchip = screen.getMenu().microchip();
		
		this.wires = new WireRendering(this);
		
		var bounds = microchip.size().bounds();
		this.x = x + microchip.size().scale(bounds.minX());
		this.y = y + microchip.size().scale(bounds.minY());
		this.width = bounds.width();
		this.height = bounds.height();
	}
	
	public DyeColor color()
	{
		return screen.getMenu().color();
	}
	
	public Microchip microchip()
	{
		return microchip;
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
	
	private boolean dyeComponent(int x, int y, int button, LogicEntry entry)
	{
		var menu = screen.getMenu();
		var carried = menu.getCarried();
		
		if(button == InputConstants.MOUSE_BUTTON_RIGHT &&
		   entry != null)
		{
			var result = DyeComponentResult.test(menu, carried, entry);
			if(result.success())
			{
				entry.component().setColor(result.color());
				microchip.markDirty();
				if(result.consume())
				{
					carried.consume(1, screen.getMinecraft().player);
				}
				result.playSound(screen.getMinecraft().player);
				new DyeMicrochipLogicPacket(menu.containerId, entry.slot()).sendToServer();
				return true;
			}
		}
		
		return false;
	}
	
	private boolean pickupWire(int x, int y, int button, LogicSelectedPort outputPort, LogicSelectedPort inputPort)
	{
		var menu = screen.getMenu();
		var carried = menu.getCarried();
		
		if(button == InputConstants.MOUSE_BUTTON_LEFT &&
		   (carried.isEmpty() || carried.is(LBRItems.REDSTONE_BIT.asItem())))
		{
			if(outputPort != null && !carried.isEmpty())
			{
				selectedPort = outputPort;
				return true;
			}
			else if(inputPort != null && (carried.isEmpty() || carried.getCount() < carried.getMaxStackSize()))
			{
				var wires = microchip.wires().getByInputSlot(inputPort);
				if(!wires.isEmpty())
				{
					var wire = wires.getFirst();
					if(microchip.wires().remove(wire))
					{
						microchip.markDirty();
						if(carried.isEmpty())
						{
							menu.setCarried(LBRItems.REDSTONE_BIT.asItem().getDefaultInstance());
						}
						else
						{
							carried.grow(1);
						}
						selectedPort = new LogicSelectedPort(microchip.components().get(wire.output().slot()), wire.output().index());
						new PlaceTakeMicrochipWirePacket(menu.containerId, wire, false).sendToServer();
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean pickupLogic(int x, int y, int button, LogicEntry entry)
	{
		var menu = screen.getMenu();
		var carried = menu.getCarried();
		
		if(button == InputConstants.MOUSE_BUTTON_LEFT &&
		   entry != null &&
		   carried.isEmpty())
		{
			microchip.components().remove(entry);
			microchip.markDirty();
			wires.rebuildPaths();
			menu.setCarried(entry.toStack());
			new PlaceTakeMicrochipLogicPacket(menu.containerId, x, y, false).sendToServer();
			return true;
		}
		
		return false;
	}
	
	private boolean placeWire(int x, int y, int button, LogicSelectedPort inputPort)
	{
		var menu = screen.getMenu();
		var carried = menu.getCarried();
		
		if(carried.is(LBRItems.REDSTONE_BIT.asItem()) &&
		   this.hasSelectedPort())
		{
			if(button == InputConstants.MOUSE_BUTTON_LEFT &&
			   inputPort != null && microchip.wires().add(selectedPort, inputPort))
			{
				microchip.markDirty();
				carried.shrink(1);
				new PlaceTakeMicrochipWirePacket(menu.containerId, selectedPort, inputPort, true).sendToServer();
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
		var menu = screen.getMenu();
		var carried = menu.getCarried();
		
		if(button == InputConstants.MOUSE_BUTTON_LEFT &&
		   carried.has(LBRComponents.LOGIC))
		{
			var component = carried.get(LBRComponents.LOGIC);
			int placeX = component.size().topLeftCornerX(x);
			int placeY = component.size().topLeftCornerY(y);
			
			if(microchip.size().bounds().normalize().contains(component.size().toBounds(placeX, placeY)) &&
			   microchip.components().add(placeX, placeY, component) != null)
			{
				microchip.markDirty();
				wires.rebuildPaths();
				carried.shrink(1);
				new PlaceTakeMicrochipLogicPacket(menu.containerId, placeX, placeY, true).sendToServer();
				return true;
			}
		}
		
		return false;
	}
	
	private boolean openLogicConfig(int x, int y, int button, LogicEntry entry)
	{
		var menu = screen.getMenu();
		var carried = menu.getCarried();
		
		if(button == InputConstants.MOUSE_BUTTON_RIGHT &&
		   entry != null &&
		   carried.isEmpty())
		{
			new OpenLogicConfigPacket(menu.containerId, entry.slot()).sendToServer();
			return true;
		}
		
		return false;
	}
	
	private boolean mouseClickedOnBoard(int x, int y, int button)
	{
		var entry = microchip.components().findAt(x, y);
		var inputPort = microchip.components().findPortAt(x, y, true);
		var outputPort = microchip.components().findPortAt(x, y, false);
		
		if(this.dyeComponent(x, y, button, entry))
		{
			return false;
		}
		else if(this.pickupWire(x, y, button, outputPort, inputPort))
		{
			return false;
		}
		else if(this.pickupLogic(x, y, button, entry))
		{
			return false;
		}
		else if(this.placeWire(x, y, button, inputPort))
		{
			return false;
		}
		else if(this.placeLogic(x, y, button))
		{
			return false;
		}
		else if(this.openLogicConfig(x, y, button, entry))
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
			return this.mouseClickedOnBoard(microchip.size().boardX(this.toLocalX(mouseX)), microchip.size().boardY(this.toLocalY(mouseY)), button);
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
	
	private void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		if(this.isMouseOver(mouseX, mouseY))
		{
			var carried = screen.getMenu().getCarried();
			if(carried.isEmpty())
			{
				var hovered = microchip.components().findAt(microchip.size().boardX(this.toLocalX(mouseX)), microchip.size().boardY(this.toLocalY(mouseY)));
				if(hovered != null)
				{
					var component = hovered.component();
					List<Component> lines = Lists.newArrayList();
					lines.add(component.type().displayName().withStyle(Style.EMPTY.withUnderlined(true)));
					component.type().tooltip(component, false, true, false).ifPresent((Consumer<List<Component>>) lines::addAll);
					graphics.renderComponentTooltip(Minecraft.getInstance().font, lines, mouseX, mouseY);
				}
			}
		}
	}
	
	private void renderLogic(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		for(var entry : microchip.components().traversal())
		{
			var context = LogicRenderer.Context.create(screen.getMenu().color(), entry.component(), false, this.hasSelectedPort(), screen.getMenu().getCarried().is(LBRItems.REDSTONE_BIT.asItem()));
			LogicRenderers.render(context, graphics, entry.component(), entry.x(), entry.y());
		}
	}
	
	private void renderCircuitBg(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		GuiGraphicsHelper.setColor(graphics, LBRColors.circuitboard(screen.getMenu().color()));
		graphics.blit(CIRCUIT_BACKGROUND, 0, 0, 0, 0, width, height, 64, 64);
		GuiGraphicsHelper.resetColor(graphics);
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(x, y, 0);
		graphics.pose().scale(microchip.size().scale(), microchip.size().scale(), microchip.size().scale());
		
		this.renderCircuitBg(graphics, mouseX, mouseY, partialTicks);
		this.renderLogic(graphics, mouseX, mouseY, partialTicks);
		wires.renderWires(graphics, mouseX, mouseY, partialTicks);
		
		graphics.pose().popPose();
		
		this.renderTooltip(graphics, mouseX, mouseY, partialTicks);
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
