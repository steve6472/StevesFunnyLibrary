package steve6472.funnylib.minigame;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractPlayerState
{
	protected PlayerStateTracker tracker;

	private final List<Listener> events = new LinkedList<>();
	private final List<BukkitTask> tasks = new LinkedList<>();

	public abstract String getName();

	public abstract void apply(Player player);

	public abstract void revert(Player player);

	public boolean isAppliedTo(Player player)
	{
		return tracker.hasState(player, getName());
	}

	public <EventType extends Event> void registerEvents(Class<EventType> eventClass, Consumer<EventType> handler)
	{
		EventListenerWrapper<EventType> wrapper = new EventListenerWrapper<>();
		wrapper.event = handler;

		Bukkit.getPluginManager().registerEvent(eventClass, wrapper, EventPriority.NORMAL, (listener, event) -> handler.accept((EventType) event), tracker.game.plugin);

		events.add(wrapper);
	}

	public void scheduleSyncTask(Consumer<BukkitTask> task, long delay)
	{
		tasks.add(new MiniBukkitRunnable(task).runTaskLater(tracker.game.plugin, delay));
	}

	public void scheduleAsyncTask(Consumer<BukkitTask> task, long delay)
	{
		tasks.add(new MiniBukkitRunnable(task).runTaskLaterAsynchronously(tracker.game.plugin, delay));
	}

	public void scheduleRepeatingTask(Consumer<BukkitTask> task, long delay, long period)
	{
		tasks.add(new MiniBukkitRunnable(task).runTaskTimer(tracker.game.plugin, delay, period));
	}

	public void dispose()
	{
		// This is where you cancel all the tasks and unregister your listeners
		for (Listener listener : events)
		{
			HandlerList.unregisterAll(listener);
		}
		events.clear();

		for (BukkitTask task : tasks)
		{
			task.cancel();
		}
		tasks.clear();
	}
}