package steve6472.funnylib.entity.ecs.systems.generic;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Results;
import org.bukkit.World;
import steve6472.funnylib.entity.ecs.System;
import steve6472.funnylib.entity.ecs.components.LocationComp;
import steve6472.funnylib.entity.ecs.components.ParticleDebugComp;

/**
 * Created by steve6472
 * Date: 2/12/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ParticleDebugSystem implements System
{
	@Override
	public void tick(Dominion ecs)
	{
		var entities = ecs.findEntitiesWith(LocationComp.class, ParticleDebugComp.class);

		for (Results.With2<LocationComp, ParticleDebugComp> entity : entities)
		{
			LocationComp loc = entity.comp1();
			World world = loc.getWorld();
			if (world == null) continue;

			world.spawnParticle(entity.comp2().particle, loc.getLocation().getX(), loc.getLocation().getY(), loc.getLocation().getZ(), 1, 0, 0, 0, 0.05, entity.comp2().data);
		}
	}
}
