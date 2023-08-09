package steve6472.funnylib.minigame.builtin.phase.composite;

import org.bukkit.Location;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class TeleportPlayersOnEndCPhase extends AbstractGamePhase
{
	Location location;

	public TeleportPlayersOnEndCPhase(Game game, Location location)
	{
		super(game);
		this.location = location;
	}

	@Override
	public void start()
	{
		if (location == null)
			return;

		game.getPlayers().forEach(p -> p.teleport(location));
	}

	@Override
	public void end()
	{

	}
}
