package net.swedz.little_big_redstone.guide.tags.scene;

import guideme.color.ConstantColor;
import guideme.color.MutableColor;
import guideme.document.interaction.TextTooltip;
import guideme.scene.ImplicitAnnotationStrategy;
import guideme.scene.annotation.InWorldBoxAnnotation;
import guideme.scene.annotation.SceneAnnotation;
import guideme.scene.level.GuidebookLevel;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.swedz.little_big_redstone.LBR;

public final class InputOutputImplicitAnnotationStrategy implements ImplicitAnnotationStrategy
{
	@Override
	public SceneAnnotation getAnnotation(GuidebookLevel level, BlockState state, BlockHitResult hitResult)
	{
		if(state.is(Blocks.REDSTONE_WIRE))
		{
			state = level.getBlockState(hitResult.getBlockPos().below());
		}
		int colorRGB;
		MutableComponent tooltip;
		if(state.is(Blocks.LIGHT_BLUE_CONCRETE))
		{
			colorRGB = 0x258AC8;
			tooltip = LBR.text().guideTooltipInputA();
		}
		else if(state.is(Blocks.CYAN_CONCRETE))
		{
			colorRGB = 0x258AC8;
			tooltip = LBR.text().guideTooltipInputB();
		}
		else if(state.is(Blocks.ORANGE_CONCRETE))
		{
			colorRGB = 0xE16302;
			tooltip = LBR.text().guideTooltipOutput();
		}
		else
		{
			return null;
		}
		var color = MutableColor.of(new ConstantColor(0xFF000000 | colorRGB), null);
		var annotation = InWorldBoxAnnotation.forBlock(hitResult.getBlockPos(), color.copy());
		color.lighter(50);
		annotation.setTooltip(new TextTooltip(tooltip.withColor(color.resolve(null))));
		return annotation;
	}
}
