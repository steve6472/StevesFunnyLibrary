package steve6472.standalone.bingo;

import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.minigame.builtin.phase.*;
import steve6472.funnylib.util.Preconditions;
import steve6472.standalone.FunnyLibStandalone;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoGame extends Game
{
	public Bingo bingo;

	public BingoGame(Plugin plugin, World world, long seed)
	{
		super(plugin, null, null);

		Marker lobbySpawn = (Marker) FunnyLibStandalone.markerStorage.getItem("lobby_spawn");
		Preconditions.checkNotNull(lobbySpawn, "Lobby location not found, make sure the \"lobby_spawn\" marker exists");

		GameStructure lobbyStructure = (GameStructure) FunnyLibStandalone.structureStorage.getItem("lobby");
		Preconditions.checkNotNull(lobbyStructure, "Lobby structure not found, make sure the \"lobby\" game structure exists");

		Marker lobbyLocation = (Marker) FunnyLibStandalone.markerStorage.getItem("lobby");
		Preconditions.checkNotNull(lobbyLocation, "Lobby location not found, make sure the \"lobby\" marker exists");

		Marker spawn = (Marker) FunnyLibStandalone.markerStorage.getItem("spawn");
		Preconditions.checkNotNull(lobbyLocation, "Spawn location not found, make sure the \"spawn\" marker exists");

		addPhase(new PlaceStructure(this, world, lobbyStructure, lobbyLocation.x(), lobbyLocation.y(), lobbyLocation.z()));

		addPhase(new WaitingForPlayersPhase(this, 20, true, lobbySpawn.toLocation(world)));

		addPhase(new CountdownPhase(this, 10));

		addPhase(new PhaseTeleportPlayers(this, spawn.toLocation(world)));

		addPhase(new DeleteStructure(this, world, lobbyStructure, lobbyLocation.x(), lobbyLocation.y(), lobbyLocation.z()));

		bingo = new Bingo(this, world.getUID(), seed);
		addPhase(bingo);

		addPhase(new VictoryPhase(this, p -> false));

		// Setup world
		world.setPVP(false);
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		world.setGameRule(GameRule.KEEP_INVENTORY, true);
		world.setDifficulty(Difficulty.EASY);
		world.setSpawnLocation(spawn.toLocation(world));

		start();
	}
}
