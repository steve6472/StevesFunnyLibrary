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
	BiFunction<Click, Menu, Response> onClick;
	Map<ClickType, BiFunction<Click, Menu, Response>> conditionedClick;

	int x, y;

	boolean isSticky;

	public ItemStack item()
	{
		return itemStack;
	}

	public Slot setItem(ItemStack itemStack)
	{
		this.itemStack = itemStack;
		updateSlot();
		return this;
	}

	public Menu menu()
	{
		return holder;
	}

	protected boolean canBeInteractedWith(ClickType clickType, InventoryAction inventoryAction)
	{
		return allowedClickTypes.contains(clickType) && allowedInventoryActions.contains(inventoryAction);
	}

	/*
	 * Event thingies
	 */

	Response callOnClick(Click click)
	{
		BiFunction<Click, Menu, Response> clickPlayerBiConsumer = conditionedClick.get(click.type);
		if (clickPlayerBiConsumer != null)
		{
			return clickPlayerBiConsumer.apply(click, holder);
		}

		if (onClick != null)
		{
			return onClick.apply(click, holder);
		}

		return Response.cancel();
	}

	public Slot updateSlot()
	{
		int index;

		if (isSticky)
		{
			index = x + y * 9;
		} else
		{
			if (x - holder.offsetX < 0 || x - holder.offsetX > 8 || y - holder.offsetY < 0 || y - holder.offsetY > holder.rows) return this;
			index = (x - holder.offsetX) + (y - holder.offsetY) * 9;
		}

		holder.inventory.setItem(index, item());

		return this;
	}
}
