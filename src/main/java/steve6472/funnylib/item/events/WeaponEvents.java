package steve6472.funnylib.item.events;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.context.PlayerItemContext;

/**********************
 * Created by steve6472
 * On date: 4/16/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface WeaponEvents
{
	default void dealDamage(PlayerItemContext context, EntityDamageByEntityEvent event) {}
}
