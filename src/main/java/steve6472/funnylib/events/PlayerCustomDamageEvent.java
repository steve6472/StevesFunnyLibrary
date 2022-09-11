package steve6472.funnylib.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**********************
 * Created by steve6472
 * On date: 7/11/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
@Deprecated
public class PlayerCustomDamageEvent extends Event implements Cancellable
{
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private final Player player;
	private final double damage;
	private final CustomDamageType customDamageType;
	private boolean cancelled;

	public PlayerCustomDamageEvent(Player player, double damage, CustomDamageType customDamageType)
	{
		this.player = player;
		this.damage = damage;
		this.customDamageType = customDamageType;
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

	public Player getPlayer()
	{
		return player;
	}

	public double getDamage()
	{
		return damage;
	}

	public CustomDamageType getCustomDamageType()
	{
		return customDamageType;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel)
	{
		this.cancelled = cancel;
	}

	public enum CustomDamageType
	{
		RAILZER
	}
}
