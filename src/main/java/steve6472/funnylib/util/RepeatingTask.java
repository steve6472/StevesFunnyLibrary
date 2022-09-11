package steve6472.funnylib.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import steve6472.funnylib.FunnyLib;

import java.util.HashMap;

public abstract class RepeatingTask implements Runnable, Listener
{
	public static final HashMap<Integer, RepeatingTask> TASK_HASH_MAP = new HashMap<>();

	private final int taskId;

	public RepeatingTask(JavaPlugin plugin, int delay, int period)
	{
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, delay, period);
		Bukkit.getPluginManager().registerEvents(this, FunnyLib.getPlugin());
		TASK_HASH_MAP.put(taskId, this);
	}

	public void cancel()
	{
		Bukkit.getScheduler().cancelTask(taskId);
		HandlerList.unregisterAll(this);
		TASK_HASH_MAP.remove(taskId);
	}

	public RepeatingTask sendStopMessage(Player player)
	{
		Messages.STOP_TASK.apply(taskId).send(player);
		return this;
	}

	public int getTaskId()
	{
		return taskId;
	}
}