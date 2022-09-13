package steve6472.funnylib.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary
 */
public class Slot
{
	Menu holder;
	ItemStack itemStack;
	Set<ClickType> allowedClickTypes;
	Set<InventoryAction> allowedInventoryActions;
	BiFunction<Click, Player, Response> onClick;
	Map<ClickType, BiFunction<Click, Player, Response>> conditionedClick;

	boolean isSticky;

	public ItemStack item()
	{
		return itemStack;
	}

	public void setItem(ItemStack itemStack)
	{
		this.itemStack = item();
	}

	public Menu menu()
	{
		return holder;
	}

	/*
	 * Event thingies
	 */

	Response callOnClick(Click click, Player player)
	{
		BiFunction<Click, Player, Response> clickPlayerBiConsumer = conditionedClick.get(click.type);
		if (clickPlayerBiConsumer != null)
		{
			return clickPlayerBiConsumer.apply(click, player);
		}

		if (onClick != null)
		{
			return onClick.apply(click, player);
		}

		return Response.cancel();
	}
}
