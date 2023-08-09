package steve6472.funnylib.minigame.builtin.phase;

import org.bukkit.ChatColor;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class CountdownPhase extends AbstractGamePhase
{
	int count;

	public CountdownPhase(Game game, int startingCount)
	{
		super(game);
		this.count = startingCount;
	}

	@Override
	public void start()
	{
		scheduleRepeatingTask(task ->
		{
			game.getPlayers().forEach(p -> p.sendTitle(JSONMessage.create("" + count).color(count > 3 ? ChatColor.YELLOW : ChatColor.RED).toLegacy(), "", 0, 30, 0));
			count--;
			if (count < 0)
			{
				endPhase();
			}
		}, 0, 20);
	}

	@Override
	public void end()
	{
	}
}
