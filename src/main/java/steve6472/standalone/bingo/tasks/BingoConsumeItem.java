package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoConsumeItem extends BingoTask
{
	private final Predicate<Material> predicate;

	public BingoConsumeItem(Bingo bingo, Material icon, String name, String description, String id, Predicate<Material> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(PlayerItemConsumeEvent.class, event ->
		{
			if (predicate.test(event.getItem().getType()))
				finishTask(event.getPlayer());
		});
	}
}
