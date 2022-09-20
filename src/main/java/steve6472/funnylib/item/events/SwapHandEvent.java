package steve6472.funnylib.item.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Response;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface SwapHandEvent
{
	Response swapHands(Player player, ItemStack customMainHand, ItemStack offHand);
}