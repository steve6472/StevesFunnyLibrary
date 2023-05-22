package steve6472.funnylib.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import steve6472.funnylib.FunnyLib;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/9/2022
 * Project: AkmaShorts
 */
public class Log
{
	private static Plugin plugin;
	private static Logger logger;

	public static boolean ERROR = true;
	public static boolean WARNING = false;
	public static boolean INFO = false;
	public static boolean DEBUG = false;

	public static void init(Plugin plugin)
	{
		Log.plugin = plugin;
		logger = plugin.getLogger();
	}

	public static void debug(String message)
	{
		logger.fine(message);
		if (DEBUG)
		{
			Bukkit.broadcastMessage(ChatColor.GRAY + "[" + FunnyLib.getUptimeTicks() + "] " + ChatColor.GRAY + "[Debug] " + message);
		}
	}

	public static void info(String message)
	{
		logger.info(message);
		if (INFO)
		{
			Bukkit.broadcastMessage(ChatColor.GRAY + "[" + FunnyLib.getUptimeTicks() + "] " + ChatColor.AQUA + "[Info] " + message);
		}
	}

	public static void warning(String message)
	{
		logger.warning(message);
		if (WARNING)
		{
			Bukkit.broadcastMessage(ChatColor.GRAY + "[" + FunnyLib.getUptimeTicks() + "] " + ChatColor.YELLOW + "[Warning] " + message);
		}
	}

	public static void error(String message)
	{
		logger.severe(message);
		if (ERROR)
		{
			Bukkit.broadcastMessage(ChatColor.GRAY + "[" + FunnyLib.getUptimeTicks() + "] " + ChatColor.RED + "[Error] " + message);
		}
	}
}
