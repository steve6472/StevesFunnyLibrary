package steve6472.funnylib.minigame.builtin.phase;

import org.bukkit.entity.Player;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class VictoryPhase extends AbstractGamePhase
{
	Predicate<Player> selector;

	public VictoryPhase(Game game, Predicate<Player> selector)
	{
		super(game);
		this.selector = selector;
	}

	@Override
	public void start()
	{
		Set<? extends Player> winners = game.getPlayers().stream().filter(selector).collect(Collectors.toSet());
		game.getPlayers().forEach(p -> {
			p.sendMessage("Winners: ");
			winners.forEach(w -> {
				p.sendMessage(w.getName());
			});
		});
	}

	@Override
	public void end()
	{

	}
}
