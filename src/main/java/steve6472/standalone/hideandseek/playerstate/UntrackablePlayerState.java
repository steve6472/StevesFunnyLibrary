package steve6472.standalone.hideandseek.playerstate;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import steve6472.funnylib.minigame.AbstractPlayerState;
import steve6472.funnylib.minigame.Game;

/**
 * Created by steve6472
 * Date: 8/13/2023
 * Project: StevesFunnyLibrary <br>
 */
public class UntrackablePlayerState extends AbstractPlayerState
{
	private final Game game;

	public UntrackablePlayerState(Game game)
	{
		this.game = game;
	}

	@Override
	public String getName()
	{
		return "untrackable";
	}

	@Override
	public void apply(Player player)
	{
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30 * 20, 1, false, false, true));
		player.sendMessage(ChatColor.YELLOW + "\nYou are untrackable for 30 seconds!\n");
		scheduleSyncTask(task ->
		{
			game.getStateTracker().removeState(player, getName());
		}, 30 * 20);
	}

	@Override
	public void revert(Player player)
	{

	}
}
