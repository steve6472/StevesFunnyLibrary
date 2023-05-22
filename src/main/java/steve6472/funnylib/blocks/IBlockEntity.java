package steve6472.funnylib.blocks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import steve6472.funnylib.context.BlockContext;

/**
 * Created by steve6472
 * Date: 5/21/2023
 * Project: StevesFunnyLibrary <br>
 */
public interface IBlockEntity
{
	default void despawnEntities(BlockContext context)
	{
		Entity[] entities = getEntities();

		if (entities == null)
			return;

		for (Entity entity : entities)
		{
			if (entity == null)
				continue;

			entity.remove();
		}
	}

	void spawnEntities(BlockContext context);

	Entity[] getEntities();
}
