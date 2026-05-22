package net.swedz.little_big_redstone;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.swedz.little_big_redstone.gui.noteboard.contents.NoteBoardContents;

import java.util.function.Supplier;

public final class LBRAttachments
{
	private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, LBR.ID);
	
	public static final Supplier<AttachmentType<NoteBoardContents>> NOTE_BOARD = ATTACHMENT_TYPES.register(
			"note_board",
			() -> AttachmentType.builder(() -> NoteBoardContents.EMPTY)
					.serialize(NoteBoardContents.CODEC)
					.sync(NoteBoardContents.STREAM_CODEC)
					.copyOnDeath()
					.build()
	);
	
	public static void init(IEventBus bus)
	{
		ATTACHMENT_TYPES.register(bus);
	}
}
