package steve6472.standalone.bingo.tasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoPlayerLocation extends BingoTask
{
	private final Predicate<Location> predicate;

	public BingoPlayerLocation(Bingo bingo, Material icon, String name, String description, String id, Predicate<Location> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(PlayerMoveEvent.class, event ->
		{
			if (predicate.test(event.getTo()))
				finishTask(event.getPlayer());
		});

		registerEvent(PlayerTeleportEvent.class, event ->
		{
			if (predicate.test(event.getTo()))
				finishTask(event.getPlayer());
		});
	}
}
