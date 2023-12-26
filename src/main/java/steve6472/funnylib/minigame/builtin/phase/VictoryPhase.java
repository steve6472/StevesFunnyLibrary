package steve6472.funnylib.minigame.builtin.phase;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.JSONMessage;

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
		game.getPlayers().forEach(p ->
		{
			JSONMessage winnersMessage = JSONMessage.create("");
			winners.forEach(w -> winnersMessage.then(w.getName()).color(ChatColor.WHITE));
			p.sendTitle(JSONMessage.create("Game Ended").color(ChatColor.YELLOW).toLegacy(), winnersMessage.toLegacy(), 10, 60, 10);
			System.out.println("Winners: " + winnersMessage.toLegacy());
		});
	}

	@Override
	public void end()
	{

	}
}
