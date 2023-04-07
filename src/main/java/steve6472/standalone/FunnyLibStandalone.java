package steve6472.standalone;

import org.bukkit.plugin.java.JavaPlugin;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.LibSettings;
import steve6472.funnylib.category.Markers;
import steve6472.standalone.interactable.Interactable;

/**
 * Created by steve6472
 * Date: 9/12/2022
 * Project: StevesFunnyLibrary <br>
 */
public class FunnyLibStandalone extends JavaPlugin
{
	public static Markers markerStorage;

	@Override
	public void onEnable()
	{
		FunnyLib.init(this, new LibSettings());
//		ExNulla.init();
		Interactable.init();

		FunnyLib.registerConfig(markerStorage = new Markers());

		FunnyLib.load();
	}

	@Override
	public void onDisable()
	{
		FunnyLib.onUnload();
		FunnyLib.save();
	}
}
