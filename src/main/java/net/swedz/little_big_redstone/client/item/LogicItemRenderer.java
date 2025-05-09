package net.swedz.little_big_redstone.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientRenderTypes;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.client.model.logic.LogicBakedModel;

import java.util.Collection;

/**
 * <p>I am not happy that I had to do this.</p>
 *
 * <p>For some ineffable reason, passing the desired {@link RenderType} in
 * {@link CompositeModel.Baked.Builder#addQuads(RenderTypeGroup, Collection)} does not properly apply the
 * {@link RenderType} to the model when rendering. Because of that, I have to manually apply the {@link RenderType} to
 * the parts of the model that I want it on.</p>
 */
public final class LogicItemRenderer extends BlockEntityWithoutLevelRenderer
{
	private final RandomSource random;
	
	public LogicItemRenderer()
	{
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
		random = RandomSource.create();
	}
	
	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
	{
		var component = stack.get(LBRComponents.LOGIC);
		var model = ((LogicBakedModel) Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.inventory(LBR.id(component.type().id())))).getModel(component);
		
		int index = 0;
		for(var modelLayer : model.getRenderPasses(stack, false))
		{
			var renderType = index == 2 ? LBRClientRenderTypes.logicScanline() : NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get();
			var buffer = bufferSource.getBuffer(renderType);
			for(var quad : modelLayer.getQuads(null, null, random, ModelData.EMPTY, null))
			{
				float red = 1f;
				float green = 1f;
				float blue = 1f;
				float alpha = 1f;
				if(quad.isTinted())
				{
					int tintIndex = quad.getTintIndex();
					int abgr = quad.getVertices()[tintIndex * IQuadTransformer.STRIDE + IQuadTransformer.COLOR];
					red = FastColor.ABGR32.red(abgr) / 255f;
					green = FastColor.ABGR32.green(abgr) / 255f;
					blue = FastColor.ABGR32.blue(abgr) / 255f;
					alpha = FastColor.ABGR32.alpha(abgr) / 255f;
				}
				buffer.putBulkData(poseStack.last(), quad, red, green, blue, alpha, packedLight, packedOverlay);
			}
			index++;
		}
	}
}
