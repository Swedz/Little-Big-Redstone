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
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.tesseract.neoforge.helper.RegistryHelper;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public final class SealStickyNoteEmiRecipe extends EmiPatternCraftingRecipe
{
	private final List<Item> stickyNotes;
	private final List<Item> sealants;
	
	public SealStickyNoteEmiRecipe(Identifier id)
	{
		super(
				List.of(
						EmiIngredient.of(LBRTags.Items.STICKY_NOTES),
						EmiIngredient.of(LBRTags.Items.STICKY_NOTE_SEALANT)
				),
				EmiStack.EMPTY,
				id
		);
		this.stickyNotes = RegistryHelper.values(Minecraft.getInstance().level.registryAccess(), LBRTags.Items.STICKY_NOTES).map(Holder::value).toList();
		this.sealants = RegistryHelper.values(Minecraft.getInstance().level.registryAccess(), LBRTags.Items.STICKY_NOTE_SEALANT).map(Holder::value).toList();
	}
	
	private Item getItem(List<Item> items, MutableInt index)
	{
		var item = items.get(index.getAndIncrement());
		if(index.getValue() >= items.size())
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
					(random) -> EmiStack.of(this.getItem(stickyNotes, itemIndex)),
					unique, x, y
			);
		}
		else if(slot == 1)
		{
			MutableInt itemIndex = new MutableInt();
			return new GeneratedSlotWidget(
					(random) -> EmiStack.of(this.getItem(sealants, itemIndex)),
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
				(random) ->
				{
					var result = this.getItem(stickyNotes, itemIndex).getDefaultInstance().copyWithCount(1);
					result.set(LBRComponents.STICKY_NOTE_EDITABLE, false);
					return EmiStack.of(result);
				},
				unique, x, y
		);
	}
}
