package steve6472.funnylib.minigame.builtin.state;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import steve6472.funnylib.minigame.AbstractPlayerState;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BorderLockedPlayerState extends AbstractPlayerState
{
	@Override
	public String getName()
	{
		return "border_locked";
	}

	@Override
	public void apply(Player player)
	{
		registerEvents(PlayerMoveEvent.class, event ->
		{
			if (event.getPlayer() != player)
				return;

			keepPlayerInsideBorder(player.getWorld().getWorldBorder(), player);
		});
	}

	private void keepPlayerInsideBorder(WorldBorder border, Player player)
	{
		if (border.isInside(player.getLocation()))
		{
			return;
		}

		Location center = border.getCenter();

		if (player.getLocation().getX() > center.getX() + border.getSize() / 2)
		{
			Location location = player.getLocation();
			location.setX(center.getX() + border.getSize() / 2 - 1);
			player.teleport(location);
		}

		if (player.getLocation().getX() < center.getX() - border.getSize() / 2)
		{
			Location location = player.getLocation();
			location.setX(center.getX() - border.getSize() / 2 + 1);
			player.teleport(location);
		}

		if (player.getLocation().getZ() > center.getZ() + border.getSize() / 2)
		{
			Location location = player.getLocation();
			location.setZ(center.getZ() + border.getSize() / 2 - 1);
			player.teleport(location);
		}

		if (player.getLocation().getZ() < center.getZ() - border.getSize() / 2)
		{
			Location location = player.getLocation();
			location.setZ(center.getZ() - border.getSize() / 2 + 1);
			player.teleport(location);
		}
	}

	@Override
	public void revert(Player player)
	{
		dispose();
	}
}
