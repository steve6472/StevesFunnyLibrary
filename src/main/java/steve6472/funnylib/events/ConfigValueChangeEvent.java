package steve6472.funnylib.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.minigame.config.GameConfiguration;
import steve6472.funnylib.minigame.config.Value;
import steve6472.funnylib.util.JSONMessage;

/**********************
 * Created by steve6472
 * On date: 3/6(day)/2024
 * Project: StevesFunnyLibrary
 *
 ***********************/
public class ConfigValueChangeEvent extends Event implements Cancellable
{
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private boolean cancelled;

	private final Value<?> value;
	private final Object oldValue;
	private final Object newValue;

	public ConfigValueChangeEvent(Value<?> value, Object oldValue, Object newValue)
	{
		this.value = value;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Value<?> getValue()
	{
		return value;
	}

	public Object getOldValue()
	{
		return oldValue;
	}

	public Object getNewValue()
	{
		return newValue;
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

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b)
	{
		cancelled = b;
	}

	@Override
	public String toString()
	{
		return "ConfigValueChangeEvent{" + "cancelled=" + cancelled + ", value=" + value + ", oldValue=" + oldValue + ", newValue=" + newValue + '}';
	}

	/**
	 *
	 * @param gameConfig configuration
	 * @param value the value
	 * @param newValue new value to set the value to
	 * @param player player who did the change, will send message if not null
	 * @return false if the change was cancelled, true otherwise
	 * @param <T> value type
	 */
	public static <T> boolean change(GameConfiguration gameConfig, Value<T> value, T newValue, @Nullable Player player)
	{
		T oldValue = gameConfig.getValue(value);
		ConfigValueChangeEvent event = new ConfigValueChangeEvent(value, oldValue, newValue);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled())
		{
			if (player != null)
				JSONMessage.create("Value change cancelled externally.").color(ChatColor.YELLOW).send(player);
			return false;
		}

		gameConfig.setValue(value, newValue);
		return true;
	}
}
