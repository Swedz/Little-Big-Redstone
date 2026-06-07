package net.swedz.little_big_redstone.item.floppydisk;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.item.tooltip.ItemContainerContentsTooltipData;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.packet.FloppyDiskGuiOverlayUpdatePacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.event.PlayerInventoryChangeEvent;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.Optional;
import java.util.function.Consumer;

@EventBusSubscriber(modid = LBR.ID)
public final class FloppyDiskItem extends Item
{
	public FloppyDiskItem(Properties properties, DyeColor color)
	{
		super(properties
				.stacksTo(1)
				.component(LBRComponents.FLOPPY_DISK, null));
	}
	
	public static ItemStack getHeldStack(Player player)
	{
		var mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		var offHand = player.getItemInHand(InteractionHand.OFF_HAND);
		return mainHand.is(LBRTags.Items.FLOPPY_DISKS) ? mainHand :
				offHand.is(LBRTags.Items.FLOPPY_DISKS) ? offHand : ItemStack.EMPTY;
	}
	
	private static void dropAll(Player player, NonNullList<ItemStack> drops)
	{
		if(player.hasInfiniteMaterials())
		{
			return;
		}
		for(var stack : drops)
		{
			if(!stack.isEmpty())
			{
				player.getInventory().placeItemBackInInventory(stack.copy());
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	private static void onPlaceMicrochipWithFloppyDisk(BlockEvent.EntityPlaceEvent event)
	{
		if(event.getEntity() instanceof ServerPlayer player)
		{
			var offhand = player.getItemInHand(InteractionHand.OFF_HAND);
			if(offhand.has(LBRComponents.FLOPPY_DISK))
			{
				var microchip = offhand.get(LBRComponents.FLOPPY_DISK);
				if(microchip != null)
				{
					var blockEntity = event.getLevel().getBlockEntity(event.getPos());
					if(blockEntity instanceof MicrochipBlockEntity microchipBlockEntity)
					{
						if(!microchip.isValid(microchipBlockEntity.microchip().size()))
						{
							player.sendOverlayMessage(LBR.text().floppyDiskApplyFailureMalformed());
							return;
						}
						var targetMicrochip = microchipBlockEntity.microchip().immutable();
						var result = FloppyDiskInstaller.consumeItems(player, microchip, targetMicrochip, true);
						if(result.isSuccess())
						{
							result = FloppyDiskInstaller.consumeItems(player, microchip, targetMicrochip, false);
							dropAll(player, result.remainingDrops());
							microchipBlockEntity.microchip().loadFrom(microchip);
							player.sendOverlayMessage(LBR.text().floppyDiskApplySuccess());
						}
						else
						{
							player.sendOverlayMessage(LBR.text().floppyDiskApplyFailure());
						}
						new FloppyDiskGuiOverlayUpdatePacket(true).sendToClient(player);
					}
				}
			}
		}
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		var player = context.getPlayer();
		if(player != null)
		{
			var hitBlockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
			if(hitBlockEntity instanceof MicrochipBlockEntity microchipBlockEntity)
			{
				if(!context.getLevel().isClientSide())
				{
					if(player.isShiftKeyDown())
					{
						stack.set(LBRComponents.FLOPPY_DISK, microchipBlockEntity.microchip().immutable());
						stack.remove(LBRComponents.FLOPPY_DISK_PROGRAM_NAME);
						player.sendOverlayMessage(LBR.text().floppyDiskSave());
					}
					else
					{
						if(stack.has(LBRComponents.FLOPPY_DISK) &&
						   !player.getCooldowns().isOnCooldown(stack))
						{
							var microchip = stack.get(LBRComponents.FLOPPY_DISK);
							if(microchip != null)
							{
								if(!microchip.isValid(microchipBlockEntity.microchip().size()))
								{
									player.sendOverlayMessage(LBR.text().floppyDiskApplyFailureMalformed());
									return InteractionResult.SUCCESS;
								}
								var targetMicrochip = microchipBlockEntity.microchip().immutable();
								var result = FloppyDiskInstaller.consumeItems(player, microchip, targetMicrochip, true);
								if(result.isSuccess())
								{
									if(microchipBlockEntity.getPlacedBy() == null)
									{
										microchipBlockEntity.setPlacedBy(player.getUUID());
									}
									result = FloppyDiskInstaller.consumeItems(player, microchip, targetMicrochip, false);
									dropAll(player, result.remainingDrops());
									microchipBlockEntity.microchip().loadFrom(microchip);
									player.sendOverlayMessage(LBR.text().floppyDiskApplySuccess());
								}
								else
								{
									player.sendOverlayMessage(LBR.text().floppyDiskApplyFailure());
								}
								player.getCooldowns().addCooldown(stack, 20);
								new FloppyDiskGuiOverlayUpdatePacket(true).sendToClient((ServerPlayer) player);
							}
						}
					}
				}
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResult use(Level level, Player player, InteractionHand usedHand)
	{
		var stack = player.getItemInHand(usedHand);
		if(player.isShiftKeyDown())
		{
			stack.remove(LBRComponents.FLOPPY_DISK);
			stack.remove(LBRComponents.FLOPPY_DISK_PROGRAM_NAME);
			player.sendOverlayMessage(LBR.text().floppyDiskClear());
		}
		else if(level.isClientSide())
		{
			Proxies.get(LBRProxy.class).openFloppyDisk(usedHand);
		}
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(oldStack.getItem());
	}
	
	@SubscribeEvent
	private static void onPlayerInventoryChange(PlayerInventoryChangeEvent event)
	{
		if(event.getEntity() instanceof ServerPlayer player)
		{
			new FloppyDiskGuiOverlayUpdatePacket(false).sendToClient(player);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag)
	{
		if(display.shows(LBRComponents.FLOPPY_DISK_PROGRAM_NAME.get()) &&
		   stack.has(LBRComponents.FLOPPY_DISK_PROGRAM_NAME))
		{
			var name = stack.get(LBRComponents.FLOPPY_DISK_PROGRAM_NAME);
			lines.accept(LBR.text().floppyDiskProgramName(name.name()));
		}
	}
	
	private static ItemContainerContents convertMicrochipToContents(Microchip.Immutable microchip)
	{
		return ItemContainerContents.fromItems(FloppyDiskInstaller.asItemStacks(microchip).stream()
				.sorted(FloppyDiskInstaller.comparator())
				.toList());
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		return stack.get(DataComponents.TOOLTIP_DISPLAY).shows(LBRComponents.FLOPPY_DISK.get()) ?
				Optional.ofNullable(stack.get(LBRComponents.FLOPPY_DISK)).map((microchip) ->
						new ItemContainerContentsTooltipData(convertMicrochipToContents(microchip), 9, false)) :
				Optional.empty();
	}
}
