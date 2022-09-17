package steve6472.funnylib.blocks.events;

import org.bukkit.event.player.PlayerInteractEvent;
import steve6472.funnylib.context.PlayerBlockContext;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface BlockClickEvents
{
	default void rightClick(PlayerBlockContext context, PlayerInteractEvent e) {}
	default void leftClick(PlayerBlockContext context, PlayerInteractEvent e) {}
}
