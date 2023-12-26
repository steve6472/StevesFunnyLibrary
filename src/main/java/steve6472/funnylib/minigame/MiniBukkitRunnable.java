package steve6472.funnylib.minigame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class MiniBukkitRunnable implements Runnable
{
	private final EventHolder eventHolder;
	private BukkitTask task;
	private final Consumer<BukkitTask> consumer;
	private String name;

	public long delay = -1;
	public long period = -1;

	MiniBukkitRunnable(EventHolder eventHolder, Consumer<BukkitTask> consumer)
	{
		this.eventHolder = eventHolder;
		this.consumer = consumer;
	}

	public synchronized boolean isCancelled() throws IllegalStateException
	{
		this.checkScheduled();
		return this.task.isCancelled();
	}

	public synchronized void cancel() throws IllegalStateException
	{
		if (eventHolder != null)
		{
			eventHolder.remove(this);
		}

		Bukkit.getScheduler().cancelTask(this.getTaskId());
	}

	@NotNull
	public synchronized BukkitTask runTask(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException
	{
		this.checkNotYetScheduled();
		return this.setupTask(Bukkit.getScheduler().runTask(plugin, this));
	}

	@NotNull
	public synchronized BukkitTask runTaskAsynchronously(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException
	{
		this.checkNotYetScheduled();
		return this.setupTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, this));
	}

	@NotNull
	public synchronized BukkitTask runTaskLater(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException
	{
		this.checkNotYetScheduled();
		this.delay = delay;
		return this.setupTask(Bukkit.getScheduler().runTaskLater(plugin, this, delay));
	}

	@NotNull
	public synchronized BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException
	{
		this.checkNotYetScheduled();
		this.delay = delay;
		return this.setupTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, delay));
	}

	@NotNull
	public synchronized BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException
	{
		this.checkNotYetScheduled();
		this.delay = delay;
		this.period = period;
		return this.setupTask(Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period));
	}

	@NotNull
	public synchronized BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException
	{
		this.checkNotYetScheduled();
		this.delay = delay;
		this.period = period;
		return this.setupTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period));
	}

	public synchronized int getTaskId() throws IllegalStateException
	{
		this.checkScheduled();
		return this.task.getTaskId();
	}

	private void checkScheduled()
	{
		if (this.task == null)
		{
			throw new IllegalStateException("Not scheduled yet");
		}
	}

	private void checkNotYetScheduled()
	{
		if (this.task != null)
		{
			throw new IllegalStateException("Already scheduled as " + this.task.getTaskId());
		}
	}

	@NotNull
	public Plugin getOwner()
	{
		return task.getOwner();
	}

	public boolean isSync()
	{
		return task.isSync();
	}

	public MiniBukkitRunnable named(String name)
	{
		this.name = name;
		return this;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public void run()
	{
		// Remove from eventHolder list if this task is not repeating
		if (eventHolder != null && period == -1)
		{
			eventHolder.remove(this);
		}
		consumer.accept(task);
	}

	@NotNull
	private BukkitTask setupTask(@NotNull BukkitTask task)
	{
		this.task = task;
		return task;
	}
}