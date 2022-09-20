package steve6472.standalone;

import org.bukkit.plugin.java.JavaPlugin;
import steve6472.funnylib.FunnyLib;
import steve6472.standalone.exnulla.ExNulla;
import steve6472.standalone.interactable.Interactable;

/**
 * Created by steve6472
 * Date: 9/12/2022
 * Project: StevesFunnyLibrary <br>
 */
public class FunnyLibStandalone extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		FunnyLib.init(this, true);
		ExNulla.init();
		Interactable.init();
	}

	@Override
	public void onDisable()
	{
		FunnyLib.onUnload();
	}
}
