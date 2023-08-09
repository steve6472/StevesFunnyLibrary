package steve6472.standalone;

import org.bukkit.plugin.java.JavaPlugin;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.LibSettings;
import steve6472.funnylib.category.Markers;
import steve6472.funnylib.category.Structures;
import steve6472.funnylib.command.AnnotationCommand;
import steve6472.standalone.exnulla.ExNulla;
import steve6472.standalone.hideandseek.HNSStartCommand;
import steve6472.standalone.hideandseek.HideAndSeekGame;
import steve6472.standalone.interactable.Interactable;
import steve6472.standalone.machinal.Machinal;
import steve6472.standalone.tnttag.TNTTagGame;
import steve6472.standalone.tnttag.TagStartCommand;

/**
 * Created by steve6472
 * Date: 9/12/2022
 * Project: StevesFunnyLibrary <br>
 */
public class FunnyLibStandalone extends JavaPlugin
{
	public static Markers markerStorage;
	public static Structures structureStorage;

	@Override
	public void onEnable()
	{
		FunnyLib.init(this, new LibSettings());
//		ExNulla.init();
		Interactable.init();
//		Machinal.init();


		FunnyLib.registerConfig(markerStorage = new Markers());
		FunnyLib.registerConfig(structureStorage = new Structures());

		FunnyLib.load();

		AnnotationCommand.registerCommands(HNSStartCommand.class);
		AnnotationCommand.registerCommands(TagStartCommand.class);
	}

	@Override
	public void onDisable()
	{
		FunnyLib.onUnload();
		FunnyLib.save();
	}
}
