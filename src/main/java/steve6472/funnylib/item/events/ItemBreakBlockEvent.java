package steve6472.funnylib.item.events;

import org.bukkit.block.Block;
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
public interface ItemBreakBlockEvent
{
	/**
	 * @param player player who broke the block
	 * @param itemStack item the block was broken with
	 * @param block broken block
	 * @return false to cancel the event
	 */
	default boolean breakBlock(Player player, ItemStack itemStack, Block block) { return true; }
}
