package net.swedz.little_big_redstone.gui.microchip.wire.render;

import net.swedz.little_big_redstone.gui.microchip.wire.WirePathPosition;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class WireRenderState
{
	public int wireSize = 2;
	
	public boolean powered = false;
	public boolean hovered = false;
	public int     color   = 0xFFFFFFFF;
	
	public final Queue<WirePathPosition> positions = new ConcurrentLinkedQueue<>();
}
