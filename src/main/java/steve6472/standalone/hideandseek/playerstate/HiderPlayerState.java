package steve6472.standalone.hideandseek.playerstate;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import steve6472.funnylib.minigame.AbstractPlayerState;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class HiderPlayerState extends AbstractPlayerState
{
	private final Team hiderTeam;

	public HiderPlayerState(Team hiderTeam)
	{
		this.hiderTeam = hiderTeam;
	}

	@Override
	public String getName()
	{
		return "hider";
	}

	@Override
	public void apply(Player player)
	{
		player.setGameMode(GameMode.SURVIVAL);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, -1, 0, false, false, false));
		hiderTeam.addEntry(player.getName());
	}

	@Override
	public void revert(Player player)
	{
		player.setGameMode(GameMode.ADVENTURE);
		player.removePotionEffect(PotionEffectType.SLOW);
		hiderTeam.removeEntry(player.getName());
	}
}
