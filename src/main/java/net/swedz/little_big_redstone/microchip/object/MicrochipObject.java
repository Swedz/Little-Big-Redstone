package net.swedz.little_big_redstone.microchip.object;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.api.Bounds;

import java.util.Optional;

public interface MicrochipObject
{
	/**
	 * Gets the slot id of this object. Slots must be unique per {@link MicrochipObjectContainer}.
	 *
	 * @return the slot id
	 */
	int slot();
	
	/**
	 * Gets the x position of this object.
	 *
	 * @return the x position
	 */
	int x();
	
	/**
	 * Gets the y position of this object.
	 *
	 * @return the y position
	 */
	int y();
	
	/**
	 * Get the container type for this object.
	 *
	 * @return the {@link MicrochipObjectContainerType} of this object
	 */
	MicrochipObjectContainerType containerType();
	
	/**
	 * <p>Creates an {@link ItemStack} that represents the object. This should mean that if this {@link ItemStack} were
	 * to be placed back into the microchip, it would create an object of equivalent data to this one.</p>
	 *
	 * <p><b>NOTE:</b> The returned {@link ItemStack} <b>MUST</b> be either a fresh instance or a copy. Do not ever return the same
	 * {@link ItemStack} instance more than once. {@link ItemStack}s returned by this method can and will most
	 * certainly be modified.</p>
	 *
	 * @return the {@link ItemStack} of this object
	 */
	ItemStack toStack();
	
	/**
	 * Creates a {@link Bounds} that represents the object at the current position.
	 *
	 * @return the {@link Bounds} of this object
	 */
	Bounds toBounds();
	
	default boolean contains(int x, int y)
	{
		return this.toBounds().contains(x, y);
	}
	
	default boolean overlaps(Bounds other)
	{
		return this.toBounds().overlaps(other);
	}
	
	/**
	 * Gets the color value for this object. While this instance cannot be modified because it is an {@link Optional},
	 * this color relates to the mutable color of the object. Some objects may have multiple colors relating to them,
	 * but an object <i>should</i> only have one mutable color so that it works properly with the right-click to dye
	 * feature.
	 *
	 * @return the optional {@link DyeColor} of this object
	 */
	Optional<DyeColor> color();
	
	/**
	 * Sets the color value for this object.
	 *
	 * @param color the optional {@link DyeColor}
	 * @return true if the color changed, false otherwise
	 */
	boolean setColor(Optional<DyeColor> color);
}
