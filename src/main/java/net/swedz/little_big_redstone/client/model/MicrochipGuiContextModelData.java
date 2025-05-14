package net.swedz.little_big_redstone.client.model;

import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import java.util.Objects;

public final class MicrochipGuiContextModelData
{
	public static final ModelProperty<MicrochipGuiContextModelData> KEY = new ModelProperty<>();
	
	public static final MicrochipGuiContextModelData DEFAULT = new MicrochipGuiContextModelData(false);
	
	public static MicrochipGuiContextModelData get(ModelData modelData)
	{
		var data = modelData.get(KEY);
		return data != null ? data : DEFAULT;
	}
	
	private final boolean inMicrochip;
	
	public MicrochipGuiContextModelData(boolean inMicrochip)
	{
		this.inMicrochip = inMicrochip;
	}
	
	public boolean isInMicrochip()
	{
		return inMicrochip;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(inMicrochip);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this == o ||
			   (o instanceof MicrochipGuiContextModelData other && inMicrochip == other.inMicrochip);
	}
}
