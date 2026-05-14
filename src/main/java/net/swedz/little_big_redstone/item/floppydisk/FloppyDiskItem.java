package net.swedz.little_big_redstone.item.floppydisk;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.item.DyeColoredItem;
import net.swedz.little_big_redstone.item.tooltip.ItemContainerContentsTooltipData;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.packet.FloppyDiskGuiOverlayUpdatePacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.event.PlayerInventoryChangeEvent;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = LBR.ID)
public final class FloppyDiskItem extends Item implements DyeColoredItem
{
	private final DyeColor color;
	
	public FloppyDiskItem(Properties properties, DyeColor color)
	{
		super(properties
				.stacksTo(1)
				.component(LBRComponents.FLOPPY_DISK, null));
		this.color = color;
	}
	
	@Override
	public DyeColor color()
	{
		return color;
	}
	
	public static ItemStack getHeldStack(Player player)
	{
		var mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		var offHand = player.getItemInHand(InteractionHand.OFF_HAND);
		return mainHand.is(LBRTags.Items.FLOPPY_DISKS) ? mainHand :
				offHand.is(LBRTags.Items.FLOPPY_DISKS) ? offHand : ItemStack.EMPTY;
	}
	
	private static void dropAll(Player player, IItemHandler drops)
	{
		if(player.hasInfiniteMaterials())
		{
			return;
		}
		for(int slot = 0; slot < drops.getSlots(); slot++)
		{
			var stack = drops.getStackInSlot(slot);
			if(!stack.isEmpty())
			{
				ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
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
							player.displayClientMessage(LBR.text().floppyDiskApplyFailureMalformed(), true);
							return;
						}
						var targetMicrochip = microchipBlockEntity.microchip().immutable();
						var result = FloppyDiskInstaller.consumeItems(player, microchip, targetMicrochip, true);
						if(result.isSuccess())
						{
							result = FloppyDiskInstaller.consumeItems(player, microchip, targetMicrochip, false);
							dropAll(player, result.remainingDrops());
							microchipBlockEntity.microchip().loadFrom(microchip);
							player.displayClientMessage(LBR.text().floppyDiskApplySuccess(), true);
						}
						else
						{
							player.displayClientMessage(LBR.text().floppyDiskApplyFailure(), true);
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
						player.displayClientMessage(LBR.text().floppyDiskSave(), true);
					}
					else
					{
						if(stack.has(LBRComponents.FLOPPY_DISK) && !player.getCooldowns().isOnCooldown(this))
						{
							var microchip = stack.get(LBRComponents.FLOPPY_DISK);
							if(microchip != null)
							{
								if(!microchip.isValid(microchipBlockEntity.microchip().size()))
								{
									player.displayClientMessage(LBR.text().floppyDiskApplyFailureMalformed(), true);
									return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
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
									player.displayClientMessage(LBR.text().floppyDiskApplySuccess(), true);
								}
								else
								{
									player.displayClientMessage(LBR.text().floppyDiskApplyFailure(), true);
								}
								player.getCooldowns().addCooldown(this, 20);
								new FloppyDiskGuiOverlayUpdatePacket(true).sendToClient((ServerPlayer) player);
							}
						}
					}
				}
				return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		var stack = player.getItemInHand(usedHand);
		if(player.isShiftKeyDown())
		{
			stack.remove(LBRComponents.FLOPPY_DISK);
			stack.remove(LBRComponents.FLOPPY_DISK_PROGRAM_NAME);
			player.displayClientMessage(LBR.text().floppyDiskClear(), true);
		}
		else if(level.isClientSide())
		{
			Proxies.get(LBRProxy.class).openFloppyDisk(usedHand);
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
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
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag)
	{
		if(stack.has(LBRComponents.FLOPPY_DISK_PROGRAM_NAME))
		{
			var name = stack.get(LBRComponents.FLOPPY_DISK_PROGRAM_NAME);
			lines.add(LBR.text().floppyDiskProgramName(name.name()));
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
		return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP) ?
				Optional.ofNullable(stack.get(LBRComponents.FLOPPY_DISK)).map((microchip) ->
						new ItemContainerContentsTooltipData(convertMicrochipToContents(microchip), 9, false)) :
				Optional.empty();
	}
}
