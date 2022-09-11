package steve6472.funnylib.util;

import org.bukkit.plugin.Plugin;

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

	public static void init(Plugin plugin)
	{
		Log.plugin = plugin;
		logger = plugin.getLogger();
	}

	public static void debug(String message)
	{
		logger.fine(message);
	}

	public static void info(String message)
	{
		logger.info(message);
	}

	public static void warning(String message)
	{
		logger.warning(message);
	}

	public static void error(String message)
	{
		logger.severe(message);
	}
}
