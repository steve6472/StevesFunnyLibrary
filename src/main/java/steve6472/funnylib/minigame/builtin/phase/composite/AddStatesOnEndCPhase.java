package steve6472.funnylib.minigame.builtin.phase.composite;

import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class AddStatesOnEndCPhase extends AbstractGamePhase
{
	private final String[] states;

	public AddStatesOnEndCPhase(Game game, String... states)
	{
		super(game);
		this.states = states;
	}

	@Override
	public void start()
	{
	}

	@Override
	public void end()
	{
		if (states == null)
			return;

		for (String s : states)
		{
			game.getPlayers().forEach(p -> game.getStateTracker().addState(p, s));
		}
	}
}
