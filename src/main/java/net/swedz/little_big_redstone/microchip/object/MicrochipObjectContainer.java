package net.swedz.little_big_redstone.microchip.object;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.microchip.Microchip;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class MicrochipObjectContainer<T extends MicrochipObject, S extends MicrochipObjectContainer<T, S>> implements Iterable<T>
{
	protected final Microchip microchip;
	
	protected Map<Integer, T> objects = Maps.newHashMap();
	
	/**
	 * Should not ever be used directly, only by other constructors.
	 */
	protected MicrochipObjectContainer(Microchip microchip, List<T> objects)
	{
		this.microchip = microchip;
		for(T value : objects)
		{
			int slot = value.slot();
			if(this.objects.containsKey(slot))
			{
				LBR.LOGGER.warn("Duplicate slot id ({}) present in {}, skipping!", slot, this.getClass().getSimpleName());
				return;
			}
			this.objects.put(slot, value);
		}
	}
	
	/**
	 * Should only be used by codecs. Instances created using this should get converted to one that has a microchip attached (using {@link #with(Microchip)}) before doing anything with it.
	 */
	protected MicrochipObjectContainer(List<T> objects)
	{
		this(null, objects);
	}
	
	public MicrochipObjectContainer(Microchip microchip)
	{
		this(microchip, Lists.newArrayList());
	}
	
	public final List<T> values()
	{
		return List.copyOf(objects.values());
	}
	
	@Override
	public final Iterator<T> iterator()
	{
		return this.values().iterator();
	}
	
	public final T get(int slot)
	{
		return objects.get(slot);
	}
	
	public final boolean has(int slot)
	{
		return objects.containsKey(slot);
	}
	
	/**
	 * <p>Checks whether the {@link Bounds} overlaps with any objects in this container.</p>
	 *
	 * <p>This method <i>should</i> only be invoked by {@link Microchip#canFit(Bounds)} to ensure that the bounds of all
	 * object containers are respected. You may ignore this guideline for some purposes.</p>
	 *
	 * @param bounds the {@link Bounds} to check against
	 * @return false if the {@link Bounds} overlaps with any object, true otherwise
	 */
	public final boolean canFit(Bounds bounds)
	{
		for(T value : objects.values())
		{
			if(value.overlaps(bounds))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * <p>Finds the object that contains the position.</p>
	 *
	 * <p>This method <i>should</i> only be invoked by {@link Microchip#findAt(int, int)} to ensure that all object
	 * containers are respected. You may ignore this guideline for some purposes.</p>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the object at the position, null if none exists
	 */
	public final T findAt(int x, int y)
	{
		for(T value : objects.values())
		{
			if(value.contains(x, y))
			{
				return value;
			}
		}
		return null;
	}
	
	protected final int pickAvailableSlot()
	{
		int slot = 0;
		while(true)
		{
			if(!objects.containsKey(slot))
			{
				return slot;
			}
			slot++;
		}
	}
	
	public abstract S with(Microchip microchip);
	
	public abstract void loadFrom(S other);
	
	public void clear()
	{
		objects.clear();
	}
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object o);
}
