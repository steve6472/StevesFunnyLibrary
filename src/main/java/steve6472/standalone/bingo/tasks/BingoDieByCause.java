package steve6472.standalone.bingo.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import steve6472.standalone.bingo.Bingo;
import steve6472.standalone.bingo.BingoTask;

import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 8/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BingoDieByCause extends BingoTask
{
	private final Predicate<EntityDamageEvent.DamageCause> predicate;

	public BingoDieByCause(Bingo bingo, Material icon, String name, String description, String id, Predicate<EntityDamageEvent.DamageCause> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(EntityDeathEvent.class, event ->
		{
			if (!(event.getEntity() instanceof Player player)) return;

			EntityDamageEvent lastDamageCause = event.getEntity().getLastDamageCause();
			if (lastDamageCause == null) return;

			if (predicate.test(lastDamageCause.getCause()))
				finishTask(player);
		});
	}
}
