package net.swedz.little_big_redstone.client.model.stickynote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRComponents;

public record SelectItemStickyNoteType() implements SelectItemModelProperty<StickyNoteEntityModelData>
{
	public static final Type<SelectItemStickyNoteType, StickyNoteEntityModelData> TYPE = Type.create(
			MapCodec.unit(new SelectItemStickyNoteType()),
			StickyNoteEntityModelData.CODEC
	);
	
	@Override
	public StickyNoteEntityModelData get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext context)
	{
		return context == ItemDisplayContext.NONE ?
				stack.get(LBRComponents.STICKY_NOTE_ENTITY_MODEL_DATA) :
				null;
	}
	
	@Override
	public Codec<StickyNoteEntityModelData> valueCodec()
	{
		return StickyNoteEntityModelData.CODEC;
	}
	
	@Override
	public Type<SelectItemStickyNoteType, StickyNoteEntityModelData> type()
	{
		return TYPE;
	}
}
