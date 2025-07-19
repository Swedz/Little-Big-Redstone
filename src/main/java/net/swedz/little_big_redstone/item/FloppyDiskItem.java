package net.swedz.little_big_redstone.item;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.LBRText;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.microchip.object.logic.LogicEntry;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;
import net.swedz.little_big_redstone.network.packet.FloppyDiskGuiOverlayUpdatePacket;
import net.swedz.little_big_redstone.proxy.LBRProxy;
import net.swedz.tesseract.neoforge.api.tuple.Pair;
import net.swedz.tesseract.neoforge.event.PlayerInventoryChangeEvent;
import net.swedz.tesseract.neoforge.helper.TransferHelper;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@EventBusSubscriber(modid = LBR.ID)
public final class FloppyDiskItem extends Item implements DyeColoredItem
{
	private final DyeColor color;
	
	public FloppyDiskItem(Properties properties, DyeColor color)
	{
		super(properties.stacksTo(1).component(LBRComponents.FLOPPY_DISK, null));
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
	
	/**
	 * Gets a structured map of components for each color needed to replicate a microchip.
	 * <br><br>
	 * Each logic type for a color is mapped to a count of how many components of that type and color are needed.
	 *
	 * @param microchip the microchip
	 * @return the components of the microchip compiled into a map
	 */
	private static Map<Pair<LogicType<?>, Optional<DyeColor>>, Integer> getComponentsNeeded(Microchip.Immutable microchip)
	{
		Map<Pair<LogicType<?>, Optional<DyeColor>>, Integer> componentsNeeded = Maps.newHashMap();
		for(var entry : microchip.components())
		{
			var component = entry.component();
			Pair<LogicType<?>, Optional<DyeColor>> key = new Pair<>(component.type(), (Optional<DyeColor>) component.color());
			componentsNeeded.compute(key, (__, value) -> value == null ? 1 : (value + 1));
		}
		return componentsNeeded;
	}
	
	public static ConsumeResult consumeItems(Player player, Microchip.Immutable microchip, Microchip.Immutable targetMicrochip, boolean simulate)
	{
		if(player.hasInfiniteMaterials())
		{
			return new ConsumeResult();
		}
		
		List<ItemStack> presentItems = Lists.newArrayList();
		List<ItemStack> missingItems = Lists.newArrayList();
		
		int totalWiresNeeded = microchip.wireCount();
		int wiresReused = Math.min(totalWiresNeeded, targetMicrochip.wireCount());
		int wiresAvailable = wiresReused;
		wiresAvailable += TransferHelper.extractAny(player.getInventory(), (item) -> item.is(LBRItems.REDSTONE_BIT.get()), totalWiresNeeded - wiresAvailable, true, simulate);
		if(wiresAvailable != microchip.wireCount())
		{
			missingItems.add(new ItemStack(LBRItems.REDSTONE_BIT.get(), totalWiresNeeded - wiresAvailable));
		}
		if(wiresAvailable > 0)
		{
			presentItems.add(new ItemStack(LBRItems.REDSTONE_BIT.get(), wiresAvailable));
		}
		
		var componentsNeeded = getComponentsNeeded(microchip);
		List<Integer> logicReused = Lists.newArrayList();
		for(var entry : componentsNeeded.entrySet())
		{
			LogicType type = entry.getKey().a();
			var dye = entry.getKey().b();
			int totalLogicNeeded = entry.getValue();
			
			// Check if there is logic already available in the microchip
			int logicAvailable = 0;
			for(LogicEntry reuseEntry : targetMicrochip.components())
			{
				var component = reuseEntry.component();
				if(component.type().equals(type) &&
				   component.color().equals(dye))
				{
					logicReused.add(reuseEntry.slot());
					if(++logicAvailable == totalLogicNeeded)
					{
						break;
					}
				}
			}
			
			// If more logic is needed, check the player inventory
			if(logicAvailable < totalLogicNeeded)
			{
				Predicate<ItemStack> predicate = (item) ->
				{
					if(item.has(LBRComponents.LOGIC))
					{
						var component = item.get(LBRComponents.LOGIC);
						return component.type().equals(type) &&
							   component.color().equals(dye);
					}
					return false;
				};
				logicAvailable += TransferHelper.extractAny(player.getInventory(), predicate, totalLogicNeeded - logicAvailable, true, simulate);
			}
			
			// Not enough logic available
			if(logicAvailable != totalLogicNeeded)
			{
				var component = type.defaultFactory().create();
				component.setColor(dye);
				var stack = type.toStack(component);
				stack.setCount(totalLogicNeeded - logicAvailable);
				missingItems.add(stack);
			}
			// There is some logic available
			if(logicAvailable > 0)
			{
				var component = type.defaultFactory().create();
				component.setColor(dye);
				var stack = type.toStack(component);
				stack.setCount(logicAvailable);
				presentItems.add(stack);
			}
		}
		
		return new ConsumeResult(presentItems, missingItems, wiresReused, logicReused);
	}
	
	public record ConsumeResult(
			List<ItemStack> present, List<ItemStack> missing,
			int wiresReused, List<Integer> logicReused
	) implements Iterable<ItemStack>
	{
		public ConsumeResult(List<ItemStack> present, List<ItemStack> missing,
							 int wiresReused, List<Integer> logicReused)
		{
			present = Lists.newArrayList(present);
			missing = Lists.newArrayList(missing);
			present.sort(Comparator.comparingInt(ItemStack::getCount).reversed());
			missing.sort(Comparator.comparingInt(ItemStack::getCount).reversed());
			this.present = Collections.unmodifiableList(present);
			this.missing = Collections.unmodifiableList(missing);
			this.wiresReused = wiresReused;
			this.logicReused = logicReused;
		}
		
		public ConsumeResult()
		{
			this(List.of(), List.of(), 0, List.of());
		}
		
		public boolean isSuccess()
		{
			return missing.isEmpty();
		}
		
		public int size()
		{
			return present.size() + missing.size();
		}
		
		@Override
		public Iterator<ItemStack> iterator()
		{
			return Iterators.concat(present.iterator(), missing.iterator());
		}
	}
	
	private static void dropAll(Player player, Microchip microchip, ConsumeResult result)
	{
		if(player.hasInfiniteMaterials())
		{
			return;
		}
		for(var entry : microchip.components())
		{
			if(!result.logicReused().contains(entry.slot()))
			{
				ItemHandlerHelper.giveItemToPlayer(player, entry.toStack());
			}
		}
		int wiresToDrop = microchip.wires().values().size() - result.wiresReused();
		if(wiresToDrop > 0)
		{
			ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(LBRItems.REDSTONE_BIT, wiresToDrop));
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
						var targetMicrochip = microchipBlockEntity.microchip().immutable();
						var result = consumeItems(player, microchip, targetMicrochip, true);
						if(result.isSuccess())
						{
							consumeItems(player, microchip, targetMicrochip, false);
							dropAll(player, microchipBlockEntity.microchip(), result);
							microchipBlockEntity.microchip().loadFrom(microchip);
							player.displayClientMessage(LBRText.FLOPPY_DISK_APPLY_SUCCESS.text(), true);
						}
						else
						{
							player.displayClientMessage(LBRText.FLOPPY_DISK_APPLY_FAILURE.text(), true);
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
		Player player = context.getPlayer();
		if(player != null)
		{
			var usedHand = context.getHand();
			var itemStack = player.getItemInHand(usedHand);
			var hitBlockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
			if(hitBlockEntity instanceof MicrochipBlockEntity microchipBlockEntity)
			{
				if(!context.getLevel().isClientSide())
				{
					if(player.isShiftKeyDown())
					{
						itemStack.set(LBRComponents.FLOPPY_DISK, microchipBlockEntity.microchip().immutable());
						player.displayClientMessage(LBRText.FLOPPY_DISK_SAVE.text(), true);
					}
					else
					{
						if(itemStack.has(LBRComponents.FLOPPY_DISK) && !player.getCooldowns().isOnCooldown(this))
						{
							var microchip = itemStack.get(LBRComponents.FLOPPY_DISK);
							if(microchip != null)
							{
								var targetMicrochip = microchipBlockEntity.microchip().immutable();
								var result = consumeItems(player, microchip, targetMicrochip, true);
								if(result.isSuccess())
								{
									consumeItems(player, microchip, targetMicrochip, false);
									dropAll(player, microchipBlockEntity.microchip(), result);
									microchipBlockEntity.microchip().loadFrom(microchip);
									player.displayClientMessage(LBRText.FLOPPY_DISK_APPLY_SUCCESS.text(), true);
								}
								else
								{
									player.displayClientMessage(LBRText.FLOPPY_DISK_APPLY_FAILURE.text(), true);
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
			player.displayClientMessage(LBRText.FLOPPY_DISK_CLEAR.text(), true);
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
}
