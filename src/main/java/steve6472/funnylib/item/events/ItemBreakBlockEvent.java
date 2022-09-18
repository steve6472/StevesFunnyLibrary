package steve6472.funnylib.item.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.context.PlayerBlockContext;

/**********************
 * Created by steve6472
 * On date: 4/6/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public interface ItemBreakBlockEvent
{
	/**
	 * @param context context
	 * @return false to cancel the event
	 */
	default boolean breakBlock(PlayerBlockContext context) { return true; }
}
