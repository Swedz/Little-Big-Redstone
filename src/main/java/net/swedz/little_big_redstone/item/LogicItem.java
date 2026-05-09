package net.swedz.little_big_redstone.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.logicconfig.LogicConfigMenu;
import net.swedz.little_big_redstone.gui.logicconfig.reference.HeldItemLogicConfigReference;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.little_big_redstone.microchip.object.logic.LogicType;

import java.util.List;

public final class LogicItem extends Item
{
	private final LogicType<?> type;
	
	public LogicItem(Properties properties, LogicType<?> type)
	{
		super(properties.component(LBRComponents.LOGIC, type.defaultFactory().create()));
		this.type = type;
	}
	
	public LogicType<?> getLogicGateType()
	{
		return type;
	}
	
	private static void appendColorTooltip(List<Component> lines, DyeColor color)
	{
		lines.add(Component.translatable("item.color", Component.translatable("color.minecraft." + color.getName()))
				.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xA9A9A9))));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag)
	{
		var component = (LogicComponent<?, ?>) stack.get(LBRComponents.LOGIC);
		component.color().ifPresent((color) -> appendColorTooltip(lines, color));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);
		var stackComponent = (LogicComponent<?, ?>) stack.get(LBRComponents.LOGIC);
		if(stackComponent == null)
		{
			return InteractionResultHolder.fail(stack);
		}
		
		if(!level.isClientSide())
		{
			var component = stackComponent.copy();
			var color = component.color().orElse(DyeColor.WHITE);
			
			player.openMenu(
					new MenuProvider()
					{
						@Override
						public Component getDisplayName()
						{
							return component.type().displayName();
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
									component
							);
						}
					},
					(buf) ->
					{
						ByteBufCodecs.BOOL.encode(buf, true);
						DyeColor.STREAM_CODEC.encode(buf, color);
						LogicComponent.STREAM_CODEC.encode(buf, component);
					}
			);
		}
		
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}
}
