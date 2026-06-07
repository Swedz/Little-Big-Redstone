package net.swedz.little_big_redstone.gui.logicconfig;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBRMenus;
import net.swedz.little_big_redstone.gui.logicconfig.reference.LogicConfigReference;
import net.swedz.little_big_redstone.microchip.object.logic.LogicCodecs;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;

import java.util.function.Supplier;

public final class LogicConfigMenu extends AbstractContainerMenu
{
	private final boolean shouldClientClose;
	
	private final LogicConfigReference reference;
	
	private final Supplier<Boolean> validChecker;
	
	private final DyeColor    color;
	private final LogicConfig logicConfig;
	
	public LogicConfigMenu(
			int containerId,
			Inventory playerInventory,
			boolean shouldClientClose,
			LogicConfigReference reference,
			Supplier<Boolean> validChecker,
			DyeColor color,
			LogicConfig logicConfig
	)
	{
		super(LBRMenus.LOGIC_CONFIG.get(), containerId);
		
		this.shouldClientClose = shouldClientClose;
		this.reference = reference;
		this.validChecker = validChecker;
		this.color = color;
		this.logicConfig = logicConfig;
	}
	
	public LogicConfigMenu(
			int containerId,
			Inventory playerInventory,
			RegistryFriendlyByteBuf buf
	)
	{
		super(LBRMenus.LOGIC_CONFIG.get(), containerId);
		
		this.shouldClientClose = ByteBufCodecs.BOOL.decode(buf);
		this.reference = null;
		this.validChecker = null;
		this.color = DyeColor.STREAM_CODEC.decode(buf);
		this.logicConfig = LogicCodecs.CONFIG_STREAM_CODEC.decode(buf);
	}
	
	public boolean shouldClientClose()
	{
		return shouldClientClose;
	}
	
	public DyeColor color()
	{
		return color;
	}
	
	public LogicConfig logicConfig()
	{
		return logicConfig;
	}
	
	public void save(Player player, LogicConfig config)
	{
		reference.save(player, config);
	}
	
	public void cancel(Player player)
	{
		reference.cancel(player);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotId)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return (validChecker == null || validChecker.get()) &&
			   reference.isStillValid(player);
	}
}
