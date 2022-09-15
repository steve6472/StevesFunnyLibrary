package steve6472.funnylib.json.codec.codecs;

import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;
import steve6472.funnylib.json.codec.Codec;
import steve6472.funnylib.util.MiscUtil;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ItemStackCodec extends Codec<ItemStack>
{
	@Override
	public ItemStack fromJson(JSONObject json)
	{
		return MiscUtil.itemFromBase64(json.getString("item"));
	}

	@Override
	public void toJson(ItemStack obj, JSONObject json)
	{
		json.put("item", MiscUtil.itemToBase64(obj));
	}
}
