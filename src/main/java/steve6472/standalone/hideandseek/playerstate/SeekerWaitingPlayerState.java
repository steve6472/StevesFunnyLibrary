package steve6472.standalone.hideandseek.playerstate;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import steve6472.funnylib.minigame.AbstractPlayerState;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class SeekerWaitingPlayerState extends AbstractPlayerState
{
	@Override
	public String getName()
	{
		return "seeker_waiting";
	}

	@Override
	public void apply(Player player)
	{
		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 255, false, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, -1, 255, false, false, false));

		registerEvents(PlayerMoveEvent.class, event ->
		{
			if (event.getPlayer() == player)
			{
				event.setCancelled(true);
			}
		});

		scheduleRepeatingTask(task -> player.setFlying(true), 0, 20);
	}

	@Override
	public void revert(Player player)
	{
		player.setGameMode(GameMode.SURVIVAL);
		player.setAllowFlight(false);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		player.removePotionEffect(PotionEffectType.BLINDNESS);

		dispose();
	}
}
