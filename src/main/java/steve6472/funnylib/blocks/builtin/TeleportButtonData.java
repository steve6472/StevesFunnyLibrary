package steve6472.funnylib.blocks.builtin;

import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.BlockData;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveInt;
import steve6472.funnylib.json.codec.codecs.ItemStackCodec;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class TeleportButtonData extends BlockData
{
	@Save(type = ItemStackCodec.class)
	public ItemStack item;
}
