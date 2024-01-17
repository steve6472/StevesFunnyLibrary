package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoCraftItem extends BingoTask
{
	private final Predicate<ItemStack> predicate;

	public BingoCraftItem(Bingo bingo, Material icon, String name, String description, String id, Predicate<ItemStack> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(CraftItemEvent.class, event ->
		{
			if (predicate.test(event.getRecipe().getResult()))
				finishTask(((Player) event.getWhoClicked()));
		});
	}
}
