package steve6472.funnylib.item.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface SwapHandEvent
{
	void swapHands(Player player, ItemStack customMainHand, ItemStack offHand, CancellableResult result);
}
