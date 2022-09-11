package steve6472.funnylib.item.events;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**********************
 * Created by steve6472
 * On date: 4/6/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface ArmorEvents
{
	default void wearTick(Player player, ItemStack itemStack, EquipmentSlot slot) {}
	default void equip(Player player, ItemStack itemStack, EquipmentSlot slot) {}
	default void unequip(Player player, ItemStack itemStack, EquipmentSlot slot) {}
	default void takeDamage(Player player, EntityDamageByEntityEvent event, ItemStack itemStack, EquipmentSlot slot) {}
}
