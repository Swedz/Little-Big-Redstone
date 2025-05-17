package net.swedz.little_big_redstone.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.swedz.little_big_redstone.LBRItemDisplayContext;
import net.swedz.little_big_redstone.client.model.MicrochipGuiContextModelData;
import net.swedz.little_big_redstone.client.model.stickynote.StickyNoteModelData;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.tesseract.neoforge.helper.QuadColorFix;

public final class StickyNoteItemRenderer extends BlockEntityWithoutLevelRenderer
{
	private final RandomSource random;
	private final ItemRenderer itemRenderer;
	
	public StickyNoteItemRenderer()
	{
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
		random = RandomSource.create();
		itemRenderer = Minecraft.getInstance().getItemRenderer();
	}
	
	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
	{
		if(!(stack.getItem() instanceof StickyNoteItem stickyNoteItem))
		{
			return;
		}
		
		var modelData = ModelData.builder()
				.with(StickyNoteModelData.KEY, StickyNoteModelData.of(stack))
				.with(MicrochipGuiContextModelData.KEY, new MicrochipGuiContextModelData(displayContext == LBRItemDisplayContext.MICROCHIP_GUI))
				.build();
		
		var model = itemRenderer.getModel(stack, null, null, 0);
		
		var buffer = bufferSource.getBuffer(NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
		for(var quad : model.getQuads(null, null, random, modelData, null))
		{
			QuadColorFix.putBulkData(buffer, poseStack.last(), quad, packedLight, packedOverlay);
		}
	}
}
