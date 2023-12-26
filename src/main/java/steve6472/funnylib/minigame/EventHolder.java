package steve6472.funnylib.minigame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import steve6472.funnylib.util.JSONMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class EventHolder
{
	private final Plugin plugin;

	public EventHolder(Plugin plugin)
	{
		this.plugin = plugin;
	}

	private final List<Listener> events = new LinkedList<>();
	private final List<MiniBukkitRunnable> tasks = new LinkedList<>();

	public <EventType extends Event> void registerEvent(Class<EventType> eventClass, Consumer<EventType> handler)
	{
		EventListenerWrapper<EventType> wrapper = new EventListenerWrapper<>();
		wrapper.event = handler;
		wrapper.clazz = eventClass;

		Bukkit.getPluginManager().registerEvent(eventClass, wrapper, EventPriority.NORMAL, (listener, event) ->
		{
			if (eventClass.isAssignableFrom(event.getClass()))
				handler.accept((EventType) event);
		}, plugin);

		events.add(wrapper);
	}

	public MiniBukkitRunnable scheduleSyncTask(Consumer<BukkitTask> task, long delay)
	{
		MiniBukkitRunnable miniBukkitRunnable = new MiniBukkitRunnable(this, task);
		miniBukkitRunnable.runTaskLater(plugin, delay);
		tasks.add(miniBukkitRunnable);
		return miniBukkitRunnable;
	}

	public MiniBukkitRunnable scheduleAsyncTask(Consumer<BukkitTask> task, long delay)
	{
		MiniBukkitRunnable miniBukkitRunnable = new MiniBukkitRunnable(this, task);
		miniBukkitRunnable.runTaskLaterAsynchronously(plugin, delay);
		tasks.add(miniBukkitRunnable);
		return miniBukkitRunnable;
	}

	public MiniBukkitRunnable scheduleRepeatingTask(Consumer<BukkitTask> task, long delay, long period)
	{
		MiniBukkitRunnable miniBukkitRunnable = new MiniBukkitRunnable(this, task);
		miniBukkitRunnable.runTaskTimer(plugin, delay, period);
		tasks.add(miniBukkitRunnable);
		return miniBukkitRunnable;
	}

	public void dispose()
	{
		for (Listener listener : events)
		{
			HandlerList.unregisterAll(listener);
		}
		events.clear();

		for (MiniBukkitRunnable task : tasks)
		{
			task.cancel();
		}
		tasks.clear();
	}

	void remove(MiniBukkitRunnable runnable)
	{
		tasks.remove(runnable);
	}

	/*
	 * Debug:
	 */

	public void debugTasks(CommandSender sender)
	{
		JSONMessage jsonMessage = JSONMessage.create("Tasks for " + getClass().getSimpleName());

		// Sync tasks
		List<MiniBukkitRunnable> syncTasks = tasks.stream().filter(MiniBukkitRunnable::isSync).toList();
		jsonMessage.newline().then("[").then("Sync", ChatColor.GRAY).then("]");
		addTasks(jsonMessage, syncTasks);

		// Async tasks
		List<MiniBukkitRunnable> asyncTasks = tasks.stream().filter(t -> !t.isSync()).toList();
		jsonMessage.newline().then("[").then("Async", ChatColor.GRAY).then("]");
		addTasks(jsonMessage, asyncTasks);

		jsonMessage.send(sender);
	}

	private void addTasks(JSONMessage message, Collection<MiniBukkitRunnable> tasks)
	{
		message.newline().then("  [").then("Run Now", ChatColor.GRAY).then("]");
		tasks.stream().filter(task -> task.delay == -1 && task.period == -1)
			.forEach(task -> message.newline()
			.then(decideNameOrTag(task)).then("" + task.getTaskId()));

		message.newline().then("  [").then("Run Later", ChatColor.GRAY).then("]");
		tasks.stream().filter(task -> task.delay != -1 && task.period == -1).forEach(task -> message.newline()
			.then(decideNameOrTag(task)).then("" + task.getTaskId(), ChatColor.DARK_AQUA)
			.then("  Delay: ", ChatColor.GRAY).then("" + task.delay, ChatColor.BLUE));

		message.newline().then("  [").then("Run Timer", ChatColor.GRAY).then("]");
		tasks.stream().filter(task -> task.delay != -1 && task.period != -1).forEach(task -> message.newline()
			.then(decideNameOrTag(task))
			.then("" + task.getTaskId(), ChatColor.DARK_AQUA)
			.then("  Delay: ", ChatColor.GRAY).then("" + task.delay, ChatColor.BLUE)
			.then("  Period: ", ChatColor.GRAY).then("" + task.period, ChatColor.BLUE));
	}

	private JSONMessage decideNameOrTag(MiniBukkitRunnable task)
	{
		return JSONMessage.create(task.getName() == null ? "    Task ID: " : "    " + task.getName() + ": ").color(task.getName() == null ? ChatColor.GRAY : ChatColor.GOLD);
	}

	public void debugEvents(CommandSender sender)
	{
		JSONMessage jsonMessage = JSONMessage.create("Events for " + getClass().getSimpleName());
		for (Listener event : events)
		{
			if (event instanceof EventListenerWrapper<?> wrapper)
			{
				jsonMessage.newline().then("  ").then(wrapper.clazz.getSimpleName());
			} else
			{
				jsonMessage.newline().then("  ").then("Simple listener");
			}
		}

		jsonMessage.send(sender);
	}
}
