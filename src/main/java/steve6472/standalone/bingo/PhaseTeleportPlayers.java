package steve6472.standalone.bingo;

import org.bukkit.Location;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.RandomUtil;

/**
 * Created by steve6472
 * Date: 9/2/2023
 * Project: StevesFunnyLibrary <br>
 */
public class PhaseTeleportPlayers extends AbstractGamePhase
{
	Location location;

	public PhaseTeleportPlayers(Game game, Location location)
	{
		super(game);
		this.location = location;
	}

	@Override
	public void start()
	{
		game.getPlayers().forEach(player -> player.teleport(location.clone().add(RandomUtil.randomDouble(-1, 1) + 0.5, 0, RandomUtil.randomDouble(-1, 1) + 0.5)));
		endPhase();
	}

	@Override
	public void end()
	{

	}
}
