package steve6472.funnylib.minigame.builtin.state;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import steve6472.funnylib.minigame.AbstractPlayerState;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class AdventurePlayerState extends AbstractPlayerState
{
	@Override
	public String getName()
	{
		return "adventure";
	}

	@Override
	public void apply(Player player)
	{
		player.setGameMode(GameMode.ADVENTURE);
	}

	@Override
	public void revert(Player player)
	{
		player.setGameMode(GameMode.SURVIVAL);
	}
}
