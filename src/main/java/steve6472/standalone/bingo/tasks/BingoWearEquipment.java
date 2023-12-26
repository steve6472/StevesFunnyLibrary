package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import steve6472.funnylib.events.PlayerEquipArmorEvent;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoWearEquipment extends BingoTask
{
	private final Predicate<EntityEquipment> predicate;

	public BingoWearEquipment(Bingo bingo, Material icon, String name, String description, String id, Predicate<EntityEquipment> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(PlayerEquipArmorEvent.class, event ->
		{
			if (predicate.test(event.getPlayer().getEquipment()))
				finishTask(event.getPlayer());
		});
	}
}
