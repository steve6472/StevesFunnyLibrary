package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoGiveItemToPlayer extends BingoTask
{
	private final Predicate<ItemStack> predicate;

	public BingoGiveItemToPlayer(Bingo bingo, Material icon, String name, String description, String id, Predicate<ItemStack> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(EntityPickupItemEvent.class, event ->
		{
			if (!(event.getEntity() instanceof Player player)) return;
			if (!predicate.test(event.getItem().getItemStack())) return;

			UUID ownerUUID = event.getItem().getThrower();
			if (ownerUUID == null) return;

			Optional<? extends Player> ownerOptional = bingo.getGame().getPlayers().stream().filter(p -> p.getUniqueId().equals(ownerUUID)).findFirst();
			ownerOptional.ifPresent(owner ->
			{
				if (owner == player) return;
				finishTask(owner);
			});
		});
	}
}
