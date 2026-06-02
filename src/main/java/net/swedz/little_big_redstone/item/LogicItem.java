package net.swedz.little_big_redstone.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigMenu;
import net.swedz.little_big_redstone.gui.logicconfig.reference.HeldItemLogicConfigReference;
import net.swedz.little_big_redstone.microchip.object.logic.LogicCodecs;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.Optional;
import java.util.function.Consumer;

public final class LogicItem extends Item
{
	public LogicItem(Properties properties, LogicType<?, ?> type)
	{
		super(properties
				.component(LBRComponents.LOGIC_CONFIG, type.defaultConfig())
				.component(LBRComponents.LOGIC_COLOR, null));
	}
	
	public static Optional<DyeColor> getColor(ItemStack stack)
	{
		return Optional.ofNullable(stack.get(LBRComponents.LOGIC_COLOR));
	}
	
	public static DyeColor getColor(ItemStack stack, DyeColor fallback)
	{
		return getColor(stack).orElse(fallback);
	}
	
	private static void appendColorTooltip(Consumer<Component> lines, DyeColor color)
	{
		lines.accept(Component.translatable("item.color", Component.translatable("color.minecraft." + color.getName()))
				.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xA9A9A9))));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag)
	{
		if(display.shows(LBRComponents.LOGIC_COLOR.get()) &&
		   stack.has(LBRComponents.LOGIC_COLOR))
		{
			var color = stack.get(LBRComponents.LOGIC_COLOR);
			appendColorTooltip(lines, color);
		}
	}
	
	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);
		var logicConfig = stack.get(LBRComponents.LOGIC_CONFIG);
		if(logicConfig == null)
		{
			return InteractionResult.FAIL;
		}
		
		if(!level.isClientSide())
		{
			var color = getColor(stack, DyeColor.WHITE);
			
			player.openMenu(
					new MenuProvider()
					{
						@Override
						public Component getDisplayName()
						{
							return logicConfig.type().displayName();
						}
						
						@Override
						public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player)
						{
							return new LogicConfigMenu(
									containerId,
									playerInventory,
									true,
									new HeldItemLogicConfigReference(hand),
									() -> true,
									color,
									logicConfig
							);
						}
					},
					(buf) ->
					{
						ByteBufCodecs.BOOL.encode(buf, true);
						DyeColor.STREAM_CODEC.encode(buf, color);
						LogicCodecs.CONFIG_STREAM_CODEC.encode(buf, logicConfig);
					}
			);
		}
		
		return InteractionResult.SUCCESS;
	}
}
