package steve6472.funnylib.item;

import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.util.ItemStackBuilder;

/**********************
 * Created by steve6472
 * On date: 4/6/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public abstract class CustomItem
{
	public abstract String id();

	protected abstract ItemStack item();

	public ItemStack newItemStack()
	{
		return ItemStackBuilder.editNonStatic(item()).setCustomId(id()).buildItemStack();
	}

	@Override
	public String toString()
	{
		return "CustomItem{id='" + id() + "', item=" + item().toString() + "}";
	}
}
