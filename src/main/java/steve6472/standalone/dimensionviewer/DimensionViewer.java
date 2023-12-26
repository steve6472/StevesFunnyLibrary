package steve6472.standalone.dimensionviewer;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/13/2023
 * Project: StevesFunnyLibrary <br>
 */
public class DimensionViewer implements Listener
{
	public static double scale = 1d;
	public static int refreshTime = 60;

	public Map<UUID, ViewController> controllers = new HashMap<>();

	public DimensionViewer()
	{
		init();

		Bukkit.getPluginManager().registerEvents(this, FunnyLib.getPlugin());
	}

	public static CustomItem DIMENSINAL_EYE;

	public void init()
	{
		Items.registerItem(DIMENSINAL_EYE = new DimentionalEye(this));
	}

	public void onUnload()
	{
		controllers.values().forEach(ViewController::delete);
		controllers.clear();
	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent e)
	{
		ViewController controller = controllers.remove(e.getPlayer().getUniqueId());
		if (controller != null)
			controller.delete();
	}

	@EventHandler
	public void playerLeave(PlayerChangedWorldEvent e)
	{
		ViewController controller = controllers.remove(e.getPlayer().getUniqueId());
		if (controller != null)
			controller.delete();
	}
}
