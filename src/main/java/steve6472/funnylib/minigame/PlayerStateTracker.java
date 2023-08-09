package steve6472.funnylib.minigame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStateTracker implements Listener
{
	private final Map<UUID, List<String>> playerStates = new ConcurrentHashMap<>();
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
		String stateName = state.getName();

		List<String> states = playerStates.computeIfAbsent(playerId, irrelevant -> new ArrayList<>());

		// Optional
		if (states.contains(stateName))
		{
			return;
		}

		// Apply the state and add to the list
		states.add(stateName);
		state.apply(player);

		// Feel free to save this list to PDC in case an instance crashes
	}

	public void addState(Player player, String stateName)
	{
		addState(player, registry.getState(stateName));
	}

	public void removeState(Player player, AbstractPlayerState state)
	{
		if (state == null)
		{ // idiot proof
			return;
		}

		UUID playerId = player.getUniqueId();
		String stateName = state.getName();

		List<String> states = playerStates.getOrDefault(playerId, Collections.EMPTY_LIST);

		if (states.isEmpty() || !states.contains(stateName))
		{
			return;
		}

		states.remove(stateName);

		if (states.isEmpty())
		{
			playerStates.remove(playerId);
		}

		state.revert(player);
		state.dispose();
	}

	public void removeState(Player player, String stateName)
	{
		removeState(player, registry.getState(stateName));
	}

	public boolean hasState(Player player, String state)
	{
		return playerStates.getOrDefault(player.getUniqueId(), Collections.EMPTY_LIST).contains(state);
	}

	public void removeAll(Player player)
	{
		UUID playerId = player.getUniqueId();
		List<String> states = this.playerStates.remove(playerId);

		if (states == null)
		{
			return;
		}

		// Apply reverse order just in case
		Collections.reverse(states);

		for (String state : states)
		{
			AbstractPlayerState abstractState = registry.getState(state);

			if (abstractState == null)
			{ // ??
				continue;
			}

			abstractState.revert(player);
			abstractState.dispose();
		}
	}

	public Map<UUID, List<String>> getPlayerStates()
	{
		return playerStates;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		removeAll(event.getPlayer());
	}
}