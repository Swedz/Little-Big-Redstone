package net.swedz.little_big_redstone.microchip.tag;

import net.swedz.tesseract.neoforge.api.Assert;

import java.util.UUID;

public record TagOwnerKey(UUID uuid)
{
	public static final TagOwnerKey GLOBAL = new TagOwnerKey(new UUID(0, 0));
	
	public TagOwnerKey
	{
		Assert.notNull(uuid);
	}
}
