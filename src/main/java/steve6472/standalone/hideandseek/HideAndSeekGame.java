package steve6472.standalone.hideandseek;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.minigame.builtin.phase.*;
import steve6472.funnylib.minigame.builtin.phase.composite.AddStatesOnJoinCPhase;
import steve6472.funnylib.minigame.builtin.phase.composite.AddStatesOnStartCPhase;
import steve6472.funnylib.minigame.builtin.phase.composite.RemoveStatesOnEndCPhase;
import steve6472.funnylib.minigame.builtin.state.*;
import steve6472.funnylib.util.Preconditions;
import steve6472.standalone.FunnyLibStandalone;
import steve6472.standalone.hideandseek.phases.HidingPhase;
import steve6472.standalone.hideandseek.phases.SeekingPhase;
import steve6472.standalone.hideandseek.playerstate.*;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class HideAndSeekGame extends Game
{
	public HideAndSeekGame(Plugin plugin, World world, int worldBorderSize)
	{
		/*
		 * Don't forget to enable the other dimensions in server config
		 */

		super(plugin);

		Preconditions.checkNotNull(world, "World is null!");

		GameStructure lobbyStructure = (GameStructure) FunnyLibStandalone.structureStorage.getItem("lobby");
		Preconditions.checkNotNull(lobbyStructure, "Lobby structure not found, make sure the \"lobby\" game structure exists");

		Marker lobbyLocation = (Marker) FunnyLibStandalone.markerStorage.getItem("lobby");
		Preconditions.checkNotNull(lobbyLocation, "Lobby location not found, make sure the \"lobby\" marker exists");

		Marker lobbySpawn = (Marker) FunnyLibStandalone.markerStorage.getItem("lobby_spawn");
		Preconditions.checkNotNull(lobbyLocation, "Lobby location not found, make sure the \"lobby_spawn\" marker exists");

		Marker spawn = (Marker) FunnyLibStandalone.markerStorage.getItem("hider_spawn");
		Preconditions.checkNotNull(lobbyLocation, "Hider spawn location not found, make sure the \"hider_spawn\" marker exists");

		registerState(new ImmovablePlayerState());
		registerState(new InvinciblePlayerState());
		registerState(new SpectatorPlayerState());
		registerState(new BorderLockedPlayerState());
		registerState(new GlowingPlayerState());

		registerState(new HiderPlayerState());
		registerState(new HiderHidingPlayerState());
		registerState(new SeekerPlayerState());
		registerState(new SeekerWaitingPlayerState());
		registerState(new AdventurePlayerState());

		addPhase(new PlaceStructure(this, world, lobbyStructure, lobbyLocation.x(), lobbyLocation.y(), lobbyLocation.z()));

		addPhase(new WaitingForPlayersPhase(this, 4, true, lobbySpawn.toLocation(world))
			.addComponent(new AddStatesOnJoinCPhase(this, "invincible", "border_locked", "adventure"))
			.addComponent(new AddStatesOnStartCPhase(this, "invincible", "border_locked", "adventure")));

		addPhase(new CountdownPhase(this, 5)
			.addComponent(new RemoveStatesOnEndCPhase(this, "adventure")));

		addPhase(new DeleteStructure(this, world, lobbyStructure, lobbyLocation.x(), lobbyLocation.y(), lobbyLocation.z()));

		addPhase(new HidingPhase(this, world, spawn, 2 * 60 * 20));

		addPhase(new SeekingPhase(this));

		addPhase(new VictoryPhase(this, p -> !getStateTracker().hasState(p, "spectator") && !p.getName().equals("akmatras")));

		setupWorld(world, worldBorderSize, lobbySpawn.toLocation(world));
		setupWorld(Bukkit.getWorld(world.getName() + "_nether"), worldBorderSize, lobbySpawn.toLocation(world));
		setupWorld(Bukkit.getWorld(world.getName() + "_the_end"), worldBorderSize, lobbySpawn.toLocation(world));

		Bukkit.setWhitelist(true);

		start();
	}

	private void setupWorld(World world, int worldBorderSize, Location worldSpawn)
	{
		Preconditions.checkNotNull(world, "World not found");

		world.setPVP(true);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
		world.setGameRule(GameRule.KEEP_INVENTORY, true);
		world.setGameRule(GameRule.DO_TILE_DROPS, true);
		world.setFullTime(0);
		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(worldBorderSize);
		world.setSpawnLocation(worldSpawn);

	}
}
