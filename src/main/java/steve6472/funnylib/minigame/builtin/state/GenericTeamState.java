package steve6472.funnylib.minigame.builtin.state;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import steve6472.funnylib.minigame.AbstractPlayerState;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class GenericTeamState extends AbstractPlayerState
{
	private final String name;
	private final Team team;

	public GenericTeamState(String name, Team team)
	{
		this.name = name;
		this.team = team;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void apply(Player player)
	{
		team.addEntry(player.getName());
	}

	@Override
	public void revert(Player player)
	{
		team.removeEntry(player.getName());
	}
}
