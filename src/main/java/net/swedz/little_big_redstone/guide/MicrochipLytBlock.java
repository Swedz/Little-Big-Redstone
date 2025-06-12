package net.swedz.little_big_redstone.guide;

import guideme.document.LytRect;
import guideme.document.block.LytBlock;
import guideme.layout.LayoutContext;
import guideme.render.RenderContext;
import guideme.siteexport.ExportableResourceProvider;
import guideme.siteexport.ResourceExporter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.microchip.panel.MicrochipRenderBoardPanel;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.MicrochipSize;
import net.swedz.tesseract.neoforge.api.Bounds;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

public final class MicrochipLytBlock extends LytBlock implements ExportableResourceProvider
{
	private final Microchip microchip;
	
	private final MicrochipRenderBoardPanel panel;
	
	public MicrochipLytBlock(DyeColor color, int width, int height)
	{
		microchip = new Microchip(MicrochipSize.create(new Bounds(0, 0, width, height), 1));
		
		panel = new MicrochipRenderBoardPanel(color, microchip);
	}
	
	public Microchip microchip()
	{
		return microchip;
	}
	
	@Override
	protected LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth)
	{
		return new LytRect(x, y, microchip.size().bounds().width(), microchip.size().bounds().height());
	}
	
	@Override
	protected void onLayoutMoved(int deltaX, int deltaY)
	{
	}
	
	@Override
	public void renderBatch(RenderContext context, MultiBufferSource buffers)
	{
	}
	
	@Override
	public void render(RenderContext context)
	{
		var graphics = new TesseractGuiGraphics(context.guiGraphics());
		graphics.pose().pushPose();
		graphics.pose().translate(bounds.x(), bounds.y(), 0);
		panel.render(graphics);
		graphics.pose().popPose();
	}
	
	@Override
	public void exportResources(ResourceExporter exporter)
	{
		exporter.exportTexture(LBR.id("textures/gui/container/microchip/circuit_background.png"));
		// TODO other textures for stuff...
	}
}
