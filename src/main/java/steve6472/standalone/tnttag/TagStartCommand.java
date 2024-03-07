package steve6472.standalone.tnttag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Usage;
import steve6472.funnylib.minigame.EventListenerWrapper;
import steve6472.standalone.FunnyLibStandalone;
import steve6472.standalone.hideandseek.HideAndSeekGame;

import java.util.ArrayList;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class TagStartCommand
{
	@Command
	@Usage("/printEvents")
	public static boolean printEvents(@NotNull Player player, @NotNull String[] args)
	{
		if (!player.getName().equals("steve6472") && !player.getName().equals("akmatras"))
			return false;

		ArrayList<RegisteredListener> registeredListeners = HandlerList.getRegisteredListeners(FunnyLib.getPlugin());
		for (RegisteredListener listener : registeredListeners)
		{
			if (listener.getListener() instanceof EventListenerWrapper<?> wrapper)
			{
				if (wrapper.clazz == null)
				{
					Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Wrapped: unknownEvent");
				} else
				{
					Bukkit.broadcastMessage(ChatColor.AQUA + "Wrapped: " + wrapper.clazz.getSimpleName());
				}
			} else
			{
//				Bukkit.broadcastMessage(listener.getPriority() + " " + listener.getListener().getClass().getSimpleName());
			}
		}

		return true;
	}
}
