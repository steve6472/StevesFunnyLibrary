package steve6472.funnylib.minigame;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.util.JSONMessage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by IllusionTheDev
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class Game
{
	final Scoreboard scoreboard;
	final Plugin plugin;
	final PhaseChain phases = new PhaseChain();
	final Set<UUID> players = new HashSet<>();
	final PlayerStateRegistry stateRegistry = new PlayerStateRegistry();
	final PlayerStateTracker stateTracker = new PlayerStateTracker(this, stateRegistry);
	final Listener tick;

	public Game(Plugin plugin)
	{
		this.plugin = plugin;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Bukkit.getPluginManager().registerEvents(stateTracker, plugin);
		Bukkit.getPluginManager().registerEvents(tick = new Listener()
		{
			@EventHandler
			public void tick(ServerTickEvent e)
			{
				AbstractGamePhase currentPhase = phases.getCurrentPhase();
				if (currentPhase != null)
				{
					currentPhase.tick();
				}
			}
		}, plugin);
	}

	public Plugin getPlugin()
	{
		return plugin;
	}

	public PlayerStateRegistry getStateRegistry()
	{
		return stateRegistry;
	}

	public PlayerStateTracker getStateTracker()
	{
		return stateTracker;
	}

	public Scoreboard getScoreboard()
	{
		return scoreboard;
	}

	// Phase logic

	protected void addPhase(AbstractGamePhase phase)
	{
		phases.addPhase(phase);
	}

	protected void start()
	{
		phases.start();
	}

	public void dispose()
	{
		phases.dispose();
		HandlerList.unregisterAll(stateTracker);
		HandlerList.unregisterAll(tick);
		stateRegistry.dispose();
	}

	// Player logic

	public Collection<? extends Player> getPlayers()
	{
		// Up to you
		return Bukkit.getOnlinePlayers().stream().filter(p -> this.players.contains(p.getUniqueId())).collect(Collectors.toSet());
	}

	public Collection<UUID> getPlayerUUIDs()
	{
		return players;
	}

	public void addPlayer(Player player)
	{
		players.add(player.getUniqueId());
		player.setScoreboard(getScoreboard());
	}

	public void removePlayer(Player player)
	{
		players.remove(player.getUniqueId());
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	public boolean isPlayer(Player player)
	{
		return players.contains(player.getUniqueId());
	}

	public void registerState(AbstractPlayerState state)
	{
		stateRegistry.registerState(state);
		state.tracker = stateTracker;
	}

	public void broadcast(JSONMessage message)
	{
		message.send(getPlayers());
	}
}