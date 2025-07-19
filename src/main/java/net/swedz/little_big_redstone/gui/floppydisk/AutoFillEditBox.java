package net.swedz.little_big_redstone.gui.floppydisk;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class AutoFillEditBox extends EditBox
{
	private final Supplier<List<String>> fillOptions;
	private final int                    optionsToDisplay;
	
	private List<String> matchingFillOptions = List.of();
	
	private int viewOptionIndex        = 0;
	private int highlightedOptionIndex = 0;
	
	public AutoFillEditBox(Font font, int width, int height, Component message,
						   Supplier<List<String>> fillOptions, int optionsToDisplay)
	{
		super(font, width, height, message);
		this.fillOptions = fillOptions;
		this.optionsToDisplay = optionsToDisplay;
		this.rebuildMatchingFillOptions();
	}
	
	public AutoFillEditBox(Font font, int x, int y, int width, int height, Component message,
						   Supplier<List<String>> fillOptions, int optionsToDisplay)
	{
		super(font, x, y, width, height, message);
		this.fillOptions = fillOptions;
		this.optionsToDisplay = optionsToDisplay;
		this.rebuildMatchingFillOptions();
	}
	
	public AutoFillEditBox(Font font, int x, int y, int width, int height, AutoFillEditBox editBox, Component message,
						   Supplier<List<String>> fillOptions, int optionsToDisplay)
	{
		super(font, x, y, width, height, editBox, message);
		this.fillOptions = fillOptions;
		this.optionsToDisplay = optionsToDisplay;
		this.rebuildMatchingFillOptions();
	}
	
	public final List<String> getFillOptions()
	{
		return fillOptions.get();
	}
	
	public final int getOptionsToDisplay()
	{
		return optionsToDisplay;
	}
	
	public final List<String> getMatchingFillOptions()
	{
		return matchingFillOptions;
	}
	
	private List<String> filterMatchingFillOptions(String text)
	{
		List<String> matchingFillOptions = Lists.newArrayList();
		for(String option : this.getFillOptions())
		{
			if(option.startsWith(text) && !option.equals(text))
			{
				matchingFillOptions.add(option);
			}
		}
		Collections.sort(matchingFillOptions);
		return Collections.unmodifiableList(matchingFillOptions);
	}
	
	private void updateSuggestion()
	{
		if(highlightedOptionIndex > -1 && matchingFillOptions.size() > highlightedOptionIndex)
		{
			this.setSuggestion(matchingFillOptions.get(highlightedOptionIndex).substring(this.getValue().length()));
		}
		else
		{
			this.setSuggestion(null);
		}
	}
	
	private void rebuildMatchingFillOptions()
	{
		matchingFillOptions = this.filterMatchingFillOptions(this.getValue());
		viewOptionIndex = 0;
		highlightedOptionIndex = 0;
		this.updateSuggestion();
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if(keyCode == GLFW.GLFW_KEY_UP)
		{
			highlightedOptionIndex = Math.max(0, highlightedOptionIndex - 1);
			if(highlightedOptionIndex < viewOptionIndex)
			{
				viewOptionIndex = highlightedOptionIndex;
			}
			this.updateSuggestion();
			return true;
		}
		else if(keyCode == GLFW.GLFW_KEY_DOWN)
		{
			highlightedOptionIndex = Math.min(matchingFillOptions.size() - 1, highlightedOptionIndex + 1);
			if(highlightedOptionIndex > viewOptionIndex + optionsToDisplay - 1)
			{
				viewOptionIndex++;
			}
			this.updateSuggestion();
			return true;
		}
		else if(keyCode == GLFW.GLFW_KEY_TAB)
		{
			if(matchingFillOptions.size() > highlightedOptionIndex)
			{
				this.setValue(matchingFillOptions.get(highlightedOptionIndex));
			}
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	protected void onValueChange(String newText)
	{
		this.rebuildMatchingFillOptions();
		super.onValueChange(newText);
	}
	
	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		super.renderWidget(graphics, mouseX, mouseY, partialTick);
		
		if(this.isVisible() && this.isFocused() && !matchingFillOptions.isEmpty())
		{
			int endIndex = Math.min(matchingFillOptions.size(), viewOptionIndex + optionsToDisplay);
			
			int backgroundWidth = 0;
			for(int index = viewOptionIndex; index < endIndex; index++)
			{
				String option = matchingFillOptions.get(index);
				int optionWidth = Minecraft.getInstance().font.width(option) + 8;
				if(optionWidth > backgroundWidth)
				{
					backgroundWidth = optionWidth;
				}
			}
			graphics.fill(this.getX(), this.getBottom(), this.getX() + backgroundWidth, this.getBottom() + (Math.min(matchingFillOptions.size(), optionsToDisplay) * (Minecraft.getInstance().font.lineHeight + 4)) + 4, 0xAA000000);
			
			int lineX = this.isBordered() ? (this.getX() + 4) : this.getX();
			int lineY = this.getBottom() + 4;
			for(int index = viewOptionIndex; index < endIndex; index++)
			{
				String option = matchingFillOptions.get(index);
				graphics.drawString(Minecraft.getInstance().font, option, lineX, lineY, highlightedOptionIndex == index ? textColor : textColorUneditable);
				
				lineY += Minecraft.getInstance().font.lineHeight + 4;
			}
		}
	}
}
