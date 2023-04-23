package steve6472.funnylib.json;

import org.bukkit.inventory.ItemStack;

/**
 * Created by steve6472
 * Date: 4/23/2023
 * Project: StevesFunnyLibrary <br>
 * <br>
 * Should implement "public static T fromItem(ItemStack)" method
 */
public interface Itemizable<T>
{
	ItemStack toItem();
}
