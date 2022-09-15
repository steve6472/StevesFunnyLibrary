package steve6472.funnylib.blocks.events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.stateengine.State;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface BlockClickEvents
{
	default void rightClick(State state, ItemStack itemInHand, Player player, PlayerInteractEvent e) {}
	default void leftClick(State state, ItemStack itemInHand, Player player, PlayerInteractEvent e) {}
}
