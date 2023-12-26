package steve6472.funnylib.minigame.builtin.state;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import steve6472.funnylib.minigame.AbstractPlayerState;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ImmovablePlayerState extends AbstractPlayerState
{
	@Override
	public String getName()
	{
		return "immovable";
	}

	@Override
	public void apply(Player player)
	{
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, -1, 255, false, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, -1, 255, false, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, -1, 127, false, false, false));

		registerPlayerEvent(player, PlayerMoveEvent.class, event ->
		{
			if (event.getTo() == null)
				return;

			if (event.getFrom().getWorld() != null && event.getTo().getWorld() != null)
			{
				if (!event.getFrom().getWorld().equals(event.getTo().getWorld()))
				{
					event.setCancelled(true);
					return;
				}
			}

			if (event.getFrom().distanceSquared(event.getTo()) != 0)
				event.setCancelled(true);
		});

		registerPlayerEvent(player, PlayerTeleportEvent.class, event ->
		{
			if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN) || event.getCause().equals(PlayerTeleportEvent.TeleportCause.UNKNOWN))
				return;
			event.setCancelled(true);
		});

		registerPlayerEvent(player, PlayerPortalEvent.class, event ->
		{
			if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN) || event.getCause().equals(PlayerTeleportEvent.TeleportCause.UNKNOWN))
				return;
			event.setCancelled(true);
		});
	}

	@Override
	public void revert(Player player)
	{
		player.removePotionEffect(PotionEffectType.SLOW);
		player.removePotionEffect(PotionEffectType.SLOW_FALLING);
		player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
	}
}
