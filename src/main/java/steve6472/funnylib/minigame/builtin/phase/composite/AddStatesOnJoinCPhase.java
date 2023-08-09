package steve6472.funnylib.minigame.builtin.phase.composite;

import org.bukkit.event.player.PlayerJoinEvent;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.Preconditions;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class AddStatesOnJoinCPhase extends AbstractGamePhase
{
	private final String[] states;

	public AddStatesOnJoinCPhase(Game game, String... states)
	{
		super(game);
		this.states = states;
		Preconditions.checkNotNull(states);
	}

	@Override
	public void start()
	{
		registerEvents(PlayerJoinEvent.class, event ->
		{
			if (states == null)
				return;

			for (String s : states)
			{
				game.getPlayers().forEach(p -> game.getStateTracker().addState(p, s));
			}
		});
	}

	@Override
	public void end()
	{
	}
}
