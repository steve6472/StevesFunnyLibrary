package steve6472.funnylib.minigame;

import steve6472.funnylib.minigame.config.ConfigTypeRegistry;
import steve6472.funnylib.minigame.config.GameConfiguration;
import steve6472.funnylib.util.JSONMessage;
import steve6472.standalone.buildbattle.BuildBattleGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 12/20/2023
 * Project: StevesFunnyLibrary <br>
 */
public class Minigames
{
	public final ConfigTypeRegistry configTypeRegistry;
	public final List<GameConfiguration> games;

	public Minigames()
	{
		configTypeRegistry = new ConfigTypeRegistry();
		games = new ArrayList<>();

		registerMinigames();
	}

	private GameConfiguration registerMinigame(String id)
	{
		GameConfiguration config = new GameConfiguration(configTypeRegistry, id);
		games.add(config);
		return config;
	}

	public void registerMinigames()
	{
		registerMinigame("build_battle")
			.setName(JSONMessage.create("Build Battle"))
			.setDescription(JSONMessage.create("You build... that's about it"))
			.registerValue(BuildBattleGame.PLOT)
			.registerValue(BuildBattleGame.TEST_STRING)
			.registerValue(BuildBattleGame.BUILD_TIME)
			.registerValue(BuildBattleGame.CENTER)
		;

//		registerMinigame("tnt_tag")
//			.registerStructure("Lobby", "lobby")
//			.registerMarker("Lobby Location", "lobby_loc")
//			.registerMarker("Lobby Spawn", "lobby_spawn")
//			.registerMarker("Hider Spawn", "hider_spawn")
//		;
	}


}
