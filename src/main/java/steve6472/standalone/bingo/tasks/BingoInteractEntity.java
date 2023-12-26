package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.function.BiPredicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoInteractEntity extends BingoTask
{
	private final BiPredicate<Player, Entity> predicate;

	public BingoInteractEntity(Bingo bingo, Material icon, String name, String description, String id, BiPredicate<Player, Entity> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(PlayerInteractEntityEvent.class, event ->
		{
			if (predicate.test(event.getPlayer(), event.getRightClicked()))
				finishTask(event.getPlayer());
		});
	}
}
