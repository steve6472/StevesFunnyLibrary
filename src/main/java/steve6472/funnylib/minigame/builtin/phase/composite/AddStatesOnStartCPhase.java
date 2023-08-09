package steve6472.funnylib.minigame.builtin.phase.composite;

import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class AddStatesOnStartCPhase extends AbstractGamePhase
{
	private final String[] states;

	public AddStatesOnStartCPhase(Game game, String... states)
	{
		super(game);
		this.states = states;
	}

	@Override
	public void start()
	{
		if (states == null)
			return;

		for (String s : states)
		{
			game.getPlayers().forEach(p -> game.getStateTracker().addState(p, s));
		}
	}

	@Override
	public void end()
	{
	}
}
