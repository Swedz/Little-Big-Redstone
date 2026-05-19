package net.swedz.little_big_redstone.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.tesseract.neoforge.helper.RegistryHelper;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public final class SealStickyNoteEmiRecipe extends EmiPatternCraftingRecipe
{
	private final List<Item> inputItems;
	
	public SealStickyNoteEmiRecipe(ResourceLocation id)
	{
		super(
				List.of(
						EmiIngredient.of(LBRTags.Items.STICKY_NOTES),
						EmiIngredient.of(Ingredient.of(Items.HONEYCOMB))
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
		if(slot == 0)
		{
			MutableInt itemIndex = new MutableInt();
			return new GeneratedSlotWidget(
					(random) -> EmiStack.of(this.getItem(itemIndex)),
					unique, x, y
			);
		}
		else if(slot == 1)
		{
			return new SlotWidget(EmiIngredient.of(Ingredient.of(Items.HONEYCOMB)), x, y);
		}
		return new SlotWidget(EmiStack.EMPTY, x, y);
	}
	
	@Override
	public SlotWidget getOutputWidget(int x, int y)
	{
		MutableInt itemIndex = new MutableInt();
		return new GeneratedSlotWidget(
				(random) ->
				{
					var result = this.getItem(itemIndex).getDefaultInstance().copyWithCount(1);
					result.set(LBRComponents.STICKY_NOTE_EDITABLE, false);
					return EmiStack.of(result);
				},
				unique, x, y
		);
	}
}
