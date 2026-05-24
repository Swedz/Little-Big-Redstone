package net.swedz.little_big_redstone;

import com.google.common.collect.Maps;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.swedz.tesseract.api.Assert;

import java.util.Collections;
import java.util.Map;

@EventBusSubscriber(modid = LBR.ID, value = Dist.CLIENT)
public final class LBRClientModels
{
	private static final Map<DyeColor, ModelResourceLocation> STICKY_NOTES;
	
	static
	{
		Map<DyeColor, ModelResourceLocation> stickyNotes = Maps.newHashMap();
		for(var color : DyeColor.values())
		{
			stickyNotes.put(color, ModelResourceLocation.standalone(LBR.id("block/%s_sticky_note".formatted(color.getName()))));
		}
		STICKY_NOTES = Collections.unmodifiableMap(stickyNotes);
	}
	
	public static ModelResourceLocation stickyNote(DyeColor color)
	{
		Assert.notNull(color);
		return STICKY_NOTES.get(color);
	}
	
	@SubscribeEvent
	private static void registerAdditionalModels(ModelEvent.RegisterAdditional event)
	{
		STICKY_NOTES.values().forEach(event::register);
	}
}
