package steve6472.funnylib.item.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.context.PlayerContext;

/**********************
 * Created by steve6472
 * On date: 4/12/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface ConsumeEvent
{
	default void consumed(PlayerContext context) {}
}
