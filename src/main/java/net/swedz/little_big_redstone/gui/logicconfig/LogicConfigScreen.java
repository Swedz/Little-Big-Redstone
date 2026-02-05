package net.swedz.little_big_redstone.gui.logicconfig;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.client.model.logic.LogicBakingModelData;
import net.swedz.little_big_redstone.gui.logicconfig.widget.LogicConfigButton;
import net.swedz.little_big_redstone.gui.logicconfig.widget.TickableLogicConfigWidget;
import net.swedz.little_big_redstone.gui.logicconfig.widget.cycle.CycleLogicConfigButton;
import net.swedz.little_big_redstone.gui.logicconfig.widget.iconcycle.CheckboxState;
import net.swedz.little_big_redstone.gui.logicconfig.widget.iconcycle.IconCycleLogicConfigButton;
import net.swedz.little_big_redstone.gui.logicconfig.widget.iconcycle.IconCycleLogicConfigButtonIcon;
import net.swedz.little_big_redstone.gui.logicconfig.widget.slider.SliderLogicConfigWidget;
import net.swedz.little_big_redstone.gui.logicconfig.widget.textbox.TextBoxLogicConfigWidget;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigButtonReference;
import net.swedz.little_big_redstone.microchip.object.logic.config.menu.LogicConfigMenuBuilder;
import net.swedz.little_big_redstone.network.packet.RequestMicrochipMenuPacket;
import net.swedz.little_big_redstone.network.packet.WriteLogicConfigPacket;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class LogicConfigScreen extends AbstractContainerScreen<LogicConfigMenu> implements LogicConfigMenuBuilder
{
	private final int        color;
	private final LogicEntry logicEntry;
	private final ItemStack  logicStack;
	
	private int configX, configY, configWidth, configHeight;
	
	private final List<TickableLogicConfigWidget> tickableWidgets = Lists.newArrayList();
	
	public LogicConfigScreen(LogicConfigMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		imageWidth = 176;
		imageHeight = 196;
		inventoryLabelY = imageHeight - 94;
		
		configWidth = 160 - 4;
		configHeight = 180 - 4;
		
		var component = menu.logicEntry().component();
		var colorSet = LogicBakingModelData.get(component).getColorSet(component, menu.color());
		color = colorSet.foreground();
		logicEntry = menu.logicEntry();
		
		logicStack = logicEntry.toStack();
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
			protected void renderWidget(GuiGraphics internal, int mouseX, int mouseY, float partialTick)
			{
				super.renderWidget(internal, mouseX, mouseY, partialTick);
				
				var graphics = new TesseractGuiGraphics(internal);
				graphics.setColor(color);
				graphics.setStringDropShadow(false);
				graphics.drawString(text, this.getX() + width + 6, this.getY() + (18 / 2f) - (Minecraft.getInstance().font.lineHeight / 2f));
				graphics.resetColor();
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
	public <T extends IconCycleLogicConfigButtonIcon> LogicConfigButtonReference<T> addCycleButton(Component tooltip, int x, int y, ResourceLocation atlas, T initialValue, List<T> values, Consumer<T> onChange)
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
	
	private void save()
	{
		new WriteLogicConfigPacket(menu.blockPos(), logicEntry.slot(), logicEntry.component(), menu.returnViewPosition()).sendToServer();
	}
	
	private void cancel()
	{
		new RequestMicrochipMenuPacket(menu.blockPos(), menu.returnViewPosition()).sendToServer();
	}
	
	@Override
	protected void init()
	{
		super.init();
		configX = leftPos + 8 + 2;
		configY = topPos + 8 + 2;
		
		logicEntry.component().config().getMenuProvider().create(this, configWidth, configHeight);
		
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
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
	{
		graphics.blit(LBR.id("textures/gui/container/logic_config/background.png"), leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		graphics.blit(LBR.id("textures/gui/container/logic_config/item_background.png"), leftPos - 61, topPos, 0, 0, imageWidth, imageHeight);
		
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos - 45, topPos + 18, 0);
		graphics.pose().scale(2, 2, 1);
		graphics.renderItem(logicStack, 0, 0);
		graphics.pose().popPose();
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		var mouseKey = InputConstants.getKey(keyCode, scanCode);
		if(minecraft.options.keyInventory.isActiveAndMatches(mouseKey) &&
		   this.getFocused() != null)
		{
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
	{
		return this.getFocused() != null && this.isDragging() && button == 0 ?
				this.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY) :
				super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}
}
