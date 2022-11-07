package steve6472.funnylib.item.events;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.context.PlayerItemContext;

/**********************
 * Created by steve6472
 * On date: 4/7/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface ItemInvEvents
{
	default void clickInInventoryEvent(PlayerItemContext context, Inventory inventory, int slot, InventoryAction action, InventoryClickEvent theEvent) { }
}
