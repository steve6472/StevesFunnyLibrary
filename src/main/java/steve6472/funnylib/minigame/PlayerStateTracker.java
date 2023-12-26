package steve6472.funnylib.minigame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStateTracker implements Listener
{
	private final Map<UUID, List<AbstractPlayerState>> playerStates = new ConcurrentHashMap<>();
	private final PlayerStateRegistry registry;
	public final Game game;

	public PlayerStateTracker(Game game, PlayerStateRegistry registry)
	{
		this.registry = registry;
		this.game = game;

		// Register events or something
	}

	public void addState(Player player, AbstractPlayerState state)
	{
		if (state == null)
		{ // idiot proof
			return;
		}

		UUID playerId = player.getUniqueId();

		List<AbstractPlayerState> states = playerStates.computeIfAbsent(playerId, irrelevant -> new ArrayList<>());

		// Optional
		if (states.stream().anyMatch(s -> s.getName().equals(state.getName())))
		{
			return;
		}

		// Apply the state and add to the list
		states.add(state);
		state.apply(player);

		// Feel free to save this list to PDC in case an instance crashes
	}

	public void addState(Player player, String stateName)
	{
		addState(player, registry.getState(stateName));
	}

	public void removeState(Player player, String stateName)
	{
		if (stateName == null || stateName.isBlank())
		{ // idiot proof
			return;
		}

		UUID playerId = player.getUniqueId();

		List<AbstractPlayerState> states = playerStates.getOrDefault(playerId, Collections.EMPTY_LIST);

		if (states.isEmpty() || states.stream().noneMatch(s -> s.getName().equals(stateName)))
		{
			return;
		}

		for (Iterator<AbstractPlayerState> iterator = states.iterator(); iterator.hasNext(); )
		{
			AbstractPlayerState state = iterator.next();

			if (state.getName().equals(stateName))
			{
				state.revert(player);
				state.dispose();
				iterator.remove();
			}
		}

		if (states.isEmpty())
		{
			playerStates.remove(playerId);
		}
	}

	public boolean hasState(Player player, String state)
	{
//		return playerStates.getOrDefault(player.getUniqueId(), Collections.EMPTY_LIST).stream().contains(state);
		return playerStates.getOrDefault(player.getUniqueId(), new ArrayList<>()).stream().anyMatch(s -> s.getName().equals(state));
	}

	public void removeAll(Player player)
	{
		UUID playerId = player.getUniqueId();
		List<AbstractPlayerState> states = this.playerStates.remove(playerId);

		if (states == null)
		{
			return;
		}

		// Apply reverse order just in case
		Collections.reverse(states);

		for (AbstractPlayerState state : states)
		{
			state.revert(player);
			state.dispose();
		}
	}

	public Map<UUID, List<AbstractPlayerState>> getPlayerStates()
	{
		return playerStates;
	}

	public void dispose()
	{
		game.getPlayers().forEach(this::removeAll);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event)
	{
		removeAll(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event)
	{
		game.addPlayer(event.getPlayer());
	}
}