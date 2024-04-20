package steve6472.funnylib.minigame;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.minigame.config.ConfigTypeRegistry;
import steve6472.funnylib.minigame.config.GameConfiguration;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.TriFunction;
import steve6472.standalone.buildbattle.BuildBattleGame;
import steve6472.standalone.buildbattle.items.LimitedRectangleFillerItem;
import steve6472.standalone.buildbattle.items.LimitedSphereFillerItem;
import steve6472.standalone.tnttag.TNTTagGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 12/20/2023
 * Project: StevesFunnyLibrary <br>
 */
public class Minigames
{
	public static final DynamicCommandExceptionType ERROR_MINIGAME_SPECIFIC = new DynamicCommandExceptionType((var0) -> new LiteralMessage("Minigame specific command!\nMinigame not in progress."));

	public final ConfigTypeRegistry configTypeRegistry;
	public final List<GameConfiguration> games;

	public static CustomItem FILL_RECTANGLE_LIMITED;
	public static CustomItem FILL_SPHERE_LIMITED;

	public Minigames()
	{
		configTypeRegistry = new ConfigTypeRegistry();
		games = new ArrayList<>();

		registerMinigames();

		initMinigamesItems();
	}

	private void initMinigamesItems()
	{
		FILL_RECTANGLE_LIMITED = Items.registerItem(new LimitedRectangleFillerItem());
		FILL_SPHERE_LIMITED = Items.registerItem(new LimitedSphereFillerItem());
	}

	private GameConfiguration registerMinigame(String id, TriFunction<Plugin, World, GameConfiguration, Game> init)
	{
		GameConfiguration config = new GameConfiguration(configTypeRegistry, id, init);
		games.add(config);
		return config;
	}

	public void registerMinigames()
	{
		registerMinigame("build_battle", BuildBattleGame::new)
			.setName(JSONMessage.create("Build Battle"))
			.setDescription(JSONMessage.create("You build... that's about it"))
			.registerValue(BuildBattleGame.PLOT)
			.registerValue(BuildBattleGame.PLOT_PLACE_OFFSET)
			.registerValue(BuildBattleGame.BUILD_TIME)
			.registerValue(BuildBattleGame.CENTER)
			.registerValue(BuildBattleGame.PLOT_BUILD_SIZE)
			.registerValue(BuildBattleGame.PLOT_BUILD_OFFSET)
			.registerValue(BuildBattleGame.BARRIER_OFFSET)
			.registerValue(BuildBattleGame.BARRIER_SIZE)
			.registerValue(BuildBattleGame.BARRIER_CAP_TOP)
			.registerValue(BuildBattleGame.THEMES)
		;

		// Does not work lol
		registerMinigame("tnt_tag", TNTTagGame::new)
			.setName(JSONMessage.create("TNT Tag (Not working)"))
			.setDescription(JSONMessage.create("Run from [IT]"))
			.registerValue(TNTTagGame.LOBBY_STRUCTURE)
			.registerValue(TNTTagGame.LOBBY_LOCATION)
			.registerValue(TNTTagGame.LOBBY_SPAWN)
			.registerValue(TNTTagGame.HIDER_SPAWN)
			.registerValue(TNTTagGame.PLAYERCOUNT)
			.registerValue(TNTTagGame.BORDER_SIZE)
		;
	}
}
