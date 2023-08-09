package steve6472.funnylib.minigame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

class MiniBukkitRunnable implements Runnable
{
	private BukkitTask task;
	private Consumer<BukkitTask> consumer;

	public MiniBukkitRunnable(Consumer<BukkitTask> consumer)
	{
		this.consumer = consumer;
	}

	public synchronized boolean isCancelled() throws IllegalStateException
	{
		this.checkScheduled();
		return this.task.isCancelled();
	}

	public synchronized void cancel() throws IllegalStateException
	{
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
		return this.setupTask(Bukkit.getScheduler().runTaskLater(plugin, this, delay));
	}

	@NotNull
	public synchronized BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException
	{
		this.checkNotYetScheduled();
		return this.setupTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, delay));
	}

	@NotNull
	public synchronized BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException
	{
		this.checkNotYetScheduled();
		return this.setupTask(Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period));
	}

	@NotNull
	public synchronized BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException
	{
		this.checkNotYetScheduled();
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

	@Override
	public void run()
	{
		consumer.accept(task);
	}

	@NotNull
	private BukkitTask setupTask(@NotNull BukkitTask task)
	{
		this.task = task;
		return task;
	}
}