package steve6472.funnylib.command.impl;

import net.minecraft.nbt.Tag;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomChunk;
import steve6472.funnylib.command.*;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.RepeatingTask;
import steve6472.standalone.FunnyLibStandalone;

import java.util.Map;

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
		Items.ITEMS.forEach((k, v) ->
		{
			if (!v.hidden())
			{
				JSONMessage msg = JSONMessage.create("").then(k, v.requireAdmin() ? ChatColor.RED : ChatColor.WHITE);
				msg.runCommand("/gib " + k);
				msg.tooltip("Click to give");
				msg.send(player);
			}
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

	@Command
	@Description("Prints content of PersistantDataContainer of this chunk")
	@Usage("/debugChunkData")
	@Usage("[-b] -> broadcast")
	@Usage("[-s] -> disable saving")
	public static boolean debugChunkData(@NotNull Player player, @NotNull String[] args)
	{
		boolean broadcast = hasFlag("-b", args);
		boolean disableSave = hasFlag("-s", args);

		if (!disableSave && player.getLocation().getWorld() != null)
			player.getLocation().getWorld().save();

		PersistentDataContainer chunkData = player.getLocation().getChunk().getPersistentDataContainer();
		CraftPersistentDataContainer container = (CraftPersistentDataContainer) chunkData;
		Map<String, Tag> stringTagMap = container.getRaw();
		stringTagMap.forEach((k, v) ->
		{
			String msg = k + " " + v.toString();
			if (broadcast)
				Bukkit.broadcastMessage(msg);
			else
				player.sendMessage(msg);
		});
		return true;
	}

	@Command
	@Description("Prints block debug")
	@Usage("/debugChunkBlocks")
	@Usage("[-b] -> broadcast")
	public static boolean debugChunkBlocks(@NotNull Player player, @NotNull String[] args)
	{
		boolean broadcast = hasFlag("-b", args);

		if (player.getLocation().getWorld() != null)
			player.getLocation().getWorld().save();
		CustomChunk chunk = Blocks.CHUNK_MAP.get(player.getLocation().getChunk());
		chunk.blockData.forEach((k, v) -> {
			player.sendMessage(k + "(" + CustomChunk.keyToX(k) + "/" + CustomChunk.keyToY(k) + "/" + CustomChunk.keyToZ(k) + " -> " + v);
		});
		return true;
	}

	@Command
	@Description("Removes all data from custom blocks in this chunk")
	@Usage("/clearCustomBlocks")
	public static boolean clearCustomBlocks(@NotNull Player player, @NotNull String[] args)
	{
		PersistentDataContainer chunkData = player.getLocation().getChunk().getPersistentDataContainer();
		Blocks.CHUNK_MAP.remove(player.getLocation().getChunk());
		chunkData.remove(new NamespacedKey(FunnyLib.getPlugin(), "custom_blocks"));
		return true;
	}

	@Command
	@Description("Renames an item")
	@Usage("/name <new_name>")
	public static boolean name(@NotNull Player player, @NotNull String[] args)
	{
		ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
		ItemStackBuilder edit = ItemStackBuilder.edit(item);
		String originalName = edit.hasString("original_name") ? edit.getString("original_name") : null;
		if (args.length == 0)
		{
			if (originalName == null || originalName.isBlank())
				return false;
			edit.setName(JSONMessage.create(originalName));
		} else
		{
			if (originalName == null || originalName.isBlank())
			{
				edit.setString("original_name", edit.getNameLegacy());
				originalName = edit.getNameLegacy();
			}
			edit.setName(JSONMessage.create(ChatColor.AQUA + originalName + ChatColor.ITALIC + " (" + args[0] + ")"));
		}
		player.getInventory().setItem(EquipmentSlot.HAND, edit.buildItemStack());
		return true;
	}

	@Command
	@Description("Removes akmashorts chunks")
	@Usage("/clearAkmaChunks")
	public static boolean clearAkmaChunks(@NotNull Player player, @NotNull String[] args)
	{
		new RepeatingTask((JavaPlugin) FunnyLib.getPlugin(), 0, 60)
		{
			@Override
			public void run()
			{
				for (World world : Bukkit.getWorlds())
				{
					for (Chunk loadedChunk : world.getLoadedChunks())
					{
						PersistentDataContainer chunkData = loadedChunk.getPersistentDataContainer();
						Blocks.CHUNK_MAP.remove(loadedChunk);
						chunkData.remove(new NamespacedKey("akmashorts", "custom_blocks"));
					}
				}
			}
		}.sendStopMessage(player);
		return true;
	}







	// TODO: Replace with /storage <storage> command

	@Command
	@Description("Shows Markers GUI")
	@Usage("/markers")
	public static boolean markers(@NotNull Player player, @NotNull String[] args)
	{
		FunnyLibStandalone.markerStorage.showToPlayer(player);

		return true;
	}

	@Command
	@Description("Shows Structures GUI")
	@Usage("/structures")
	public static boolean structures(@NotNull Player player, @NotNull String[] args)
	{
		FunnyLibStandalone.structureStorage.showToPlayer(player);

		return true;
	}

//	@Command
//	@Usage("/printAliases")
//	public static boolean printAliases(@NotNull Player player, @NotNull String[] args)
//	{
//		String alias = ConfigurationSerialization.getAlias(ItemMeta.class);
//		player.sendMessage(alias);
//				try
//		{
//			Field field = ConfigurationSerialization.class.getDeclaredField("aliases");
//			field.setAccessible(true);
//			Map<String, Class<? extends ConfigurationSerializable>> aliases = (Map<String, Class<? extends ConfigurationSerializable>>) field.get(null);
//			aliases.forEach((k, v) -> System.out.println(k + " -> " + v));
//		} catch (IllegalAccessException | NoSuchFieldException e)
//		{
//			throw new RuntimeException(e);
//		}
//		return true;
//	}


	public static boolean hasFlag(String flag, String[] args)
	{
		for (String arg : args)
		{
			if (arg.equals(flag))
			{
				return true;
			}
		}

		return false;
	}
}
