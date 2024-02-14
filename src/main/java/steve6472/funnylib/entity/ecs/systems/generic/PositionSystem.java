package steve6472.funnylib.entity.ecs.systems.generic;

import dev.dominion.ecs.api.Dominion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.loot.LootTables;
import org.bukkit.loot.Lootable;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.entity.ecs.System;
import steve6472.funnylib.entity.ecs.components.LocationComp;
import steve6472.funnylib.entity.ecs.components.UUIDEntityComp;
import steve6472.funnylib.util.NMS;

/**
 * Created by steve6472
 * Date: 2/11/2024
 * Project: StevesFunnyLibrary <br>
 */
public class PositionSystem implements System
{
	@Override
	public void tick(Dominion ecs)
	{
		var entities = ecs.findEntitiesWith(UUIDEntityComp.class, LocationComp.class);
		var iterator = entities.iterator();

		while (iterator.hasNext())
		{
			var entity = iterator.next();

			Entity bukkitEntity = null;

			boolean moved = entity.comp2().hasMoved();

			if (FunnyLib.getUptimeTicks() % 20 == 0 || moved)
				bukkitEntity = ensureBukkitEntity(entity.comp1(), entity.comp2());

			if (bukkitEntity == null)
				continue;

			if (moved)
			{
				bukkitEntity.teleport(entity.comp2().getLocation());
				entity.comp2().unmove();
			}
		}
	}

	private Entity ensureBukkitEntity(UUIDEntityComp ent, LocationComp loc)
	{
		World world = loc.getWorld();

		// Silently fail
		if (world == null)
		{
			Bukkit.broadcastMessage("World null");
			return null;
		}

		Entity entity = NMS.getEntityInWorld(world, ent.getEntityUUID());

		if (entity == null)
		{
			Bukkit.broadcastMessage("Spawning new entity");
			world.spawn(loc.getLocation(), ent.entityType, newEnt ->
			{
				newEnt.setPersistent(false);
				newEnt.setSilent(true);
				newEnt.setGravity(false);
				newEnt.setInvulnerable(true);

				if (newEnt instanceof LivingEntity living)
				{
					living.setAI(false);
					living.setCollidable(false);
				}

				if (newEnt instanceof Lootable lootable)
				{
					lootable.setLootTable(LootTables.EMPTY.getLootTable());
				}

				ent.reassignUUID(newEnt.getUniqueId());
			});
		}

		return entity;
	}
}
