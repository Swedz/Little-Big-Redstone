package net.swedz.little_big_redstone.gui.microchip.widget;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRColors;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.gui.microchip.MicrochipScreen;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathKey;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.List;
import java.util.Optional;

public final class MicrochipThermostatWidget implements GuiEventListener, Renderable, NarratableEntry
{
	private record ThermometerStats(
			int logic,
			int stickyNotes,
			int wires,
			int wiresPopulated
	)
	{
		public int objects()
		{
			return logic + stickyNotes;
		}
	}
	
	final int x, y;
	
	private boolean focused;
	
	private final MicrochipScreen screen;
	
	private ThermometerStats stats = new ThermometerStats(0, 0, 0, 0);
	
	private float fullnessTotal, fullnessVisible;
	
	public MicrochipThermostatWidget(int x, int y, MicrochipScreen screen)
	{
		this.x = x + 14;
		this.y = y + 150;
		
		this.screen = screen;
	}
	
	private static final int MAX_OBJECTS;
	private static final int LOTS_OF_WIRES = 1000;
	
	static
	{
		float objectWidthHeight = 16;
		float circuitWidth = MicrochipBlockEntity.CIRCUIT_BOUNDS.width() / MicrochipBlockEntity.CIRCUIT_SCALE;
		float circuitHeight = MicrochipBlockEntity.CIRCUIT_BOUNDS.height() / MicrochipBlockEntity.CIRCUIT_SCALE;
		MAX_OBJECTS = (int) ((circuitWidth / objectWidthHeight) * (circuitHeight / objectWidthHeight));
	}
	
	private ThermometerStats calculateStats()
	{
		var microchip = screen.getMenu().microchip();
		int components = microchip.components().size();
		int wires = microchip.wires().size();
		int stickyNotes = microchip.stickyNotes().size();
		
		int wiresPopulated = 0;
		for(var wire : microchip.wires())
		{
			var key = WirePathKey.placed(microchip, wire);
			var path = screen.getMicrochipWidget().panel().wires().pathing().get(key, true);
			wiresPopulated += path.isPopulated() ? 1 : 0;
		}
		
		return new ThermometerStats(components, stickyNotes, wires, wiresPopulated);
	}
	
	private float calculateFullness(ThermometerStats stats, boolean visible)
	{
		int wires = visible ? stats.wiresPopulated() : stats.wires();
		
		float objectScale = stats.objects() / (float) MAX_OBJECTS;
		float wireScale = wires / (float) LOTS_OF_WIRES;
		float averageScale = (objectScale + wireScale) / 2f;
		
		return Mth.clamp(averageScale, 0, 1);
	}
	
	public void tick()
	{
		stats = this.calculateStats();
		fullnessTotal = this.calculateFullness(stats, false);
		fullnessVisible = this.calculateFullness(stats, true);
	}
	
	@Override
	public void render(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		this.renderThermometer(graphics, mouseX, mouseY);
		
		this.renderTooltip(vanilla, mouseX, mouseY);
	}
	
	private void renderThermometerJuice(TesseractGuiGraphics graphics, int mouseX, int mouseY, float fullness)
	{
		int fullBarHeight = 50;
		int bar = Math.round(fullBarHeight * fullness);
		graphics.blit(0, 2 + (fullBarHeight - bar), 14, 2 + (fullBarHeight - bar), 14, bar + (64 - fullBarHeight), 42, 64);
	}
	
	private void renderThermometer(TesseractGuiGraphics graphics, int mouseX, int mouseY)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(x, y, 0);
		
		graphics.setTexture(LBR.id("textures/gui/container/microchip/thermometer.png"));
		
		if(fullnessTotal != fullnessVisible)
		{
			graphics.setColor(LBRColors.componentForeground(screen.getMenu().color()));
			this.renderThermometerJuice(graphics, mouseX, mouseY, fullnessTotal);
			graphics.resetColor();
			
			graphics.setColor(0, 0, 0, 0.5f);
			this.renderThermometerJuice(graphics, mouseX, mouseY, fullnessTotal);
			graphics.resetColor();
		}
		
		graphics.setColor(LBRColors.componentForeground(screen.getMenu().color()));
		this.renderThermometerJuice(graphics, mouseX, mouseY, fullnessVisible);
		graphics.resetColor();
		
		graphics.blit(0, 0, 28, 0, 14, 64, 42, 64);
		
		graphics.pose().popPose();
	}
	
	private MutableComponent formatComplexity(float percentage)
	{
		if(percentage >= 0.9f)
		{
			return LBR.text().thermometerComplexityVeryHigh(percentage);
		}
		else if(percentage >= 0.6f)
		{
			return LBR.text().thermometerComplexityHigh(percentage);
		}
		else if(percentage >= 0.3f)
		{
			return LBR.text().thermometerComplexityModerate(percentage);
		}
		return LBR.text().thermometerComplexityLow(percentage);
	}
	
	private List<Component> formatTooltip()
	{
		boolean allWiresPopulated = stats.wiresPopulated() == stats.wires();
		
		List<Component> lines = Lists.newArrayList();
		
		lines.add(LBR.text().thermometerTooltipHeader());
		
		lines.add(LBR.text().thermometerTooltipComplexity(this.formatComplexity(fullnessTotal)));
		lines.add(LBR.text().thermometerTooltipLogic(stats.logic()));
		lines.add(allWiresPopulated ?
				LBR.text().thermometerTooltipWires(stats.wires()) :
				LBR.text().thermometerTooltipWires(stats.wiresPopulated(), (stats.wiresPopulated() / (float) stats.wires())));
		lines.add(LBR.text().thermometerTooltipNotes(stats.stickyNotes()));
		
		if(!allWiresPopulated)
		{
			lines.add(Component.empty());
			lines.add(LBR.text().thermometerTooltipLoadingWires());
		}
		
		return lines;
	}
	
	private void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
	{
		if(this.isMouseOver(mouseX, mouseY))
		{
			graphics.renderTooltip(Minecraft.getInstance().font, this.formatTooltip(), Optional.empty(), mouseX, mouseY);
		}
	}
	
	@Override
	public void setFocused(boolean focused)
	{
		this.focused = focused;
	}
	
	@Override
	public boolean isFocused()
	{
		return focused;
	}
	
	@Override
	public NarrationPriority narrationPriority()
	{
		return NarrationPriority.NONE;
	}
	
	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput)
	{
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		return this.getRectangle().containsPoint((int) mouseX, (int) mouseY);
	}
	
	@Override
	public ScreenRectangle getRectangle()
	{
		return new ScreenRectangle(x, y, 14, 64);
	}
}
