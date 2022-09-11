package steve6472.funnylib.item.events;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**********************
 * Created by steve6472
 * On date: 4/16/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface WeaponEvents
{
	default void dealDamage(Player player, EntityDamageByEntityEvent event, ItemStack itemStack, EquipmentSlot slot) {}
}
