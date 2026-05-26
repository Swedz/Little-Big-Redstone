package net.swedz.little_big_redstone.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.recipe.DataRetainingDyeRecipe;
import net.swedz.tesseract.neoforge.helper.RegistryHelper;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class DataRetainingDyeEmiRecipe extends EmiPatternCraftingRecipe
{
	private static final List<DyeItem> DYES = Stream.of(DyeColor.values()).map(DyeItem::byColor).toList();
	
	private final List<Item> inputItems;
	
	public DataRetainingDyeEmiRecipe(TagKey<Item> inputItemTag, Identifier id)
	{
		super(
				List.of(
						EmiIngredient.of(DYES.stream().map(EmiStack::of).toList()),
						EmiIngredient.of(inputItemTag)
				),
				EmiStack.EMPTY,
				id
		);
		this.inputItems = RegistryHelper.values(Minecraft.getInstance().level.registryAccess(), inputItemTag).map(Holder::value).toList();
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
	
	private DyeItem getDye(MutableInt index)
	{
		var inputDye = DYES.get(index.getAndIncrement());
		if(index.getValue() >= DYES.size())
		{
			index.setValue(0);
		}
		return inputDye;
	}
	
	@Override
	public SlotWidget getInputWidget(int slot, int x, int y)
	{
		if(slot == 0)
		{
			MutableInt index = new MutableInt();
			return new GeneratedSlotWidget(
					(random) -> EmiStack.of(this.getItem(index)),
					unique, x, y
			);
		}
		else if(slot == 1)
		{
			MutableInt index = new MutableInt();
			return new GeneratedSlotWidget(
					(random) -> EmiStack.of(this.getDye(index)),
					unique, x, y
			);
		}
		return new SlotWidget(EmiStack.EMPTY, x, y);
	}
	
	@Override
	public SlotWidget getOutputWidget(int x, int y)
	{
		MutableInt itemIndex = new MutableInt();
		MutableInt dyeIndex = new MutableInt();
		return new GeneratedSlotWidget(
				(random) ->
				{
					var inputItem = this.getItem(itemIndex);
					var dyeItem = this.getDye(dyeIndex);
					return EmiStack.of(DataRetainingDyeRecipe.outputVariant(new ItemStack(inputItem), Optional.of(dyeItem.getDyeColor())));
				},
				unique, x, y
		);
	}
}
