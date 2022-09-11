package steve6472.funnylib;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import steve6472.funnylib.command.AnnotationCommand;
import steve6472.funnylib.command.BuiltInCommands;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.MarkerItem;
import steve6472.funnylib.item.events.ArmorEventListener;

/**
 * Created by steve6472
 * Date: 9/8/2022
 * Project: StevesFunnyLibrary
 */
public class FunnyLib
{
	private static final ServerTickEvent SERVER_TICK_EVENT = new ServerTickEvent();

	private static Plugin PLUGIN;
	private static long uptimeTicks;
	private static ArmorEventListener armorEventListener;

	public static void init(Plugin plugin, boolean builtInItems)
	{
		if (FunnyLib.PLUGIN != null)
			throw new RuntimeException("Plugin %s tried to initialize FunnyLib again. This is not allowed!".formatted(plugin.getName()));

		FunnyLib.PLUGIN = plugin;

		AnnotationCommand.registerCommands(BuiltInCommands.class);
		Bukkit.getPluginManager().registerEvents(armorEventListener = new ArmorEventListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new CustomCommandRunner(), plugin);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
		{
			uptimeTicks++;
			armorEventListener.tick();
			SERVER_TICK_EVENT.setUptimeTick(uptimeTicks);
			Bukkit.getPluginManager().callEvent(SERVER_TICK_EVENT);

		}, 0, 0);

		if (builtInItems)
			initBuiltin();
	}

	public static Plugin getPlugin()
	{
		return PLUGIN;
	}

	private static class CustomCommandRunner implements Listener
	{
		@EventHandler
		public void commands(PlayerCommandPreprocessEvent e)
		{
			String command = e.getMessage().substring(1);
			BukkitCommand bukkitCommand;
			AnnotationCommand.CommandData commandData;
			if (command.contains(" "))
			{
				commandData = AnnotationCommand.commands.get(command.substring(0, command.indexOf(" ")).toLowerCase());
			} else
			{
				commandData = AnnotationCommand.commands.get(command.toLowerCase());
			}
			if (commandData == null)
				return;
			bukkitCommand = commandData.executor();

			if (bukkitCommand != null)
			{
				e.setCancelled(true);

				if (command.contains(" "))
				{
					String[] split = command.split(" +");
					String[] subarray = ArrayUtils.subarray(split, 1, split.length);
					bukkitCommand.execute(e.getPlayer(), "", subarray);
				}
				else
				{
					bukkitCommand.execute(e.getPlayer(), "", new String[0]);
				}
			}
		}
	}

	/*
	 * Built-in stuff
	 */

	public static CustomItem LOCATION_MARKER;

	private static void initBuiltin()
	{
		Items.registerAdminItem(LOCATION_MARKER = new MarkerItem());
	}
}
