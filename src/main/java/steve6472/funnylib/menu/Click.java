package steve6472.funnylib.menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Click
{
	Slot slot;
	ClickType type;
	InventoryAction action;
	ItemStack itemOnCursor;

	// TODO: make getters

	public Menu getMenu()
	{
		return slot.menu();
	}

	public Slot getSlot()
	{
		return slot;
	}

	public ClickType getType()
	{
		return type;
	}

	public InventoryAction getAction()
	{
		return action;
	}

	public ItemStack getItemOnCursor()
	{
		return itemOnCursor;
	}

	@Override
	public String toString()
	{
		return "Click{" + "slot=" + slot + ", type=" + type + ", action=" + action + ", itemOnCursor=" + itemOnCursor + '}';
	}
}
