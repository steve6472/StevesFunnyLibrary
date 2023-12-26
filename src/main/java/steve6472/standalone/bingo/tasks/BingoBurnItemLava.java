package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoBurnItemLava extends BingoTask
{
	private final Predicate<ItemStack> predicate;

	public BingoBurnItemLava(Bingo bingo, Material icon, String name, String description, String id, Predicate<ItemStack> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(EntityDamageEvent.class, event ->
		{
			if (!(event.getEntity() instanceof Item itemEntity)) return;
			if (event.getCause() != EntityDamageEvent.DamageCause.LAVA) return;
			if (predicate.test(itemEntity.getItemStack()))
			{
				Optional<? extends Player> ownerOptional = bingo.getGame().getPlayers().stream().filter(p -> p.getUniqueId().equals(itemEntity.getThrower())).findFirst();
				ownerOptional.ifPresent(this::finishTask);
			}
		});
	}
}
