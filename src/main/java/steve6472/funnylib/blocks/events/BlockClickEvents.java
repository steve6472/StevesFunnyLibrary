package steve6472.funnylib.blocks.events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface BlockClickEvents
{
	default void rightClick(ItemStack itemInHand, Player player, PlayerInteractEvent e) {}
	default void leftClick(ItemStack itemInHand, Player player, PlayerInteractEvent e) {}
}
