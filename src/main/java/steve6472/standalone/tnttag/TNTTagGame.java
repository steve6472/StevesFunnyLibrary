package steve6472.standalone.tnttag;

import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.minigame.builtin.phase.CountdownPhase;
import steve6472.funnylib.minigame.builtin.phase.PlaceStructure;
import steve6472.funnylib.minigame.builtin.phase.VictoryPhase;
import steve6472.funnylib.minigame.builtin.phase.WaitingForPlayersPhase;
import steve6472.funnylib.minigame.builtin.phase.composite.*;
import steve6472.funnylib.minigame.builtin.state.*;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Preconditions;
import steve6472.standalone.FunnyLibStandalone;
import steve6472.standalone.tnttag.phases.TagPhase;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class TNTTagGame extends Game
{
	public TNTTagGame(Plugin plugin, World world)
	{
		super(plugin, null, null);

		Preconditions.checkNotNull(world, "World is null!");

		GameStructure lobbyStructure = (GameStructure) FunnyLibStandalone.structureStorage.getItem("lobby");
		Preconditions.checkNotNull(lobbyStructure, "Lobby structure not found, make sure the \"lobby\" game structure exists");

		Marker lobbyLocation = (Marker) FunnyLibStandalone.markerStorage.getItem("lobby");
		Preconditions.checkNotNull(lobbyLocation, "Lobby location not found, make sure the \"lobby\" marker exists");

		Marker lobbySpawn = (Marker) FunnyLibStandalone.markerStorage.getItem("lobby_spawn");
		Preconditions.checkNotNull(lobbyLocation, "Lobby location not found, make sure the \"lobby_spawn\" marker exists");

		Marker spawn = (Marker) FunnyLibStandalone.markerStorage.getItem("spawn");
		Preconditions.checkNotNull(lobbyLocation, "Hider spawn location not found, make sure the \"hider_spawn\" marker exists");

		Scoreboard scoreboard = getScoreboard();

		Team tagTeam = scoreboard.registerNewTeam("tag");
		tagTeam.setPrefix(JSONMessage.create("[TAG] ").color(ChatColor.DARK_RED).toLegacy());
		tagTeam.setAllowFriendlyFire(false);
		tagTeam.setColor(ChatColor.RED);
		tagTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

		Team runnerTeam = scoreboard.registerNewTeam("runner");
		runnerTeam.setPrefix(JSONMessage.create("[RUNNER] ").color(ChatColor.AQUA).toLegacy());
		runnerTeam.setAllowFriendlyFire(false);
		runnerTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

		Team winnerTeam = scoreboard.registerNewTeam("winner");
		winnerTeam.setPrefix(JSONMessage.create("[WINNER] ").color(ChatColor.GOLD).toLegacy());
		winnerTeam.setAllowFriendlyFire(false);
		winnerTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

		registerState("immovable", ImmovablePlayerState::new);
		registerState("invincible", InvinciblePlayerState::new);
		registerState("spectator", SpectatorPlayerState::new);
		registerState("border_locked", BorderLockedPlayerState::new);
		registerState("adventure", AdventurePlayerState::new);

		registerState("runner", () -> new GenericTeamState("runner", runnerTeam));
		registerState("tag", () -> new GenericTeamState("tag", tagTeam));
		registerState("winner", () -> new GenericTeamState("winner", winnerTeam));

		addPhase(new PlaceStructure(this, world, lobbyStructure, lobbyLocation.x(), lobbyLocation.y(), lobbyLocation.z()));

		addPhase(
			new WaitingForPlayersPhase(this, 4, true, lobbySpawn.toLocation(world).add(0.5, 0.05, 0.5))
				.addComponent(new AddStatesOnJoinCPhase(this, "invincible", "border_locked", "adventure"))
				.addComponent(new AddStatesOnStartCPhase(this, "invincible", "border_locked", "adventure")));

		addPhase(
			new CountdownPhase(this, 5)
				.addComponent(new AddStatesOnEndCPhase(this, "runner"))
				.addComponent(new RemoveStatesOnStartCPhase(this, "adventure"))
//				.addComponent(new TeleportPlayersOnEndCPhase(this, spawn.toLocation(world).add(0.5, 0.05, 0.5)))
		);

		addPhase(new TagPhase(this));

		addPhase(
			new VictoryPhase(this, p -> getStateTracker().hasState(p, "winner"))
				.addComponent(new AddStatesOnStartCPhase(this, "spectator"))
				.addComponent(new RemoveStatesOnStartCPhase(this, "runner", "tag")));

		/*
		 * Setup world
		 */

		world.setPVP(true);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_TILE_DROPS, false);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.DO_MOB_LOOT, false);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setFullTime(6000);
		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(64);
		world.setSpawnLocation(spawn.toLocation(world));

		start();
	}
}
