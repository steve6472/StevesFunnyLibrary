package steve6472.funnylib.item.events;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**********************
 * Created by steve6472
 * On date: 4/7/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface ItemInvEvents
{
	default void clickInInventoryEvent(Player player, ItemStack itemStack, Inventory inventory, int slot, InventoryAction action, InventoryClickEvent theEvent) { }
}
