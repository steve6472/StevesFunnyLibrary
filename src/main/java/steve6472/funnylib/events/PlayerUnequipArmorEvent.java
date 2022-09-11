package steve6472.funnylib.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**********************
 * Created by steve6472
 * On date: 4/9/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public class PlayerUnequipArmorEvent extends Event
{
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private Player player;
	private EquipmentSlot slot;
	private ItemStack item;

	public PlayerUnequipArmorEvent(Player player, EquipmentSlot slot, ItemStack item)
	{
		this.player = player;
		this.slot = slot;
		this.item = item;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS_LIST;
	}

	public Player getPlayer()
	{
		return player;
	}

	public EquipmentSlot getSlot()
	{
		return slot;
	}

	public ItemStack getItem()
	{
		return item;
	}
}
