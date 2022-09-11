package steve6472.funnylib.item.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**********************
 * Created by steve6472
 * On date: 6/10/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface TickInHandEvent
{
	default void tickInHand(Player player, ItemStack itemStack, EquipmentSlot hand) {}
}
