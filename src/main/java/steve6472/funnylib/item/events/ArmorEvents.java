package steve6472.funnylib.item.events;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.context.PlayerItemContext;

/**********************
 * Created by steve6472
 * On date: 4/6/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface ArmorEvents
{
	default void wearTick(PlayerItemContext context) {}
	default void equip(PlayerItemContext context) {}
	default void unequip(PlayerItemContext context) {}
	default void takeDamage(PlayerItemContext context, EntityDamageByEntityEvent event) {}
}
