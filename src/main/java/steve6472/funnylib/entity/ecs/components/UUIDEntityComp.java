package steve6472.funnylib.entity.ecs.components;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 2/11/2024
 * Project: StevesFunnyLibrary <br>
 */
public class UUIDEntityComp
{
	public final Class<? extends Entity> entityType;
	private UUID entityUUID;

	public UUIDEntityComp(Class<? extends Entity> entityType)
	{
		this.entityType = entityType;
	}

	public void assignUUID(UUID uuid)
	{
		if (entityUUID != null)
			throw new RuntimeException("Entity already has UUID");
		this.entityUUID = uuid;
	}

	public void reassignUUID(UUID uuid)
	{
		if (entityUUID != null)
		{
			Entity entity = Bukkit.getEntity(entityUUID);
			if (entity != null)
				entity.remove();
		}

		this.entityUUID = uuid;
	}

	public UUID getEntityUUID()
	{
		return entityUUID;
	}

	public void removeEntity()
	{
		if (entityUUID == null)
			return;
		Entity entity = Bukkit.getEntity(entityUUID);
		if (entity != null)
		{
			entity.remove();
		}
	}
}
