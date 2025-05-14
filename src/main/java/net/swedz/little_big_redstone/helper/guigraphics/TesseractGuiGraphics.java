package net.swedz.little_big_redstone.helper.guigraphics;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swedz.tesseract.neoforge.api.Assert;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// TODO move to Tesseract
public final class TesseractGuiGraphics implements BlitGuiGraphics, FillGuiGraphics, StringGuiGraphics, TooltipGuiGraphics, ItemGuiGraphics
{
	private final TesseractGuiGraphics parent;
	private final GuiGraphics          vanilla;
	
	private boolean             batching;
	private List<Integer>       batchOrder     = Lists.newArrayList();
	private Map<Integer, Batch> batches        = Maps.newHashMap();
	private List<Runnable>      delayedRenders = Lists.newArrayList();
	
	private record Batch(ResourceLocation[] textures, TextureShaderConfiguration shader, List<DrawAction> draws)
	{
	}
	
	private int[] color     = new int[]{255, 255, 255, 255};
	private int[] lastColor = new int[]{255, 255, 255, 255};
	
	private TextureShaderConfiguration textureShader = TextureShaderConfiguration.DEFAULT;
	private ResourceLocation[]         textures      = new ResourceLocation[]{MissingTextureAtlasSprite.getLocation()};
	private List<DrawAction>           textureBatch;
	
	private Font font = Minecraft.getInstance().font;
	
	private TesseractGuiGraphics(TesseractGuiGraphics parent, GuiGraphics vanilla)
	{
		this.parent = parent;
		this.vanilla = vanilla;
	}
	
	public TesseractGuiGraphics(GuiGraphics vanilla)
	{
		this(null, vanilla);
	}
	
	/**
	 * Returns the vanilla {@link GuiGraphics} instance wrapped by this instance.
	 *
	 * @return the vanilla {@link GuiGraphics}
	 * @deprecated Try to avoid using this as much as possible. If there is something needed in {@link GuiGraphics} not
	 * supported by this wrapper, add that support instead!
	 */
	@Deprecated
	public GuiGraphics vanilla()
	{
		return vanilla;
	}
	
	@Override
	public int guiWidth()
	{
		return vanilla.guiWidth();
	}
	
	@Override
	public int guiHeight()
	{
		return vanilla.guiHeight();
	}
	
	public PoseStack pose()
	{
		return vanilla.pose();
	}
	
	public MultiBufferSource.BufferSource bufferSource()
	{
		return vanilla.bufferSource();
	}
	
	public TesseractGuiGraphics inner()
	{
		return new TesseractGuiGraphics(this, vanilla);
	}
	
	public TesseractGuiGraphics end()
	{
		Assert.notNull(parent, "Cannot close outermost graphic instance");
		if(batching)
		{
			parent.delayed(this::drawBatches);
		}
		return parent;
	}
	
	public void enableBatching()
	{
		batching = true;
	}
	
	private void disableBatching()
	{
		batching = false;
		batchOrder = Lists.newArrayList();
		batches = Maps.newHashMap();
		delayedRenders = Lists.newArrayList();
	}
	
	public void drawBatches()
	{
		Assert.that(batching, "Batching has not been enabled");
		
		// Set batching to false so that any delayed renders that contain batching conditionals do not try to add to a batch that no longer exists
		batching = false;
		
		for(var texture : batchOrder)
		{
			var batchInstance = batches.get(texture);
			var draws = batchInstance.draws();
			if(!draws.isEmpty())
			{
				var shader = batchInstance.shader();
				var batch = GuiGraphicsBatch.start(vanilla, batchInstance.textures(), shader.shader(), shader.mode(), shader.format(), shader.extraSetup());
				for(var draw : draws)
				{
					draw.addVertexes(batch);
				}
				batch.end();
			}
		}
		
		for(var render : delayedRenders)
		{
			render.run();
		}
		
		this.disableBatching();
	}
	
	@Override
	public int[] getColor()
	{
		return color;
	}
	
	@Override
	public void setColorInt(int red, int green, int blue, int alpha)
	{
		lastColor = color;
		color = new int[]{red, green, blue, alpha};
	}
	
	@Override
	public void revertColor()
	{
		color = lastColor;
		lastColor = new int[]{255, 255, 255, 255};
	}
	
	@Override
	public TextureShaderConfiguration getTextureShader()
	{
		return textureShader;
	}
	
	@Override
	public void setTextureShader(TextureShaderConfiguration textureShader)
	{
		if(textureShader == null)
		{
			textureShader = TextureShaderConfiguration.DEFAULT;
		}
		this.textureShader = textureShader;
	}
	
	@Override
	public ResourceLocation[] getTextures()
	{
		return textures;
	}
	
	@Override
	public void setTextures(ResourceLocation... textures)
	{
		if(textures == null)
		{
			textures = new ResourceLocation[]{MissingTextureAtlasSprite.getLocation()};
		}
		else
		{
			for(int i = 0; i < textures.length; i++)
			{
				if(textures[i] == null)
				{
					textures[i] = MissingTextureAtlasSprite.getLocation();
				}
			}
		}
		this.textures = textures;
		textureBatch = null;
	}
	
	private void addToTextureBatch(DrawAction draw)
	{
		Assert.that(batching, "Batching has not been enabled");
		
		if(textureBatch == null)
		{
			int textureIndex = Arrays.hashCode(textures);
			var batchInstance = batches.get(textureIndex);
			List<DrawAction> draws;
			if(batchInstance == null)
			{
				draws = Lists.newArrayList();
				batchOrder.add(textureIndex);
				batches.put(textureIndex, new Batch(textures, textureShader, draws));
			}
			else
			{
				draws = batchInstance.draws();
			}
			textureBatch = draws;
		}
		
		textureBatch.add(draw);
	}
	
	@Override
	public Font getFont()
	{
		return font;
	}
	
	@Override
	public void setFont(Font font)
	{
		if(font == null)
		{
			font = Minecraft.getInstance().font;
		}
		this.font = font;
	}
	
	public void delayed(Runnable runnable)
	{
		if(batching)
		{
			delayedRenders.add(runnable);
		}
		else
		{
			runnable.run();
		}
	}
	
	@ApiStatus.Internal
	@Override
	public void innerBlit(int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV)
	{
		int red = color[0];
		int green = color[1];
		int blue = color[2];
		int alpha = color[3];
		
		final DrawAction draw = (pose, buffer) ->
		{
			buffer.addVertex(pose, (float) x1, (float) y1, (float) blitOffset).setUv(minU, minV).setColor(red, green, blue, alpha);
			buffer.addVertex(pose, (float) x1, (float) y2, (float) blitOffset).setUv(minU, maxV).setColor(red, green, blue, alpha);
			buffer.addVertex(pose, (float) x2, (float) y2, (float) blitOffset).setUv(maxU, maxV).setColor(red, green, blue, alpha);
			buffer.addVertex(pose, (float) x2, (float) y1, (float) blitOffset).setUv(maxU, minV).setColor(red, green, blue, alpha);
		};
		
		if(batching)
		{
			this.addToTextureBatch(draw);
		}
		else
		{
			var batch = GuiGraphicsBatch.start(vanilla, textures, textureShader.shader(), textureShader.mode(), textureShader.format(), textureShader.extraSetup());
			draw.addVertexes(batch);
			batch.end();
		}
	}
	
	@Override
	public void fill(RenderType renderType, int minX, int minY, int maxX, int maxY, int z)
	{
		int color = this.getColorARGB();
		this.delayed(() -> vanilla.fill(renderType, minX, minY, maxX, maxY, z, color));
	}
	
	@Override
	public int drawString(String text, float x, float y, boolean dropShadow)
	{
		return vanilla.drawString(font, text, x, y, this.getColorARGB(), dropShadow);
	}
	
	@Override
	public int drawString(FormattedCharSequence text, float x, float y, boolean dropShadow)
	{
		return vanilla.drawString(font, text, x, y, this.getColorARGB(), dropShadow);
	}
	
	@ApiStatus.Internal
	@Override
	public void renderTooltipInternal(List<ClientTooltipComponent> lines, int x, int y, int width, int height, int backgroundTopColor, int backgroundBottomColor, int borderTopColor, int borderBottomColor)
	{
		TooltipRenderUtil.renderTooltipBackground(vanilla, x, y, width, height, 400, backgroundTopColor, backgroundBottomColor, borderTopColor, borderBottomColor);
		
		vanilla.pose().pushPose();
		vanilla.pose().translate(0, 0, 400);
		
		int textY = y;
		int lineIndex = 0;
		for(var line : lines)
		{
			line.renderText(font, x, textY, vanilla.pose().last().pose(), vanilla.bufferSource());
			textY += line.getHeight() + (lineIndex == 0 ? 2 : 0);
			lineIndex++;
		}
		
		vanilla.pose().popPose();
	}
	
	@Override
	public void renderItem(Level level, LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, int x, int y, int guiOffset)
	{
		if(!stack.isEmpty())
		{
			var model = Minecraft.getInstance().getItemRenderer().getModel(stack, level, entity, 0);
			
			this.pose().pushPose();
			this.pose().translate(x + 8, y + 8, 150 + (model.isGui3d() ? guiOffset : 0));
			this.pose().scale(16, -16, 16);
			
			if(!model.usesBlockLight())
			{
				Lighting.setupForFlatItems();
			}
			
			Minecraft.getInstance().getItemRenderer().render(stack, displayContext, false, this.pose(), this.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model);
			vanilla.flush();
			
			if(!model.usesBlockLight())
			{
				Lighting.setupFor3DItems();
			}
			
			this.pose().popPose();
		}
	}
}
