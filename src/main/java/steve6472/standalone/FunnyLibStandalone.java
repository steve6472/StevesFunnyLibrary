package steve6472.standalone;

import org.bukkit.plugin.java.JavaPlugin;
import steve6472.brigit.Brigit;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.LibSettings;
import steve6472.funnylib.category.Markers;
import steve6472.funnylib.category.Structures;
import steve6472.funnylib.minigame.MinigameCommand;
import steve6472.funnylib.minigame.Minigames;
import steve6472.standalone.bingo.BingoCommand;
import steve6472.standalone.buildbattle.AddDummyPlayer;
import steve6472.standalone.buildbattle.commands.PlotCommand;
import steve6472.standalone.buildbattle.commands.SkullCommand;
import steve6472.standalone.buildbattle.commands.TopCommand;
import steve6472.standalone.dimensionviewer.DimensionViewer;
import steve6472.standalone.interactable.Interactable;

/**
 * Created by steve6472
 * Date: 9/12/2022
 * Project: StevesFunnyLibrary <br>
 */
public class FunnyLibStandalone extends JavaPlugin
{
	public static Markers markerStorage;
	public static Structures structureStorage;

	private DimensionViewer dimensionViewer;
	public static Minigames minigames;

	@Override
	public void onEnable()
	{
		Brigit.removeCommands(this);
		FunnyLib.init(this, new LibSettings());
//		ExNulla.init();
		Interactable.init();
//		Machinal.init();
//		dimensionViewer = new DimensionViewer();


		FunnyLib.registerConfig(markerStorage = new Markers());
		FunnyLib.registerConfig(structureStorage = new Structures());

		FunnyLib.load();

//		AnnotationCommand.registerCommands(HNSStartCommand.class);
//		AnnotationCommand.registerCommands(TagStartCommand.class);

		Brigit.addBrigitCommand(this, new BingoCommand(this));
		Brigit.addBrigitCommand(this, new MinigameCommand());
//		Brigit.addBrigitCommand(this, new VarCommand(this));

		minigames = new Minigames();

		/*
		 * build battle commands
		 */

		Brigit.addBrigitCommand(FunnyLib.getPlugin(), new SkullCommand());
		Brigit.addBrigitCommand(FunnyLib.getPlugin(), new PlotCommand());
		Brigit.addBrigitCommand(FunnyLib.getPlugin(), new TopCommand());

		if (FunnyLib.isDev())
			Brigit.addBrigitCommand(FunnyLib.getPlugin(), new AddDummyPlayer());
	}

	@Override
	public void onDisable()
	{
		FunnyLib.onUnload();
		FunnyLib.save();

		if (dimensionViewer != null)
		{
			dimensionViewer.onUnload();
		}
	}
}
