package steve6472.funnylib.minigame;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.minigame.config.GameConfiguration;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
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
	final NPCRegistry npcRegistry;
	final PlayerStateRegistry stateRegistry;
	final PlayerStateTracker stateTracker;
	final Listener tick;
	final AbstractGamePhase permanentPhase;
	final Set<Pair<NamespacedKey, BossBar>> bossBars = new HashSet<>();
	final GameConfiguration configuration;

	public Game(Plugin plugin, AbstractGamePhase permanentPhase, GameConfiguration configuration)
	{
		this.plugin = plugin;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		stateRegistry = new PlayerStateRegistry();
		stateTracker = new PlayerStateTracker(this, stateRegistry);
		stateRegistry.tracker = stateTracker;

		npcRegistry = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());

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
		this.permanentPhase = permanentPhase;
		this.configuration = configuration;
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

	public NPCRegistry getNpcRegistry()
	{
		return npcRegistry;
	}

	// Phase logic

	protected void addPhase(AbstractGamePhase phase)
	{
		phases.addPhase(phase);
	}

	protected void start()
	{
		if (permanentPhase != null)
			permanentPhase.startPhase();
		phases.start();
	}

	public void dispose()
	{
		phases.dispose();
		HandlerList.unregisterAll(stateTracker);
		HandlerList.unregisterAll(tick);
		stateTracker.dispose();
		if (permanentPhase != null)
			permanentPhase.endPhase();
		npcRegistry.deregisterAll();
		bossBars.forEach(bossBarPair -> {
			bossBarPair.b().removeAll();
			Bukkit.removeBossBar(bossBarPair.a());
		});
	}

	public AbstractGamePhase getCurrentPhase()
	{
		return phases.getCurrentPhase();
	}

	// Player logic

	public Collection<? extends Player> getPlayers()
	{
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
		stateTracker.removeAll(player);
		players.remove(player.getUniqueId());
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	public boolean isPlayer(Player player)
	{
		return players.contains(player.getUniqueId());
	}

	public void registerState(String stateName, Supplier<AbstractPlayerState> state)
	{
		stateRegistry.registerState(stateName, state);
	}

	/*
	 * Game logic
	 */

	public BossBar createBossBar(@NotNull String key, @Nullable String title, @NotNull BarColor color, @NotNull BarStyle style, @NotNull BarFlag... flags)
	{
		NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
		BossBar bossBar = Bukkit.createBossBar(namespacedKey, title, color, style, flags);
		bossBars.add(new Pair<>(namespacedKey, bossBar));
		return bossBar;
	}

	public void deleteBossBar(@NotNull String key)
	{
		NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
		for (Pair<NamespacedKey, BossBar> bossBar : bossBars)
		{
			if (bossBar.a().equals(namespacedKey))
			{
				bossBar.b().removeAll();
				Bukkit.removeBossBar(namespacedKey);
				return;
			}
		}
	}

	public GameConfiguration getConfig()
	{
		return configuration;
	}

	public void broadcast(JSONMessage message)
	{
		message.send(getPlayers());
	}
}