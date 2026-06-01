package net.swedz.little_big_redstone.gui.microchip.wire.render;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class WiresRenderState
{
	public final Queue<WireRenderState> wires = new ConcurrentLinkedQueue<>();
	
	public void add(WireRenderState state)
	{
		if(state != null)
		{
			wires.add(state);
		}
	}
}
