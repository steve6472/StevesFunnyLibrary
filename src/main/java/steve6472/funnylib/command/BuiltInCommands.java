package steve6472.funnylib.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.RepeatingTask;

/**
 * Created by steve6472
 * Date: 9/8/2022
 * Project: StevesFunnyLibrary
 */
public class BuiltInCommands
{
	@Command
	@Description("Shows all custom commands")
	@Usage("/cmds")
	@Usage("Arguments in <> are required")
	@Usage("Arguments in [] are optional")
	public static boolean cmds(@NotNull Player player, @NotNull String[] args)
	{
		AnnotationCommand.commands.forEach((k, v) ->
		{
			if (!v.hidden())
			{
				JSONMessage tooltip = JSONMessage.create("Click to show details").newline().newline();

				JSONMessage[] description = AnnotationCommand.sendDescription(null, k);

				for (JSONMessage s : description)
				{
					tooltip.then(s).newline();
				}

				tooltip.newline();

				String[] usage = AnnotationCommand.sendUsage(null, k);

				for (int i = 0; i < usage.length; i++)
				{
					String s = usage[i];
					tooltip.then(s);

					// Prevent empty line at the end
					if (i < usage.length - 1)
						tooltip.newline();
				}

				JSONMessage click = JSONMessage
					.create("/" + k)
					.tooltip(tooltip)
					.runCommand("/showCommandInfo " + k);

				click.send(player);
			}
		});

		return true;
	}

	@Command
	@Hidden
	public static boolean showCommandInfo(@NotNull Player player, @NotNull String[] args)
	{
		String command = args[0];
		AnnotationCommand.CommandData data = AnnotationCommand.commands.get(command);

		if (data.hidden())
			return false;

		player.sendMessage("");
		JSONMessage.create("/" + command).color(ChatColor.AQUA).suggestCommand("/" + command).tooltip("Click to suggest command").send(player);
		AnnotationCommand.sendDescription(player, command);
		AnnotationCommand.sendUsage(player, command);
		player.sendMessage("");

		return true;
	}

	@Command
	@Description("Stops any task")
	@Usage("/stoptask <taskId>")
	public static boolean stopTask(@NotNull Player player, @NotNull String[] args)
	{
		int taskId = Integer.parseInt(args[0]);
		player.sendMessage("Stopping task " + taskId);
		RepeatingTask bukkitWorker = RepeatingTask.TASK_HASH_MAP.get(taskId);
		if (bukkitWorker != null)
		{
			bukkitWorker.cancel();
		}
		Bukkit.getScheduler().cancelTask(taskId);

		return true;
	}

	@Command
	@Description("Shows task ids")
	@Usage("/showtasks")
	public static boolean showtasks(@NotNull Player player, @NotNull String[] args)
	{
		StringBuilder sb = new StringBuilder();

		for (Integer integer : RepeatingTask.TASK_HASH_MAP.keySet())
		{
			sb.append(integer).append(", ");
		}

		player.sendMessage(sb.toString());

		return true;
	}

	@Command
	@Description("Creates a stone platform")
	@Usage("/platform [block]")
	public static boolean platform(@NotNull Player player, @NotNull String[] args)
	{
		Material type = Material.STONE;
		if (args.length > 0)
			type = Material.valueOf(args[0].toUpperCase());

		for (int i = -16; i <= 16; i++)
		{
			for (int k = -16; k <= 16; k++)
			{
				player
					.getWorld()
					.getBlockAt(player.getLocation().getBlockX() + i, player.getLocation().getBlockY() - 1, player
						.getLocation()
						.getBlockZ() + k)
					.setType(type);
			}
		}

		return true;
	}

	@Command
	@Description("Prints information about items")
	@Usage("/funnyItemDebug")
	public static boolean funnyItemDebug(@NotNull Player player, @NotNull String[] args)
	{
		Items.ITEMS.forEach((k, v) -> {

			JSONMessage msg = JSONMessage.create(k + ": ");


			msg.send(player);
		});

		return true;
	}

	@Command
	@Description("Gibs custom item")
	@Usage("/gib <customItem")
	public static boolean gib(@NotNull Player player, @NotNull String[] args)
	{
		player.getInventory().addItem(Items.ITEMS.get(args[0]).customItem().newItemStack());
		return true;
	}
}
