package net.swedz.little_big_redstone.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.tesseract.neoforge.helper.RegistryHelper;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public final class CopyStickyNoteEmiRecipe extends EmiPatternCraftingRecipe
{
	private final List<Item> inputItems;
	
	public CopyStickyNoteEmiRecipe(Identifier id)
	{
		super(
				List.of(
						EmiIngredient.of(LBRTags.Items.STICKY_NOTES)
				),
				EmiStack.EMPTY,
				id
		);
		this.inputItems = RegistryHelper.values(Minecraft.getInstance().level.registryAccess(), LBRTags.Items.STICKY_NOTES).map(Holder::value).toList();
	}
	
	private Item getItem(MutableInt index)
	{
		var item = inputItems.get(index.getAndIncrement());
		if(index.getValue() >= inputItems.size())
		{
			index.setValue(0);
		}
		return item;
	}
	
	@Override
	public SlotWidget getInputWidget(int slot, int x, int y)
	{
		if(slot == 0 || slot == 1)
		{
			MutableInt itemIndex = new MutableInt();
			return new GeneratedSlotWidget(
					(random) -> EmiStack.of(this.getItem(itemIndex)),
					unique, x, y
			);
		}
		return new SlotWidget(EmiStack.EMPTY, x, y);
	}
	
	@Override
	public SlotWidget getOutputWidget(int x, int y)
	{
		MutableInt itemIndex = new MutableInt();
		return new GeneratedSlotWidget(
				(random) -> EmiStack.of(this.getItem(itemIndex).getDefaultInstance().copyWithCount(2)),
				unique, x, y
		);
	}
}
