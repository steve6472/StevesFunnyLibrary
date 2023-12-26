package steve6472.standalone.bingo;

import org.bukkit.entity.Player;
import steve6472.funnylib.minigame.EventHolder;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.funnylib.util.Preconditions;

import java.util.*;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 8/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoTaskManager
{
	private final Bingo bingo;

	private final Set<BingoTask> tasks = new HashSet<>();

	public BingoTaskManager(Bingo bingo)
	{
		this.bingo = bingo;
	}

	public void setupTasks(long randomSeed)
	{
		ArrayList<Function<Bingo, BingoTask>> tasks = new ArrayList<>(new BingoTasks().getTasks());
		Collections.shuffle(tasks, new Random(randomSeed));

		if (tasks.size() < 20)
			throw new RuntimeException("Not enough tasks! (" + tasks.size() + ")");

		for (int i = 0; i < 20; i++)
		{
			BingoTask task = tasks.get(i).apply(bingo);
			this.tasks.add(task);

			int x = i % 5;
			int y = i / 5;

			task.createAdvancement(2 + x - 8, y - 1.5f);
		}
	}

	public void enable()
	{
		tasks.forEach(BingoTask::setupEvents);
	}

	public void dispose()
	{
		tasks.forEach(EventHolder::dispose);
	}

	public Set<BingoTask> getTasks()
	{
		return tasks;
	}

	public int getTaskCount()
	{
		return getTasks().size();
	}

	public void populateUnfinishedTasks(Player player)
	{
		PdcNBT playerNBT = PdcNBT.fromPDC(player.getPersistentDataContainer());
		NBT bingoNBT = playerNBT.getOrCreateCompound(bingo.bingoKey);

		for (BingoTask task : tasks)
		{
			if (!bingoNBT.hasBoolean(task.getKey()))
			{
				bingoNBT.setBoolean(task.getKey(), false);
			} else
			{
				if (bingoNBT.getBoolean(task.getKey(), false))
				{
					bingo.advancementManager.grantAdvancement(player, task.getAdvancement());
				}
			}
		}

		playerNBT.setCompound(bingo.bingoKey, bingoNBT);
	}

	public void clearAll()
	{
		for (Player player : bingo.getGame().getPlayers())
		{
			clear(player);
		}
	}

	public void clear(Player player)
	{
		PdcNBT nbt = PdcNBT.fromPDC(player.getPersistentDataContainer());
		nbt.remove(bingo.bingoKey);
		for (BingoTask task : tasks)
		{
			bingo.advancementManager.revokeAdvancement(player, task.getAdvancement());
		}
	}

	public int countCompletedTasks(Player player)
	{
		int c = 0;

		PdcNBT nbt = PdcNBT.fromPDC(player.getPersistentDataContainer());
		NBT playerTasks = nbt.getOrCreateCompound(bingo.bingoKey);

		for (BingoTask task : tasks)
		{
			if (playerTasks.getBoolean(task.getKey(), false))
			{
				c++;
			}
		}
		return c;
	}

	public BingoTask getTask(String id)
	{
		for (BingoTask task : getTasks())
		{
			if (task.getKey().equals(id))
				return task;
		}

		return null;
	}

	public void revoke(Player player, String taskId)
	{
		BingoTask task = getTask(taskId);
		Preconditions.checkNotNull(task, "Task with id \"" + taskId + "\" does not exist");

		PdcNBT nbt = PdcNBT.fromPDC(player.getPersistentDataContainer());
		NBT playerTasks = nbt.getOrCreateCompound(bingo.bingoKey);

		playerTasks.setBoolean(task.getKey(), false);
		nbt.setCompound(bingo.bingoKey, playerTasks);
		bingo.advancementManager.revokeAdvancement(player, task.getAdvancement());
	}

	public boolean hasCompleted(Player player, String taskId)
	{
		BingoTask task = getTask(taskId);
		Preconditions.checkNotNull(task, "Task with id \"" + taskId + "\" does not exist");

		PdcNBT nbt = PdcNBT.fromPDC(player.getPersistentDataContainer());
		NBT playerTasks = nbt.getOrCreateCompound(bingo.bingoKey);
		return playerTasks.getBoolean(task.getKey(), false);
	}

	public void grant(Player player, String taskId)
	{
		BingoTask task = getTask(taskId);
		Preconditions.checkNotNull(task, "Task with id \"" + taskId + "\" does not exist");
		task.finishTask(player);
	}
}
