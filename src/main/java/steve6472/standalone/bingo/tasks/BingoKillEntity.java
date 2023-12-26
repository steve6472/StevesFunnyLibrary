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
public class BingoKillEntity extends BingoTask
{
	private final Predicate<LivingEntity> predicate;

	public BingoKillEntity(Bingo bingo, Material icon, String name, String description, String id, Predicate<LivingEntity> predicate)
	{
		super(bingo, icon, name, description, id);
		this.predicate = predicate;
	}

	@Override
	protected void setupEvents()
	{
		registerEvent(EntityDeathEvent.class, event ->
		{
			EntityDamageEvent lastDamageCause = event.getEntity().getLastDamageCause();
			if (!(lastDamageCause instanceof EntityDamageByEntityEvent ee)) return;
			Entity lastEntity = ee.getDamager();
			if (!(lastEntity instanceof Player player)) return;

			if (predicate.test(event.getEntity()))
				finishTask(player);
		});
	}
}
