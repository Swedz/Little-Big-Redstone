package net.swedz.little_big_redstone.guide.tags.microchip;

import com.google.common.collect.Maps;
import com.mojang.serialization.DataResult;
import guideme.compiler.PageCompiler;
import guideme.document.LytErrorSink;
import guideme.document.LytRect;
import guideme.document.block.LytBlock;
import guideme.document.block.LytBox;
import guideme.document.block.LytVBox;
import guideme.document.interaction.GuideTooltip;
import guideme.document.interaction.InteractiveElement;
import guideme.document.interaction.ItemTooltip;
import guideme.document.interaction.LytWidget;
import guideme.internal.screen.GuideIconButton;
import guideme.layout.LayoutContext;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import guideme.render.RenderContext;
import guideme.siteexport.ExportableResourceProvider;
import guideme.siteexport.ResourceExporter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.DyeColor;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.panel.MicrochipRenderBoardPanel;
import net.swedz.little_big_redstone.guide.PausePlayGuideIconButton;
import net.swedz.little_big_redstone.guide.tags.microchip.element.MicrochipObjectGuideTooltip;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.MicrochipSize;
import net.swedz.little_big_redstone.microchip.awareness.AwarenessTypes;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicContext;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.tesseract.neoforge.api.Bounds;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.Map;
import java.util.Optional;

public final class MicrochipGuidebookScene extends LytBox implements ExportableResourceProvider
{
	public static final int PANEL_MARGIN = 5;
	
	private final DyeColor color;
	
	private final int width, height;
	private final boolean autoWidth, autoHeight;
	
	private final int marginWidth, marginHeight;
	private final boolean includeToolbar;
	
	private Microchip                 microchip;
	private MicrochipRenderBoardPanel panel;
	
	private final Map<Integer, LogicComponent> logicDefaults   = Maps.newHashMap();
	private final Map<String, Integer>         logic           = Maps.newHashMap();
	private final TimedRedstoneSignals         redstoneSignals = new TimedRedstoneSignals();
	
	private final Viewport viewport = new Viewport();
	private final LytVBox  toolbar  = new LytVBox();
	
	private final LytWidget resetButton;
	private final LytWidget pausePlayButton;
	
	public MicrochipGuidebookScene(DyeColor color,
								   int width, int height,
								   int marginWidth, int marginHeight,
								   boolean includeToolbar)
	{
		this.color = color;
		this.width = width;
		this.height = height;
		this.autoWidth = width == -1;
		this.autoHeight = height == -1;
		this.marginWidth = marginWidth;
		this.marginHeight = marginHeight;
		this.includeToolbar = includeToolbar;
		
		this.rebuildMicrochip(0, 0, autoWidth ? 0 : width, autoHeight ? 0 : height);
		
		this.append(viewport);
		
		toolbar.append(resetButton = new LytWidget(new GuideIconButton(0, 0, GuideIconButton.Role.RESET_VIEW, () ->
		{
			for(var entry : microchip.components())
			{
				entry.component().loadFrom(logicDefaults.get(entry.slot()));
			}
			redstoneSignals.reset();
			this.tickLogic();
		})));
		toolbar.append(pausePlayButton = new LytWidget(new PausePlayGuideIconButton(0, 0, () ->
		{
		})));
		if(includeToolbar)
		{
			this.append(toolbar);
		}
	}
	
	private void rebuildMicrochip(int startX, int startY, int endX, int endY)
	{
		int width = Math.abs(endX - startX);
		int height = Math.abs(endY - startY);
		var previous = microchip;
		microchip = new Microchip(MicrochipSize.create(new Bounds(0, 0, width, height), 1));
		if(previous != null)
		{
			microchip.loadFrom(previous);
			microchip.components().loadFrom(previous.components(), (before) -> new LogicEntry(before.slot(), before.x() - startX, before.y() - startY, before.component()));
		}
		panel = new MicrochipRenderBoardPanel(color, microchip);
	}
	
	public void adjustSize()
	{
		int startX = -1;
		int startY = -1;
		int endX = 0;
		int endY = 0;
		if(autoWidth || autoHeight)
		{
			// Find the first visible logic component coordinate
			int firstVisibleX = -1;
			int firstVisibleY = -1;
			for(var entry : microchip.components())
			{
				var bounds = entry.toBounds();
				if(entry.component().config().isVisible())
				{
					int x = bounds.minX() - marginWidth;
					if(firstVisibleX == -1 || firstVisibleX > x)
					{
						firstVisibleX = x;
					}
					int y = bounds.minY() - marginHeight;
					if(firstVisibleY == -1 || firstVisibleY > y)
					{
						firstVisibleY = y;
					}
				}
			}
			// Figure out where the start and end coordinates should be
			for(var entry : microchip.components())
			{
				boolean visible = entry.component().config().isVisible();
				var bounds = entry.toBounds();
				if(autoWidth)
				{
					// Shift the start x to the end of the furthest right hidden logic before the first visible logic
					if(!visible)
					{
						int entryStartX = bounds.maxX() + 1;
						if(startX < entryStartX && entryStartX < firstVisibleX)
						{
							startX = entryStartX;
						}
					}
					// Shift the end x to the start of the furthest left hidden logic
					int entryEndX = visible ? (bounds.maxX() + marginWidth + 1) : bounds.minX();
					if(endX < entryEndX)
					{
						endX = entryEndX;
					}
				}
				if(autoHeight)
				{
					// Shift the start y to the end of the top most hidden logic before the first visible logic
					if(!visible)
					{
						int entryStartY = bounds.maxY() + 1;
						if(startY < entryStartY && entryStartY < firstVisibleY)
						{
							startY = entryStartY;
						}
					}
					// Shift the end y to the start of the bottom most hidden logic
					int entryEndY = visible ? (bounds.maxY() + marginHeight + 1) : bounds.minY();
					if(endY < entryEndY)
					{
						endY = entryEndY;
					}
				}
			}
			if(startX == -1)
			{
				startX = firstVisibleX;
			}
			if(startY == -1)
			{
				startY = firstVisibleY;
			}
		}
		else
		{
			startX = 0;
			startY = 0;
			endX = width + (marginWidth * 2);
			endY = height + (marginHeight * 2);
		}
		this.rebuildMicrochip(startX, startY, endX, endY);
	}
	
	public Integer getLogicSlot(String name)
	{
		return logic.get(name);
	}
	
	public LogicEntry getLogic(String name)
	{
		var slot = this.getLogicSlot(name);
		return slot == null ? null : microchip.components().get(slot);
	}
	
	public void addLogic(String name, int x, int y, DyeColor color, LogicType<?> type, CompoundTag data, boolean hide,
						 PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el)
	{
		DataResult<? extends LogicComponent> result = type.codec().codec().parse(NbtOps.INSTANCE, data);
		if(result.isError())
		{
			errorSink.appendError(compiler, "Failed to parse data: " + result.error().orElseThrow().message(), el);
			return;
		}
		LogicComponent<?, ?> component = result.getOrThrow();
		component.setColor(Optional.ofNullable(color));
		if(hide)
		{
			component.config().hide();
		}
		var entry = microchip.components().addUnsafe(x + marginWidth, y + marginHeight, component);
		logicDefaults.put(entry.slot(), component);
		logic.put(name, entry.slot());
		microchip.markDirty();
	}
	
	public void addWire(String from, String to, int fromPort, int toPort,
						PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el)
	{
		var fromSlot = this.getLogicSlot(from);
		if(fromSlot == null)
		{
			errorSink.appendError(compiler, "Logic with name '" + from + "' does not exist", el);
			return;
		}
		var fromLogic = microchip.components().get(fromSlot);
		if(fromLogic.component().outputs() <= fromPort)
		{
			errorSink.appendError(compiler, "Logic with name '" + from + "' does not have an output port for that index", el);
			return;
		}
		
		var toSlot = this.getLogicSlot(to);
		if(toSlot == null)
		{
			errorSink.appendError(compiler, "Logic with name '" + to + "' does not exist", el);
			return;
		}
		var toLogic = microchip.components().get(toSlot);
		if(toLogic.component().inputs() <= toPort)
		{
			errorSink.appendError(compiler, "Logic with name '" + from + "' does not have an input port for that index", el);
			return;
		}
		
		microchip.wires().add(fromSlot, fromPort, toSlot, toPort);
		microchip.markDirty();
	}
	
	public void setRedstoneSignal(Integer step, Direction direction, int signal)
	{
		redstoneSignals.setSignal(step, direction, signal);
	}
	
	@Override
	protected LytRect computeBoxLayout(LayoutContext context, int x, int y, int availableWidth)
	{
		var viewportBounds = new LytRect(x, y, microchip.size().bounds().width() + (PANEL_MARGIN * 2), microchip.size().bounds().height() + (PANEL_MARGIN * 2));
		viewport.setBounds(viewportBounds);
		
		if(includeToolbar)
		{
			var toolbarBounds = toolbar.layout(context, x + viewportBounds.width(), y, availableWidth - viewportBounds.width());
			
			return LytRect.union(viewportBounds, toolbarBounds);
		}
		
		return viewportBounds;
	}
	
	@Override
	public void tick()
	{
		if(!((PausePlayGuideIconButton) pausePlayButton.getWidget()).isPlaying())
		{
			return;
		}
		
		this.tickLogic();
	}
	
	private void tickLogic()
	{
		var redstone = microchip.awarenesses().get(AwarenessTypes.REDSTONE);
		if(redstone != null)
		{
			redstoneSignals.tick();
			redstoneSignals.applySignals(redstone);
		}
		
		var context = new LogicContext(null, new BlockPos(0, 0, 0), microchip, null);
		
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
	
	final class Viewport extends LytBlock implements InteractiveElement
	{
		public void setBounds(LytRect bounds)
		{
			this.bounds = bounds;
		}
		
		@Override
		protected LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth)
		{
			return bounds;
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
			
			graphics.pose().translate(bounds.x() + PANEL_MARGIN, bounds.y() + PANEL_MARGIN, 10);
			
			panel.render(graphics);
			
			graphics.pose().popPose();
		}
		
		@Override
		public Optional<GuideTooltip> getTooltip(float mouseX, float mouseY)
		{
			int x = (int) (mouseX - bounds.x() - PANEL_MARGIN);
			int y = (int) (mouseY - bounds.y() - PANEL_MARGIN);
			
			var hoveredObject = microchip.findAt(x, y);
			if(hoveredObject != null && !(hoveredObject instanceof LogicEntry entry && !entry.component().config().isVisible()))
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
	}
}
