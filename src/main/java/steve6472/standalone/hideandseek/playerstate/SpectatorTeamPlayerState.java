package steve6472.standalone.hideandseek.playerstate;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import steve6472.funnylib.minigame.AbstractPlayerState;

public class SpectatorTeamPlayerState extends AbstractPlayerState
{
	private final Team team;

	public SpectatorTeamPlayerState(Team team)
	{
		this.team = team;
	}

	@Override
	public String getName()
	{
		return "spectator";
	}

	@Override
	public void apply(Player player)
	{
		player.setGameMode(GameMode.SPECTATOR);
		team.addEntry(player.getName());
	}

	@Override
	public void revert(Player player)
	{
		player.setGameMode(GameMode.ADVENTURE);
		team.removeEntry(player.getName());
	}
}