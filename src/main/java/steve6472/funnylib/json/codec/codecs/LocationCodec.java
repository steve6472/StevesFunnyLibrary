package steve6472.funnylib.json.codec.codecs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONObject;
import steve6472.funnylib.json.codec.Codec;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class LocationCodec extends Codec<Location>
{
	@Override
	public Location fromJson(JSONObject json)
	{
		String worldUUID = json.optString("world", null);
		World world = null;
		if (worldUUID != null)
		{
			world = Bukkit.getWorld(UUID.fromString(worldUUID));
		}

		return new Location(world, json.getDouble("x"), json.getDouble("y"), json.getDouble("z"), json.optFloat("yaw", 0.0f), json.optFloat("pitch", 0.0f));
	}

	@Override
	public void toJson(Location obj, JSONObject json)
	{
		if (obj.getWorld() != null)
		{
			json.put("world", obj.getWorld().getUID().toString());
		}

		json.put("x", obj.getX());
		json.put("y", obj.getY());
		json.put("z", obj.getZ());
		json.put("yaw", obj.getYaw());
		json.put("pitch", obj.getPitch());
	}
}
