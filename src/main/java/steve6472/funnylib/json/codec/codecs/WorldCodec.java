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
public class WorldCodec extends Codec<World>
{
	@Override
	public World fromJson(JSONObject json)
	{
		return Bukkit.getWorld(UUID.fromString(json.getString("uuid")));
	}

	@Override
	public void toJson(World obj, JSONObject json)
	{
		json.put("uuid", obj.getUID().toString());
	}
}
