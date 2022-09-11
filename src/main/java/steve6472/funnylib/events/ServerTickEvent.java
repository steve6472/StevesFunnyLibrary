package steve6472.funnylib.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**********************
 * Created by steve6472
 * On date: 4/9/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public class ServerTickEvent extends Event
{
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private long uptimeTick;

	public ServerTickEvent()
	{
	}

	public void setUptimeTick(long uptimeTick)
	{
		this.uptimeTick = uptimeTick;
	}

	public long getUptimeTick()
	{
		return uptimeTick;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS_LIST;
	}
}
