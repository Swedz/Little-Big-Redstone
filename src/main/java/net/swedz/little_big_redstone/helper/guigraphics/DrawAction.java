package net.swedz.little_big_redstone.helper.guigraphics;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.joml.Matrix4f;

public interface DrawAction
{
	void addVertexes(Matrix4f pose, BufferBuilder buffer);
	
	default void addVertexes(GuiGraphicsBatch batch)
	{
		this.addVertexes(batch.pose(), batch.buffer());
	}
}
