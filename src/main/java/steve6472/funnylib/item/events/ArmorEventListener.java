package steve6472.funnylib.item.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.events.PlayerEquipArmorEvent;
import steve6472.funnylib.events.PlayerUnequipArmorEvent;
import steve6472.funnylib.item.Items;

import java.util.HashMap;
import java.util.Map;

/**********************
 * Created by steve6472
 * On date: 4/7/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public class ArmorEventListener implements Listener
{
	private final Map<Player, Map<EquipmentSlot, ItemStack>> playerEquipMap = new HashMap<>();
	private final Map<Player, Map<EquipmentSlot, ItemStack>> playerCustomEquipMap = new HashMap<>();

	public void tick()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			equipEvent(player, EquipmentSlot.HEAD);
			equipEvent(player, EquipmentSlot.CHEST);
			equipEvent(player, EquipmentSlot.LEGS);
			equipEvent(player, EquipmentSlot.FEET);
		}
	}

	private void equipEvent(Player player, EquipmentSlot equipmentSlot)
	{
		customEquipEvent(player, equipmentSlot);

		Map<EquipmentSlot, ItemStack> itemStackMap = playerEquipMap.computeIfAbsent(player, k -> new HashMap<>());

		ItemStack lastItem = itemStackMap.get(equipmentSlot);
		ItemStack currentItem = player.getInventory().getItem(equipmentSlot);
		if (lastItem == null && currentItem != null)
		{
			itemStackMap.put(equipmentSlot, currentItem);
			Bukkit.getPluginManager().callEvent(new PlayerEquipArmorEvent(player, equipmentSlot, currentItem));
		} else if (currentItem == null)
		{
			Bukkit.getPluginManager().callEvent(new PlayerUnequipArmorEvent(player, equipmentSlot, lastItem));
			itemStackMap.remove(equipmentSlot);
		}
	}

	/**
	 * The custom item one
	 */
	private void customEquipEvent(Player player, EquipmentSlot equipmentSlot)
	{
		boolean admin = player.isOp();

		Map<EquipmentSlot, ItemStack> itemStackMap = playerCustomEquipMap.computeIfAbsent(player, k -> new HashMap<>());

		ItemStack lastItem = itemStackMap.get(equipmentSlot);
		ItemStack currentItem = player.getInventory().getItem(equipmentSlot);
		Items.ItemEventEntry itemEntry = Items.getCustomItemEntry(currentItem);
		if (lastItem == null && itemEntry != null)
		{
			if (itemEntry.requireAdmin() && admin || !itemEntry.requireAdmin())
			{
				itemStackMap.put(equipmentSlot, currentItem);
				Items.callEventOnCustomItem(player, ArmorEvents.class, currentItem, (ae, i) -> ae.equip(player, i, equipmentSlot));
			}
		}
		else if (lastItem != null && itemEntry == null)
		{
			Items.ItemEventEntry lastCustomItem = Items.getCustomItemEntry(lastItem);
			if (lastCustomItem != null && (lastCustomItem.requireAdmin() && admin || !lastCustomItem.requireAdmin()))
			{
				Items.callEventOnCustomItem(player, ArmorEvents.class, lastItem, (ae, i) -> ae.unequip(player, i, equipmentSlot));
				itemStackMap.remove(equipmentSlot);
			}
		}
	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent e)
	{
		playerEquipMap.remove(e.getPlayer());
		playerCustomEquipMap.remove(e.getPlayer());
	}
}
