package steve6472.funnylib.minigame;

import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by IllusionTheDev
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class AbstractGamePhase implements GamePhase
{
	protected final Game game;
	private final List<Listener> events = new LinkedList<>();
	private final List<BukkitTask> tasks = new LinkedList<>();
	private final Set<AbstractGamePhase> components = new HashSet<>();
	// TODO: entities (store by uuid so spigot server is not angery)

	private Runnable onStart, onEnd, onCancel;

	public AbstractGamePhase(Game game)
	{
		this.game = game;
	}

	// Event logic methods

	public <EventType extends Event> void registerEvents(Class<EventType> eventClass, Consumer<EventType> handler)
	{
		EventListenerWrapper<EventType> wrapper = new EventListenerWrapper<>();
		wrapper.event = handler;

		Bukkit.getPluginManager().registerEvent(eventClass, wrapper, EventPriority.NORMAL, (listener, event) -> handler.accept((EventType) event), game.plugin);

		events.add(wrapper);
	}

	public void scheduleSyncTask(Consumer<BukkitTask> task, long delay)
	{
		tasks.add(new MiniBukkitRunnable(task).runTaskLater(game.plugin, delay));
	}

	public void scheduleAsyncTask(Consumer<BukkitTask> task, long delay)
	{
		tasks.add(new MiniBukkitRunnable(task).runTaskLaterAsynchronously(game.plugin, delay));
	}

	public void scheduleRepeatingTask(Consumer<BukkitTask> task, long delay, long period)
	{
		tasks.add(new MiniBukkitRunnable(task).runTaskTimer(game.plugin, delay, period));
	}

	public AbstractGamePhase addComponent(AbstractGamePhase component)
	{
		this.components.add(component);
		return this;
	}

	// Event state notification methods

	public void onStart(Runnable task)
	{
		this.onStart = task;
	}

	public void onCancel(Runnable task)
	{
		// This might be called by external factors, make sure to not just set a new task, but to "add" to the old one
		this.onCancel = task;
	}

	public void onEnd(Runnable task)
	{
		this.onEnd = task;
	}

	// Event state methods

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

		components.forEach(AbstractGamePhase::dispose);
	}

	public abstract void start();
	public abstract void end();

	@Override
	public void startPhase()
	{
		start();
		components.forEach(AbstractGamePhase::startPhase);
	}

	@Override
	public void endPhase()
	{
		// Call the end task
		end();
		dispose();
		components.forEach(AbstractGamePhase::endPhase);

		if (onEnd != null)
			onEnd.run();
	}

	@Override
	public void cancel()
	{
		// Call the cancel task
		dispose();
		components.forEach(AbstractGamePhase::cancel);
		if (onCancel != null)
			onCancel.run();
	}
}