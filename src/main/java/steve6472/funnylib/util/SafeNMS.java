package steve6472.funnylib.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import steve6472.funnylib.FunnyLib;

import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 11/7/2022
 * Project: StevesFunnyLibrary <br>
 */
public class SafeNMS
{
	public static <R> R nmsFunction(Supplier<R> r, R failsafe)
	{
		if (FunnyLib.NMS_FAILED)
		{
			return failsafe;
		} else
		{
			try
			{
				return r.get();
			} catch (Exception exception)
			{
				Log.error("NMS Failed!");
				Bukkit.broadcastMessage(ChatColor.RED + "NMS Failed!");
				exception.printStackTrace();
				FunnyLib.NMS_FAILED = true;
				return failsafe;
			}
		}
	}

	public static void nmsFunction(Procedure r, Procedure failsafe)
	{
		if (FunnyLib.NMS_FAILED)
		{
			failsafe.apply();
		} else
		{
			try
			{
				r.apply();
			} catch (Exception exception)
			{
				Log.error("NMS Failed!");
				Bukkit.broadcastMessage(ChatColor.RED + "NMS Failed!");
				exception.printStackTrace();
				FunnyLib.NMS_FAILED = true;
				failsafe.apply();
			}
		}
	}
}
