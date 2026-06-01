package net.swedz.little_big_redstone;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.little_big_redstone.item.floppydisk.FloppyDiskProgramName;
import net.swedz.little_big_redstone.item.stickynote.StickyNote;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.tesseract.neoforge.item.ItemStackInstance;

import java.util.function.Supplier;

public final class LBRComponents
{
	private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LBR.ID);
	
	public static final Supplier<DataComponentType<LogicComponent>>        LOGIC                    = create("logic", LogicComponent.CODEC, LogicComponent.STREAM_CODEC); // TODO 26.1 split into LOGIC_COLOR, LOGIC_CONFIG? would make recipes really simple
	public static final Supplier<DataComponentType<ItemContainerContents>> LOGIC_ARRAY_STORAGE      = create("logic_array_storage", ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC);
	public static final Supplier<DataComponentType<Microchip.Immutable>>   FLOPPY_DISK              = create("floppy_disk", Microchip.Immutable.CODEC, Microchip.Immutable.STREAM_CODEC);
	public static final Supplier<DataComponentType<FloppyDiskProgramName>> FLOPPY_DISK_PROGRAM_NAME = create("floppy_disk_program_name", FloppyDiskProgramName.CODEC, FloppyDiskProgramName.STREAM_CODEC);
	public static final Supplier<DataComponentType<StickyNote>>            STICKY_NOTE              = create("sticky_note", StickyNote.CODEC, StickyNote.STREAM_CODEC);
	public static final Supplier<DataComponentType<DyeColor>>              STICKY_NOTE_COLOR        = create("sticky_note_color", DyeColor.CODEC, DyeColor.STREAM_CODEC); // TODO 26.1 use this instead of instanceof checks everywhere
	public static final Supplier<DataComponentType<DyeColor>>              STICKY_NOTE_TEXT_COLOR   = create("sticky_note_text_color", DyeColor.CODEC, DyeColor.STREAM_CODEC);
	public static final Supplier<DataComponentType<Boolean>>               STICKY_NOTE_EDITABLE     = create("sticky_note_editable", Codec.BOOL, ByteBufCodecs.BOOL);
	public static final Supplier<DataComponentType<ItemStackInstance>>     STICKY_NOTE_DISPLAY_ITEM = create("sticky_note_display_item", ItemStackInstance.CODEC, ItemStackInstance.STREAM_CODEC);
	
	public static void init(IEventBus bus)
	{
		COMPONENTS.register(bus);
	}
	
	private static <D> DeferredHolder<DataComponentType<?>, DataComponentType<D>> create(String name, Codec<D> codec, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec)
	{
		return COMPONENTS.registerComponentType(name, (b) -> b.persistent(codec).networkSynchronized(streamCodec));
	}
}
