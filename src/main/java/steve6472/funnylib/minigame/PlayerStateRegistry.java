package steve6472.funnylib.minigame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStateRegistry
{
	private final Map<String, AbstractPlayerState> states = new ConcurrentHashMap<>();

	public void registerState(AbstractPlayerState state)
	{
		states.put(state.getName(), state);
	}

	public AbstractPlayerState getState(String name)
	{
		return states.get(name);
	}

	public void dispose()
	{
		states.values().forEach(AbstractPlayerState::dispose);
	}
}