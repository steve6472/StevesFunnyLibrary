package steve6472.funnylib.context;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Created by steve6472
 * Date: 11/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class EntityContext
{
	protected final Entity entity;

	public EntityContext(Entity entity)
	{
		this.entity = entity;
	}

	public Entity getEntity()
	{
		return entity;
	}

	public World getWorld()
	{
		return entity.getWorld();
	}

	public Chunk getEntityChunk()
	{
		return getLocation().getChunk();
	}

	public Location getLocation()
	{
		return entity.getLocation();
	}
}
