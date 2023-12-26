package steve6472.funnylib.minigame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class PlayerStateRegistry
{
	PlayerStateTracker tracker;
	private final Map<String, Supplier<AbstractPlayerState>> states = new ConcurrentHashMap<>();

	public void registerState(String stateName, Supplier<AbstractPlayerState> state)
	{
		states.put(stateName, state);
	}

	public AbstractPlayerState getState(String name)
	{
		AbstractPlayerState abstractPlayerState = states.get(name).get();
		abstractPlayerState.tracker = tracker;
		return abstractPlayerState;
	}
}