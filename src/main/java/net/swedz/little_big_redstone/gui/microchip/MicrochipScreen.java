package net.swedz.little_big_redstone.gui.microchip;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItemDisplayContext;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipThermostatWidget;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidget;
import net.swedz.little_big_redstone.gui.microchip.wire.WireMetadata;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePath;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathKey;
import net.swedz.little_big_redstone.gui.slot.MaybeLockedPlayerSlot;
import net.swedz.little_big_redstone.item.LogicItem;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.little_big_redstone.network.packet.ScrollLogicCreativePacket;
import net.swedz.little_big_redstone.network.packet.StoreMicrochipViewPositionPacket;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.Optional;

public final class MicrochipScreen extends AbstractContainerScreen<MicrochipMenu>
{
	private static final ResourceLocation MICROCHIP_BACKGROUND   = LBR.id("textures/gui/container/microchip/inventory_background.png");
	private static final ResourceLocation LOGIC_ARRAY_BACKGROUND = LBR.id("textures/gui/container/logic_array/microchip_inventory_background.png");
	
	public static int getGridSnappedCoord(int coord)
	{
		return (coord / 16) * 16;
	}
	
	private MicrochipWidget microchipWidget;
	private MicrochipThermostatWidget thermostatWidget;
	
	private float partialTicks;
	
	public MicrochipScreen(
			MicrochipMenu menu,
			Inventory playerInventory,
			Component title
	)
	{
		super(menu, playerInventory, title);
		
		imageWidth = 256;
		imageHeight = 227;
	}
	
	public void handleUpdate(boolean rerouteWires)
	{
		microchipWidget.handleUpdate(rerouteWires);
	}
	
	private boolean isWithinBoard(int x, int y)
	{
		return menu.microchip().size().bounds().contains(x, y);
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		this.addRenderableWidget(microchipWidget = new MicrochipWidget(leftPos + 8, topPos + 8, this, menu.viewPosition()));
		
		this.addRenderableWidget(thermostatWidget = new MicrochipThermostatWidget(leftPos, topPos, this));
	}
	
	public MicrochipWidget getMicrochipWidget()
	{
		return microchipWidget;
	}
	
	public MicrochipThermostatWidget getThermostatWidget()
	{
		return thermostatWidget;
	}
	
	@Override
	protected void containerTick()
	{
		thermostatWidget.tick();
	}
	
	@Override
	public void removed()
	{
		new StoreMicrochipViewPositionPacket(menu.blockPos(), menu.viewPosition()).sendToServer();
		
		super.removed();
	}
	
	private boolean isOverLogicArrayPanel(double mouseX, double mouseY)
	{
		if(!menu.getLogicArrayItemHandler().isCreativeMode())
		{
			return false;
		}
		double localX = mouseX - leftPos;
		double localY = mouseY - topPos;
		return localX >= -75 && localX < -3 && localY >= 10 && localY < 136;
	}

	private boolean adjustCreativeScroll(int delta)
	{
		var creative = menu.getLogicArrayItemHandler().creativeHandler();
		int current = creative.scrollRows();
		int next = Math.max(0, Math.min(creative.maxScrollRows(), current + delta));
		if(next == current)
		{
			return false;
		}
		creative.setScrollRows(next);
		new ScrollLogicCreativePacket(menu.containerId, next).sendToServer();
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
	{
		if(this.isOverLogicArrayPanel(mouseX, mouseY) && this.adjustCreativeScroll(scrollY > 0 ? -1 : 1))
		{
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if(menu.getLogicArrayItemHandler().isCreativeMode() && this.getFocused() == null)
		{
			if(keyCode == GLFW.GLFW_KEY_UP && this.adjustCreativeScroll(-1))
			{
				return true;
			}
			if(keyCode == GLFW.GLFW_KEY_DOWN && this.adjustCreativeScroll(1))
			{
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
	{
		boolean drag = this.getFocused() != null &&
					   this.isDragging() &&
					   (button == InputConstants.MOUSE_BUTTON_LEFT || button == InputConstants.MOUSE_BUTTON_MIDDLE) &&
					   this.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY);
		return drag || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		
		this.renderTooltip(graphics, mouseX, mouseY);
		microchipWidget.renderTooltip(new TesseractGuiGraphics(graphics));
		thermostatWidget.renderTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	protected void renderSlot(GuiGraphics vanilla, Slot slot)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		if(slot instanceof MaybeLockedPlayerSlot && slot.index == menu.getLogicArrayItemHandler().getSelectedSlot())
		{
			graphics.pose().pushPose();
			graphics.pose().translate(0, 0, 100);
			
			graphics.setTexture(LBR.id("textures/gui/slot_atlas.png"));
			graphics.blit(slot.x - 1, slot.y - 1, 0, 18, 18, 18);
			
			this.renderSlotContents(vanilla, slot.getItem(), slot, null);
			
			graphics.pose().popPose();
			
			return;
		}
		
		super.renderSlot(vanilla, slot);
	}
	
	@Override
	public void renderFloatingItem(GuiGraphics vanilla, ItemStack carried, int localX, int localY, String text)
	{
		int mouseX = localX + leftPos + 8;
		int mouseY = localY + topPos + 8;
		
		if(microchipWidget.isMouseOver(mouseX, mouseY))
		{
			var microchip = menu.microchip();
			var size = microchip.size();
			int boardMouseX = size.boardCoord(localX, microchipWidget.viewPosition().zoom(), microchipWidget.viewPosition().x());
			int boardMouseY = size.boardCoord(localY, microchipWidget.viewPosition().zoom(), microchipWidget.viewPosition().y());
			
			var graphics = new TesseractGuiGraphics(vanilla);
			
			vanilla.enableScissor(microchipWidget.x(), microchipWidget.y(), microchipWidget.x() + MicrochipBlockEntity.CIRCUIT_BOUNDS.width(), microchipWidget.y() + MicrochipBlockEntity.CIRCUIT_BOUNDS.height());
			graphics.pose().pushPose();
			graphics.pose().translate(8, 8, 232);
			graphics.pose().scale(size.scale(), size.scale(), 1);
			graphics.pose().scale(microchipWidget.viewPosition().zoom(), microchipWidget.viewPosition().zoom(), 1);
			graphics.pose().translate(-microchipWidget.viewPosition().x(), -microchipWidget.viewPosition().y(), 0);
			
			if(StickyNoteItem.hasRelevantComponents(carried))
			{
				int itemX = Screen.hasControlDown() ? getGridSnappedCoord(boardMouseX) : (boardMouseX - 8);
				int itemY = Screen.hasControlDown() ? getGridSnappedCoord(boardMouseY) : (boardMouseY - 8);
				
				graphics.renderItem(carried, LBRItemDisplayContext.MICROCHIP_GUI, itemX, itemY);
				
				graphics.pose().popPose();
				vanilla.disableScissor();
				return;
			}
			else if(carried.has(LBRComponents.LOGIC_CONFIG))
			{
				var logicColor = LogicItem.getColor(carried);
				var config = carried.get(LBRComponents.LOGIC_CONFIG);
				var context = LogicRenderer.Context.create(logicColor, menu.color(), config.type(), menu.getCarriedWires() != null, microchipWidget.hasSelectedPort(), false);
				
				int logicX = Screen.hasControlDown() ? getGridSnappedCoord(config.size().topLeftCornerX(boardMouseX) + 8) : config.size().topLeftCornerX(boardMouseX);
				int logicY = Screen.hasControlDown() ? getGridSnappedCoord(config.size().topLeftCornerY(boardMouseY) + 8) : config.size().topLeftCornerY(boardMouseY);
				
				this.renderCarriedWires(graphics, logicX, logicY, context, config, logicColor);
				this.renderCarriedLogic(graphics, logicX, logicY, context, config, logicColor);
				
				graphics.pose().popPose();
				vanilla.disableScissor();
				return;
			}
			else if(carried.is(LBRItems.REDSTONE_BIT.asItem()))
			{
				graphics.setTexture(LBR.id("textures/item/redstone_bit.png"));
				if(!microchipWidget.context().hasPort())
				{
					graphics.setTextureShader(LBRClientShaders::pulsingTextureAlpha);
				}
				graphics.blit(boardMouseX - 8, boardMouseY - 8, 0, 0, 16, 16, 16, 16);
				graphics.renderItemDecorations(carried, boardMouseX - 8, boardMouseY - 8);
				
				graphics.pose().popPose();
				vanilla.disableScissor();
				return;
			}
			
			graphics.pose().popPose();
			vanilla.disableScissor();
		}
		
		super.renderFloatingItem(vanilla, carried, localX, localY, text);
	}
	
	private record CarriedWiresData(WirePathKey key, WirePath path)
	{
	}
	
	private void renderCarriedWires(TesseractGuiGraphics graphics, int logicX, int logicY, LogicRenderer.Context context, LogicConfig config, Optional<DyeColor> color)
	{
		if(menu.getCarriedWires() != null)
		{
			var wirePanel = microchipWidget.panel().wires();
			
			Map<Wire, CarriedWiresData> paths = Maps.newConcurrentMap();
			for(var wire : menu.getCarriedWires())
			{
				var key = WirePathKey.carried(microchipWidget.context(), menu.getCarriedComponentSlot(), config, wire, logicX, logicY);
				var path = wirePanel.pathing().get(key, false);
				paths.put(wire, new CarriedWiresData(key, path));
			}
			WirePath.blockAllOf(paths.values().stream().map(CarriedWiresData::path).toList());
			
			graphics.pose().pushPose();
			graphics.enableBatching();
			for(var entry : paths.entrySet())
			{
				var wire = entry.getKey();
				var data = entry.getValue();
				var metadata = WireMetadata.carried(microchipWidget.microchip(), microchipWidget.color(), menu.getCarriedComponentSlot(), config.type(), color, wire);
				wirePanel.renderWire(graphics, data.key(), data.path(), metadata);
			}
			graphics.drawBatches();
			graphics.pose().popPose();
		}
	}
	
	private void renderCarriedLogic(TesseractGuiGraphics graphics, int logicX, int logicY, LogicRenderer.Context context, LogicConfig config, Optional<DyeColor> logicColor)
	{
		var microchip = menu.microchip();
		var size = microchip.size();
		
		graphics.pose().pushPose();
		graphics.enableBatching();
		
		LogicRenderers.render(context, graphics, config.type().create(config, logicColor), logicX, logicY);
		
		graphics.drawBatches();
		graphics.pose().popPose();
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		// Dont render the "Microchip" and "Inventory" labels
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
	{
		this.partialTicks = partialTicks;
		
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos, topPos, 0);
		
		graphics.blit(MICROCHIP_BACKGROUND, 0, 0, 0, 0, 256, 256);
		
		if(menu.getLogicArrayItemHandler().shouldDisplay())
		{
			graphics.blit(LOGIC_ARRAY_BACKGROUND, -83, 0, 0, 0, 256, 256);
		}
		
		graphics.pose().popPose();
	}
}
