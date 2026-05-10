package net.swedz.little_big_redstone.gui.microchip.wire;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public record WirePath(
		List<WirePathPosition> positions,
		CompletableFuture<Void> future
)
{
	public boolean isPopulated()
	{
		return !positions.isEmpty();
	}
	
	public void block()
	{
		try
		{
			future.get();
		}
		catch(InterruptedException | ExecutionException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public static CompletableFuture<Void> allOf(Collection<WirePath> paths)
	{
		var futures = paths.stream()
				.map(WirePath::future)
				.toArray(CompletableFuture[]::new);
		return CompletableFuture.allOf(futures);
	}
	
	public static void blockAllOf(Collection<WirePath> paths)
	{
		try
		{
			allOf(paths).get();
		}
		catch(InterruptedException | ExecutionException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
