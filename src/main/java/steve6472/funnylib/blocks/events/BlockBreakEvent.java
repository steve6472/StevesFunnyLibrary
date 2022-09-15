package steve6472.funnylib.blocks.events;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface BlockBreakEvent
{
	default void rightClick(ItemStack item, PlayerInteractEvent e) {}
}
