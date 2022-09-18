package steve6472.funnylib.json.codec.codecs;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.json.JSONObject;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.json.codec.Codec;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 9/16/2022
 * Project: StevesFunnyLibrary <br>
 */
public class EntityCodec extends Codec<Entity>
{
	@Override
	public Entity fromJson(JSONObject json)
	{
		if (json.optBoolean("none", false))
			return null;

		String worldName = json.optString("world", null);
		if (worldName == null)
			return null;
		World world = Bukkit.getWorld(worldName);
		if (world == null)
			return null;

		UUID uuid = UUID.fromString(json.getString("uuid"));
		if (Blocks.currentLoadingChunk != null)
		{
			for (Entity entity : Blocks.currentLoadingChunk.getEntities())
			{
				if (entity.getUniqueId().equals(uuid))
					return entity;
			}
		}

		Collection<Entity> entities = world.getEntities();
		for (Entity entity : entities)
		{
			if (entity.getUniqueId().equals(uuid))
				return entity;
		}
		throw new RuntimeException("Entity with uuid %s not found".formatted(uuid.toString()));
	}

	@Override
	public void toJson(Entity obj, JSONObject json)
	{
		if (obj == null)
		{
			json.put("none", true);
			return;
		}

		json.put("world", obj.getWorld().getName());
		json.put("uuid", obj.getUniqueId().toString());
	}
}
