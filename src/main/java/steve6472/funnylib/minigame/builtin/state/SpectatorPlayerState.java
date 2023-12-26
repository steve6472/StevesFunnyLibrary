package steve6472.funnylib.minigame.builtin.state;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import steve6472.funnylib.minigame.AbstractPlayerState;

public class SpectatorPlayerState extends AbstractPlayerState
{
	@Override
	public String getName()
	{
		return "spectator";
	}

	@Override
	public void apply(Player player)
	{
		player.setGameMode(GameMode.SPECTATOR);
	}

	@Override
	public void revert(Player player)
	{
		player.setGameMode(GameMode.ADVENTURE);
	}
}