package steve6472.funnylib.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 9/25/2022
 * Project: StevesFunnyLibrary <br>
 */
public class GlowingUtil
{
	private static Map<ChatColor, Team> TEAMS;

	public static void init()
	{
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		if (scoreboardManager == null)
		{
			return;
		}
		Scoreboard board = scoreboardManager.getMainScoreboard();

		TEAMS = new HashMap<>();

		for (ChatColor value : ChatColor.values())
		{
			if (!value.isColor())
				continue;

			String teamName = value + value.name().toLowerCase();

			Team team = board.getTeam(teamName);
			if (team == null)
			{
				team = board.registerNewTeam(teamName);
			}
			team.setColor(value);

			TEAMS.put(value, team);
		}
	}

	public static void setGlowColor(LivingEntity entity, ChatColor color)
	{
		String id = entity.getUniqueId().toString();
		TEAMS.forEach((k, v) -> v.removeEntry(id));

		Team team = TEAMS.get(color);
		if (team != null)
		{
			team.addEntry(id);
		}
	}
}
