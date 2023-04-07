package steve6472.funnylib.json.codec.codecs;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.json.codec.Codec;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class StringListCodec extends Codec<ArrayList<String>>
{
	@Override
	public ArrayList<String> fromJson(JSONObject json)
	{
		JSONArray jsonList = json.getJSONArray("list");
		ArrayList<String> list = new ArrayList<>(jsonList.length());
		for (int i = 0; i < jsonList.length(); i++)
		{
			list.add(jsonList.getString(i));
		}
		return list;
	}

	@Override
	public void toJson(ArrayList<String> obj, JSONObject json)
	{
		JSONArray jsonList = new JSONArray();
		for (String s : obj)
		{
			jsonList.put(s);
		}
		json.put("list", jsonList);
	}
}
