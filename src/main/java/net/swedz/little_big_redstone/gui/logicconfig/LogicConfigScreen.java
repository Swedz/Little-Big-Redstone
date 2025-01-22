package net.swedz.little_big_redstone.gui.logicconfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.microchip.LogicEntry;
import net.swedz.little_big_redstone.microchip.logic.LogicConfigMenuBuilder;

import java.util.function.Consumer;
import java.util.function.Function;

public final class LogicConfigScreen extends AbstractContainerScreen<LogicConfigMenu> implements LogicConfigMenuBuilder
{
	private static final ResourceLocation INVENTORY_BACKGROUND = LBR.id("textures/gui/container/logic_config/inventory_background.png");
	
	private final LogicEntry logicEntry;
	
	public LogicConfigScreen(LogicConfigMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		imageWidth = 176;
		imageHeight = 232;
		inventoryLabelY = imageHeight - 94;
		
		logicEntry = menu.logicEntry();
	}
	
	@Override
	public <T> void addCycleButton(Component name, int x, int y, int width, int height, boolean displayOnlyValue, T initialValue, Function<T, Component> valueStringifier, Consumer<T> onChange)
	{
		var builder = CycleButton.builder(valueStringifier)
				.withInitialValue(initialValue);
		if(displayOnlyValue)
		{
			builder.displayOnlyValue();
		}
		this.addRenderableWidget(builder.create(x, y, width, height, name, (button, value) -> onChange.accept(value)));
	}
	
	@Override
	public void addSlider(Component prefix, Component suffix, int x, int y, int width, int height, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, Consumer<Double> onChange)
	{
		this.addRenderableWidget(new ExtendedSlider(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString)
		{
			@Override
			protected void applyValue()
			{
				onChange.accept(value);
			}
		});
	}
	
	@Override
	public void addCheckbox(Component text, int x, int y, boolean initialValue, Consumer<Boolean> onChange)
	{
		this.addRenderableWidget(Checkbox.builder(text, Minecraft.getInstance().font)
				.pos(x, y)
				.selected(initialValue)
				.onValueChange((button, value) -> onChange.accept(value))
				.build());
	}
	
	private void save()
	{
		// TODO send to server and return to microchip
	}
	
	private void cancel()
	{
		// TODO close and return to microchip
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		// TODO dont pass leftPos and topPos here, apply that to the methods implemented above
		logicEntry.component().config().buildMenu(leftPos, topPos, this);
		
		this.addRenderableWidget(Button.builder(Component.literal("Save"), (__) -> this.save())
				.bounds(leftPos + 8, topPos + imageHeight - 94 - 19, 75, 15)
				.build());
		
		this.addRenderableWidget(Button.builder(Component.literal("Cancel"), (__) -> this.cancel())
				.bounds(leftPos + imageWidth - 75 - 8, topPos + imageHeight - 94 - 19, 75, 15)
				.build());
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
	{
		graphics.blit(INVENTORY_BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
