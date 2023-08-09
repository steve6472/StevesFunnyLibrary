package steve6472.funnylib.minigame.builtin.state;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import steve6472.funnylib.minigame.AbstractPlayerState;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class GlowingPlayerState extends AbstractPlayerState
{
	@Override
	public String getName()
	{
		return "glowing";
	}

	@Override
	public void apply(Player player)
	{
		player.setGlowing(true);
	}

	@Override
	public void revert(Player player)
	{
		player.setGlowing(false);
	}
}
