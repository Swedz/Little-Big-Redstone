package net.swedz.little_big_redstone.guide.microchip;

import com.google.common.collect.Maps;
import guideme.document.LytRect;
import guideme.document.block.LytBlock;
import guideme.document.interaction.GuideTooltip;
import guideme.document.interaction.InteractiveElement;
import guideme.document.interaction.ItemTooltip;
import guideme.layout.LayoutContext;
import guideme.render.RenderContext;
import guideme.siteexport.ExportableResourceProvider;
import guideme.siteexport.ResourceExporter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.panel.MicrochipRenderBoardPanel;
import net.swedz.little_big_redstone.guide.microchip.element.MicrochipObjectGuideTooltip;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.MicrochipSize;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.tesseract.neoforge.api.Bounds;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.Map;
import java.util.Optional;

public final class MicrochipLytBlock extends LytBlock implements ExportableResourceProvider, InteractiveElement
{
	private static final int PANEL_MARGIN = 5;
	
	private final Microchip microchip;
	
	private final Map<String, LogicEntry> logic = Maps.newHashMap();
	
	private final MicrochipRenderBoardPanel panel;
	
	public MicrochipLytBlock(DyeColor color, int width, int height)
	{
		microchip = new Microchip(MicrochipSize.create(new Bounds(0, 0, width, height), 1));
		
		panel = new MicrochipRenderBoardPanel(color, microchip);
	}
	
	public LogicEntry getLogic(String name)
	{
		return logic.get(name);
	}
	
	public void addLogic(String name, int x, int y, DyeColor color, LogicType<?> type)
	{
		LogicComponent<?, ?> component = type.defaultFactory().create();
		component.setColor(Optional.ofNullable(color));
		var entry = microchip.components().add(x, y, component);
		logic.put(name, entry);
	}
	
	public void addWire(String from, String to, int fromPort, int toPort)
	{
		var fromEntry = this.getLogic(from);
		var toEntry = this.getLogic(to);
		microchip.wires().add(fromEntry.slot(), fromPort, toEntry.slot(), toPort);
	}
	
	// TODO awarenesses
	
	@Override
	protected LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth)
	{
		return new LytRect(x, y, microchip.size().bounds().width() + (PANEL_MARGIN * 2), microchip.size().bounds().height() + (PANEL_MARGIN * 2));
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
		
		context.renderPanel(bounds);
		
		graphics.pose().translate(bounds.x() + PANEL_MARGIN, bounds.y() + PANEL_MARGIN, 0);
		
		panel.render(graphics);
		
		graphics.pose().popPose();
	}
	
	@Override
	public Optional<GuideTooltip> getTooltip(float mouseX, float mouseY)
	{
		int x = (int) (mouseX - bounds.x() - PANEL_MARGIN);
		int y = (int) (mouseY - bounds.y() - PANEL_MARGIN);
		
		var hoveredObject = microchip.findAt(x, y);
		if(hoveredObject != null)
		{
			return Optional.of(new MicrochipObjectGuideTooltip(hoveredObject));
		}
		
		var hoveredWire = panel.wires().findHoveredWire(x, y);
		if(hoveredWire != null)
		{
			return Optional.of(new ItemTooltip(LBRItems.REDSTONE_BIT.asItem().getDefaultInstance()));
		}
		
		return Optional.empty();
	}
	
	@Override
	public void tick()
	{
		if(microchip.components().traversal().isEmpty())
		{
			microchip.components().rebuildTraversal();
		}
		
		var context = new LogicContext(Minecraft.getInstance().level, new BlockPos(0, 0, 0), microchip);
		
		microchip.tickLogic(context);
		
		boolean microchipDirty = microchip.isDirty();
		boolean contextDirty = context.isDirty();
		if(microchipDirty || contextDirty)
		{
			microchip.markClean();
		}
	}
	
	@Override
	public void exportResources(ResourceExporter exporter)
	{
		exporter.exportTexture(LBR.id("textures/gui/container/microchip/circuit_background.png"));
		// TODO other textures for stuff...
	}
}
