package steve6472.funnylib.item;

import org.bukkit.inventory.ItemStack;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class CustomStack
{
	private final CustomItem customItem;
	private final ItemStack itemStack;

	public CustomStack(CustomItem item)
	{
		customItem = item;
		itemStack = item.newItemStack();
	}
}
