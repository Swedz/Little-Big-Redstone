package net.swedz.little_big_redstone.gui.logicconfig;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.client.model.logic.LogicItemModel;
import net.swedz.little_big_redstone.gui.logicconfig.widget.LogicConfigButton;
import net.swedz.little_big_redstone.gui.logicconfig.widget.TickableLogicConfigWidget;
import net.swedz.little_big_redstone.gui.logicconfig.widget.cycle.CycleLogicConfigButton;
import net.swedz.little_big_redstone.gui.logicconfig.widget.iconcycle.CheckboxState;
import net.swedz.little_big_redstone.gui.logicconfig.widget.iconcycle.IconCycleLogicConfigButton;
import net.swedz.little_big_redstone.gui.logicconfig.widget.iconcycle.IconCycleLogicConfigButtonIcon;
import net.swedz.little_big_redstone.gui.logicconfig.widget.slider.SliderLogicConfigWidget;
import net.swedz.little_big_redstone.gui.logicconfig.widget.textbox.TextBoxLogicConfigWidget;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.network.packet.ReturnToMicrochipMenuPacket;
import net.swedz.little_big_redstone.network.packet.WriteLogicConfigPacket;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class LogicConfigScreen extends AbstractContainerScreen<LogicConfigMenu> implements LogicConfigMenuBuilder
{
	private final int            color;
	private final LogicComponent logicComponent;
	private final ItemStack      logicStack;
	
	private int configX, configY, configWidth, configHeight;
	
	private final List<TickableLogicConfigWidget> tickableWidgets = Lists.newArrayList();
	
	public LogicConfigScreen(LogicConfigMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title, 176, 196);
		
		inventoryLabelY = imageHeight - 94;
		
		configWidth = 160 - 4;
		configHeight = 180 - 4;
		
		logicComponent = menu.logicComponent();
		var colorSet = LogicItemModel.get(logicComponent).colorPalette().getColorSet(logicComponent, menu.color());
		color = colorSet.foreground();
		
		logicStack = logicComponent.type().toStack(logicComponent);
		var stackComponent = logicStack.get(LBRComponents.LOGIC).copy();
		stackComponent.setColor(Optional.of(stackComponent.color().orElse(menu.color())));
		logicStack.set(LBRComponents.LOGIC, stackComponent);
	}
	
	@Override
	public <T> LogicConfigButtonReference<T> addCycleButton(Component name, Component tooltip, int x, int y, int width, int height, boolean displayOnlyValue, T initialValue, List<T> values, CycleLogicConfigButton.ValueStringifier<T> valueStringifier, Consumer<T> onChange)
	{
		var button = new CycleLogicConfigButton<>(configX + x, configY + y, width, height, color, name, displayOnlyValue, initialValue, values, valueStringifier, (__, value) -> onChange.accept(value));
		button.setTooltip(Tooltip.create(tooltip));
		this.addRenderableWidget(button);
		return new LogicConfigButtonReference<>()
		{
			@Override
			public LogicConfigButtonReference<T> setText(Component text)
			{
				button.setMessage(text);
				return this;
			}
			
			@Override
			public LogicConfigButtonReference<T> setTooltip(Component tooltip)
			{
				button.setTooltip(Tooltip.create(tooltip));
				return this;
			}
			
			@Override
			public T getValue()
			{
				return button.value();
			}
			
			@Override
			public LogicConfigButtonReference<T> setValue(T value)
			{
				button.setValue(value);
				return this;
			}
			
			@Override
			public boolean isActive()
			{
				return button.active;
			}
			
			@Override
			public LogicConfigButtonReference<T> setActive(boolean active)
			{
				button.active = active;
				return this;
			}
			
			@Override
			public boolean isVisible()
			{
				return button.visible;
			}
			
			@Override
			public LogicConfigButtonReference<T> setVisible(boolean visible)
			{
				button.visible = visible;
				return this;
			}
		};
	}
	
	@Override
	public LogicConfigButtonReference<Double> addSlider(Component prefix, Component suffix, Component tooltip, int x, int y, int width, int height, double minValue, double maxValue, double currentValue, double stepSize, int precision, SliderLogicConfigWidget.ValueStringifier valueStringifier, Consumer<Double> onChange)
	{
		var button = new SliderLogicConfigWidget(configX + x, configY + y, width, height, color, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, true, valueStringifier, (__, value) -> onChange.accept(value));
		button.setTooltip(Tooltip.create(tooltip));
		this.addRenderableWidget(button);
		return new LogicConfigButtonReference<>()
		{
			@Override
			public LogicConfigButtonReference<Double> setText(Component text)
			{
				button.setMessage(text);
				return this;
			}
			
			@Override
			public LogicConfigButtonReference<Double> setTooltip(Component tooltip)
			{
				button.setTooltip(Tooltip.create(tooltip));
				return this;
			}
			
			@Override
			public Double getValue()
			{
				return button.getValue();
			}
			
			@Override
			public LogicConfigButtonReference<Double> setValue(Double value)
			{
				button.setValue(value);
				return this;
			}
			
			@Override
			public boolean isActive()
			{
				return button.active;
			}
			
			@Override
			public LogicConfigButtonReference<Double> setActive(boolean active)
			{
				button.active = active;
				return this;
			}
			
			@Override
			public boolean isVisible()
			{
				return button.visible;
			}
			
			@Override
			public LogicConfigButtonReference<Double> setVisible(boolean visible)
			{
				button.visible = visible;
				return this;
			}
		};
	}
	
	@Override
	public LogicConfigButtonReference<CheckboxState> addCheckbox(Component text, Component tooltip, int x, int y, boolean initialValue, Consumer<Boolean> onChange)
	{
		var button = new IconCycleLogicConfigButton<>(configX + x, configY + y, color, LBR.id("textures/gui/slot_atlas.png"), initialValue ? CheckboxState.YES : CheckboxState.NO, Arrays.asList(CheckboxState.values()), (__, value) -> onChange.accept(value == CheckboxState.YES))
		{
			@Override
			protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
			{
				super.extractContents(graphics, mouseX, mouseY, partialTick);
				
				graphics.text(font, text, this.getX() + width + 6, Math.round(this.getY() + (18 / 2f) - (Minecraft.getInstance().font.lineHeight / 2f)), color, false);
			}
		};
		button.setTooltip(Tooltip.create(tooltip));
		this.addRenderableWidget(button);
		return new LogicConfigButtonReference<>()
		{
			@Override
			public LogicConfigButtonReference<CheckboxState> setText(Component text)
			{
				return this;
			}
			
			@Override
			public LogicConfigButtonReference<CheckboxState> setTooltip(Component tooltip)
			{
				button.setTooltip(Tooltip.create(tooltip));
				return this;
			}
			
			@Override
			public CheckboxState getValue()
			{
				return button.value();
			}
			
			@Override
			public LogicConfigButtonReference<CheckboxState> setValue(CheckboxState value)
			{
				button.setValue(value);
				return this;
			}
			
			@Override
			public boolean isActive()
			{
				return button.active;
			}
			
			@Override
			public LogicConfigButtonReference<CheckboxState> setActive(boolean active)
			{
				button.active = active;
				return this;
			}
			
			@Override
			public boolean isVisible()
			{
				return button.visible;
			}
			
			@Override
			public LogicConfigButtonReference<CheckboxState> setVisible(boolean visible)
			{
				button.visible = visible;
				return this;
			}
		};
	}
	
	@Override
	public <T extends IconCycleLogicConfigButtonIcon> LogicConfigButtonReference<T> addCycleButton(Component tooltip, int x, int y, Identifier atlas, T initialValue, List<T> values, Consumer<T> onChange)
	{
		var button = new IconCycleLogicConfigButton<>(configX + x, configY + y, color, atlas, initialValue, values, (__, value) -> onChange.accept(value));
		button.setTooltip(Tooltip.create(tooltip));
		this.addRenderableWidget(button);
		return new LogicConfigButtonReference<>()
		{
			@Override
			public LogicConfigButtonReference<T> setText(Component text)
			{
				return this;
			}
			
			@Override
			public LogicConfigButtonReference<T> setTooltip(Component tooltip)
			{
				button.setTooltip(Tooltip.create(tooltip));
				return this;
			}
			
			@Override
			public T getValue()
			{
				return button.value();
			}
			
			@Override
			public LogicConfigButtonReference<T> setValue(T value)
			{
				button.setValue(value);
				return this;
			}
			
			@Override
			public boolean isActive()
			{
				return button.active;
			}
			
			@Override
			public LogicConfigButtonReference<T> setActive(boolean active)
			{
				button.active = active;
				return this;
			}
			
			@Override
			public boolean isVisible()
			{
				return button.visible;
			}
			
			@Override
			public LogicConfigButtonReference<T> setVisible(boolean visible)
			{
				button.visible = visible;
				return this;
			}
		};
	}
	
	@Override
	public LogicConfigButtonReference<String> addTextBox(Component name, Component tooltip, int x, int y, int width, int height, String initialValue, int maxLength, Predicate<String> filter, Consumer<String> onChange)
	{
		var editBox = new TextBoxLogicConfigWidget(minecraft.font, configX + x, configY + y, width, height, color, name);
		editBox.setTooltip(Tooltip.create(tooltip));
		editBox.setValue(initialValue);
		editBox.setMaxLength(maxLength);
		editBox.setFilter((value) -> value != null && filter.test(value));
		editBox.setResponder(onChange);
		this.addRenderableWidget(editBox);
		tickableWidgets.add(editBox);
		return new LogicConfigButtonReference<>()
		{
			@Override
			public LogicConfigButtonReference<String> setText(Component text)
			{
				editBox.setMessage(text);
				return this;
			}
			
			@Override
			public LogicConfigButtonReference<String> setTooltip(Component tooltip)
			{
				editBox.setTooltip(Tooltip.create(tooltip));
				return this;
			}
			
			@Override
			public String getValue()
			{
				return editBox.getValue();
			}
			
			@Override
			public LogicConfigButtonReference<String> setValue(String value)
			{
				editBox.setValue(value);
				return this;
			}
			
			@Override
			public boolean isActive()
			{
				return editBox.isActive();
			}
			
			@Override
			public LogicConfigButtonReference<String> setActive(boolean active)
			{
				editBox.active = active;
				return this;
			}
			
			@Override
			public boolean isVisible()
			{
				return editBox.visible;
			}
			
			@Override
			public LogicConfigButtonReference<String> setVisible(boolean visible)
			{
				editBox.visible = visible;
				return this;
			}
		};
	}
	
	private void maybeClose()
	{
		if(menu.shouldClientClose())
		{
			Minecraft.getInstance().player.closeContainer();
		}
	}
	
	private void save()
	{
		new WriteLogicConfigPacket(logicComponent).sendToServer();
		
		this.maybeClose();
	}
	
	private void cancel()
	{
		ReturnToMicrochipMenuPacket.INSTANCE.sendToServer();
		
		this.maybeClose();
	}
	
	@Override
	protected void init()
	{
		super.init();
		configX = leftPos + 8 + 2;
		configY = topPos + 8 + 2;
		
		logicComponent.config().getMenuProvider().create(this, configWidth, configHeight);
		
		int buttonWidth = (configWidth / 2) - 4;
		
		this.addRenderableWidget(new LogicConfigButton(configX, configY + configHeight - 16, buttonWidth, 16, color, LBR.text().logicConfigButtonLabelSave(), (__) -> this.save()));
		
		this.addRenderableWidget(new LogicConfigButton(configX + configWidth - buttonWidth, configY + configHeight - 16, buttonWidth, 16, color, LBR.text().logicConfigButtonLabelCancel(), (__) -> this.cancel()));
	}
	
	@Override
	protected void containerTick()
	{
		tickableWidgets.forEach(TickableLogicConfigWidget::tick);
	}
	
	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
	{
	}
	
	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
	{
		graphics.blit(LBR.id("textures/gui/container/logic_config/background.png"), leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		graphics.blit(LBR.id("textures/gui/container/logic_config/item_background.png"), leftPos - 61, topPos, 0, 0, imageWidth, imageHeight);
		
		graphics.pose().pushMatrix();
		graphics.pose().translate(leftPos - 45, topPos + 18);
		graphics.pose().scale(2, 2);
		graphics.item(logicStack, 0, 0);
		graphics.pose().popMatrix();
	}
	
	@Override
	public boolean keyPressed(KeyEvent event)
	{
		var mouseKey = InputConstants.getKey(event);
		if(minecraft.options.keyInventory.isActiveAndMatches(mouseKey) &&
		   this.getFocused() != null)
		{
			return true;
		}
		return super.keyPressed(event);
	}
	
	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY)
	{
		return this.getFocused() != null && this.isDragging() && event.button() == InputConstants.MOUSE_BUTTON_LEFT ?
				this.getFocused().mouseDragged(event, dragX, dragY) :
				super.mouseDragged(event, dragX, dragY);
	}
}
