package steve6472.funnylib.item.events;

import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**********************
 * Created by steve6472
 * On date: 3/18/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface ClickEvents
{
	default void rightClickAir(ItemStack item, PlayerInteractEvent e) {}
	default void rightClickBlock(ItemStack item, PlayerInteractEvent e) {}
	default void rightClick(ItemStack item, PlayerInteractEvent e) {}
	default void leftClickAir(ItemStack item, PlayerInteractEvent e) {}
	default void leftClickBlock(ItemStack item, PlayerInteractEvent e) {}
	default void leftClick(ItemStack item, PlayerInteractEvent e) {}
	default void rightClickEntity(ItemStack item, PlayerInteractEntityEvent e) {}
}
