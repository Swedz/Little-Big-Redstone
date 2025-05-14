package net.swedz.little_big_redstone.gui.logicconfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.network.packet.RequestMicrochipMenuPacket;
import net.swedz.little_big_redstone.network.packet.WriteLogicConfigPacket;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
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
	public <T> LogicConfigButtonReference addCycleButton(Component name, Component tooltip, int x, int y, int width, int height, boolean displayOnlyValue, T initialValue, List<T> values, Function<T, Component> valueStringifier, Consumer<T> onChange)
	{
		var builder = CycleButton.builder(valueStringifier)
				.withTooltip((__) -> Tooltip.create(tooltip))
				.withValues(values)
				.withInitialValue(initialValue);
		if(displayOnlyValue)
		{
			builder.displayOnlyValue();
		}
		var button = builder.create(configX + x, configY + y, width, height, name, (__, value) -> onChange.accept(value));
		this.addRenderableWidget(button);
		return new LogicConfigButtonReference<T>()
		{
			@Override
			public void setText(Component text)
			{
				button.setMessage(text);
			}
			
			@Override
			public void setTooltip(Component tooltip)
			{
				button.setTooltip(Tooltip.create(tooltip));
			}
			
			@Override
			public void setValue(T value)
			{
				button.setValue(value);
			}
		};
	}
	
	@Override
	public LogicConfigButtonReference addSlider(Component prefix, Component suffix, Component tooltip, int x, int y, int width, int height, double minValue, double maxValue, double currentValue, double stepSize, int precision, BiFunction<Double, String, Component> valueStringifier, Consumer<Double> onChange)
	{
		var widget = new ExtendedSlider(configX + x, configY + y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, true)
		{
			@Override
			protected void updateMessage()
			{
				this.setMessage(Component.literal("").append(prefix).append(valueStringifier.apply(this.getValue(), this.getValueString())).append(suffix));
				onChange.accept(minValue + (value * (maxValue - minValue)));
			}
			
			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers)
			{
				// We cannot support stepSizes of <= 0 because ExtendedSlider#setSliderValue is private
				if(stepSize <= 0D)
				{
					return false;
				}
				
				boolean left = keyCode == GLFW.GLFW_KEY_LEFT;
				if(left || keyCode == GLFW.GLFW_KEY_RIGHT)
				{
					if(minValue > maxValue)
					{
						left = !left;
					}
					float step = left ? -1 : 1;
					if(Screen.hasShiftDown())
					{
						step *= 10;
					}
					this.setValue(this.getValue() + step * stepSize);
				}
				
				return false;
			}
			
			@Override
			public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
			{
				// We cannot support stepSizes of <= 0 because ExtendedSlider#setSliderValue is private
				if(stepSize <= 0)
				{
					return false;
				}
				
				boolean left = scrollY < 0;
				if(minValue > maxValue)
				{
					left = !left;
				}
				float step = left ? -1 : 1;
				if(Screen.hasShiftDown())
				{
					step *= 10;
				}
				this.setValue(this.getValue() + step * stepSize);
				
				return true;
			}
		};
		widget.setTooltip(Tooltip.create(tooltip));
		this.addRenderableWidget(widget);
		return new LogicConfigButtonReference<Double>()
		{
			
			@Override
			public void setText(Component text)
			{
				widget.setMessage(text);
			}
			
			@Override
			public void setTooltip(Component tooltip)
			{
				widget.setTooltip(Tooltip.create(tooltip));
			}
			
			@Override
			public void setValue(Double value)
			{
				widget.setValue(value);
			}
		};
	}
	
	@Override
	public LogicConfigButtonReference addCheckbox(Component text, Component tooltip, int x, int y, boolean initialValue, Consumer<Boolean> onChange)
	{
		AtomicReference<Component> textReference = new AtomicReference<>(text);
		var button = Checkbox.builder(Component.empty(), Minecraft.getInstance().font)
				.tooltip(Tooltip.create(tooltip))
				.pos(configX + x, configY + y)
				.selected(initialValue)
				.onValueChange((__, value) -> onChange.accept(value))
				.build();
		this.addRenderableWidget(button);
		this.addRenderableOnly((graphics, mouseX, mouseY, partialTicks) ->
				graphics.drawString(Minecraft.getInstance().font, textReference.get(), configX + x + 21, configY + y + 8 - 3, 0xFFFFFF, false));
		return new LogicConfigButtonReference<Boolean>()
		{
			@Override
			public void setText(Component text)
			{
				textReference.set(text);
			}
			
			@Override
			public void setTooltip(Component tooltip)
			{
				button.setTooltip(Tooltip.create(tooltip));
			}
			
			@Override
			public void setValue(Boolean value)
			{
				if(value && !button.selected())
				{
					button.onPress();
				}
				else if(!value && button.selected())
				{
					button.onPress();
				}
			}
		};
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
