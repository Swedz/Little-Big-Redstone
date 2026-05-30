package net.swedz.little_big_redstone.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.swedz.little_big_redstone.entity.stickynote.StickyNoteEntity;

public final class StickyNoteEntityRenderer<T extends StickyNoteEntity> extends EntityRenderer<T, StickyNoteEntityRenderer.RenderState>
{
	private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
	
	private final RandomSource       random;
	private final BlockModelResolver blockModelResolver;
	private final ItemModelResolver  itemModelResolver;
	
	public StickyNoteEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		random = RandomSource.create();
		blockModelResolver = context.getBlockModelResolver();
		itemModelResolver = context.getItemModelResolver();
	}
	
	@Override
	public StickyNoteEntityRenderer.RenderState createRenderState()
	{
		return new StickyNoteEntityRenderer.RenderState();
	}
	
	public static final class RenderState extends EntityRenderState
	{
		public final BlockModelRenderState stickyNoteModel = new BlockModelRenderState();
		public final ItemStackRenderState  itemModel       = new ItemStackRenderState();
		
		public Direction direction;
		public Direction facing;
		public float     xRot;
		public float     yRot;
	}
	
	@Override
	public void submit(
			RenderState state,
			PoseStack pose,
			SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera
	)
	{
		super.submit(state, pose, submitNodeCollector, camera);
		
		var attachedFace = state.direction;
		
		pose.pushPose();
		
		pose.mulPose(Axis.XP.rotationDegrees(state.xRot));
		pose.mulPose(Axis.YP.rotationDegrees(180 - state.yRot));
		if(attachedFace.getAxis().isVertical())
		{
			boolean up = attachedFace == Direction.UP;
			pose.mulPose(Axis.ZP.rotationDegrees(state.facing.toYRot() * (up ? 1 : -1)));
		}
		
		if(!state.stickyNoteModel.isEmpty())
		{
			state.stickyNoteModel.submit(pose, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		}
		
		pose.popPose();
		
		if(!state.itemModel.isEmpty())
		{
			this.submitDisplayItem(state, pose, submitNodeCollector, camera);
		}
	}
	
	private void submitDisplayItem(
			RenderState state,
			PoseStack pose,
			SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera
	)
	{
		var attachedFace = state.direction;
		
		pose.pushPose();
		
		pose.mulPose(Axis.XP.rotationDegrees(state.xRot));
		pose.mulPose(Axis.YN.rotationDegrees(state.yRot));
		if(attachedFace.getAxis().isVertical())
		{
			boolean up = attachedFace == Direction.UP;
			pose.mulPose(Axis.ZP.rotationDegrees(state.facing.toYRot() * (up ? -1 : 1)));
		}
		
		pose.translate(0, -0.03125f, -0.00625f);
		pose.scale(0.25f, 0.25f, 0.01f);
		
		// TODO 26.1 tint item
		if(!state.itemModel.isEmpty())
		{
			state.itemModel.submit(pose, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		}
		
		pose.popPose();
	}
	
	@Override
	public void extractRenderState(
			T entity,
			RenderState state,
			float partialTicks
	)
	{
		super.extractRenderState(entity, state, partialTicks);
		
		state.direction = entity.getDirection();
		state.facing = entity.getFacing();
		state.xRot = entity.getXRot();
		state.yRot = entity.getYRot();
		
		this.updateForStickyNote(state.stickyNoteModel, entity.getColor(), entity.getTextColor(), entity.hasText() && entity.getDisplayItem().isEmpty());
		
		itemModelResolver.updateForNonLiving(state.itemModel, entity.getDisplayItem(), ItemDisplayContext.GUI, entity);
	}
	
	private void updateForStickyNote(BlockModelRenderState renderState, DyeColor paperColor, DyeColor textColor, boolean showText)
	{
		var fakeState = StickyNoteEntity.fakeBlockState(paperColor, textColor, showText);
		blockModelResolver.update(renderState, fakeState, BLOCK_DISPLAY_CONTEXT);
	}
}
