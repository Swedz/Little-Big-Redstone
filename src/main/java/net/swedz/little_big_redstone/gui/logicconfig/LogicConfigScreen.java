package net.swedz.little_big_redstone.gui.logicconfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.logic.config.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.network.packet.RequestMicrochipMenuPacket;
import net.swedz.little_big_redstone.network.packet.WriteLogicConfigPacket;
import net.swedz.tesseract.neoforge.helper.ComponentHelper;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class LogicConfigScreen extends AbstractContainerScreen<LogicConfigMenu> implements LogicConfigMenuBuilder
{
	private static final ResourceLocation INVENTORY_BACKGROUND = LBR.id("textures/gui/container/logic_config/inventory_background.png");
	
	private final LogicEntry logicEntry;
	
	private int configX, configY;
	
	public LogicConfigScreen(LogicConfigMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		imageWidth = 176;
		imageHeight = 232;
		inventoryLabelY = imageHeight - 94;
		
		logicEntry = menu.logicEntry();
	}
	
	@Override
	public <T> void addCycleButton(Component name, Component tooltip, int x, int y, int width, int height, boolean displayOnlyValue, T initialValue, List<T> values, Function<T, Component> valueStringifier, Consumer<T> onChange)
	{
		var builder = CycleButton.builder((T value) -> ComponentHelper.stripStyle(valueStringifier.apply(value)))
				.withTooltip((__) -> Tooltip.create(tooltip))
				.withValues(values)
				.withInitialValue(initialValue);
		if(displayOnlyValue)
		{
			builder.displayOnlyValue();
		}
		this.addRenderableWidget(builder.create(configX + x, configY + y, width, height, name, (button, value) -> onChange.accept(value)));
	}
	
	@Override
	public void addSlider(Component prefix, Component suffix, Component tooltip, int x, int y, int width, int height, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, Consumer<Double> onChange)
	{
		var widget = new ExtendedSlider(configX + x, configY + y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString)
		{
			@Override
			protected void updateMessage()
			{
				super.updateMessage();
				onChange.accept(minValue + (value * (maxValue - minValue)));
			}
		};
		widget.setTooltip(Tooltip.create(tooltip));
		this.addRenderableWidget(widget);
	}
	
	@Override
	public void addCheckbox(Component text, Component tooltip, int x, int y, boolean initialValue, Consumer<Boolean> onChange)
	{
		this.addRenderableWidget(Checkbox.builder(Component.empty(), Minecraft.getInstance().font)
				.tooltip(Tooltip.create(tooltip))
				.pos(configX + x, configY + y)
				.selected(initialValue)
				.onValueChange((button, value) -> onChange.accept(value))
				.build());
		this.addRenderableOnly((graphics, mouseX, mouseY, partialTicks) ->
				graphics.drawString(Minecraft.getInstance().font, text, configX + x + 21, configY + y + 8 - 3, 0xFFFFFF, false));
	}
	
	private void save()
	{
		new WriteLogicConfigPacket(menu.blockPos(), logicEntry.slot(), logicEntry.component()).sendToServer();
	}
	
	private void cancel()
	{
		new RequestMicrochipMenuPacket(menu.blockPos()).sendToServer();
	}
	
	@Override
	protected void init()
	{
		super.init();
		configX = leftPos + 8;
		configY = topPos + 17;
		
		logicEntry.component().config().buildMenu(this);
		
		this.addRenderableWidget(Button.builder(LBRText.LOGIC_CONFIG_BUTTON_LABEL_SAVE.text(), (__) -> this.save())
				.bounds(leftPos + 8, topPos + imageHeight - 94 - 20, 75, 16)
				.build());
		
		this.addRenderableWidget(Button.builder(LBRText.LOGIC_CONFIG_BUTTON_LABEL_CANCEL.text(), (__) -> this.cancel())
				.bounds(leftPos + imageWidth - 75 - 8, topPos + imageHeight - 94 - 20, 75, 16)
				.build());
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
	{
		graphics.blit(INVENTORY_BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
	{
		return this.getFocused() != null && this.isDragging() && button == 0 ?
				this.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY) :
				super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}
}
