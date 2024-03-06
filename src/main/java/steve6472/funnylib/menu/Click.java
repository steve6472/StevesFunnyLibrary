package steve6472.funnylib.menu;

import org.bukkit.entity.Player;
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
	Player player;
	Slot slot;
	ClickType type;
	InventoryAction action;
	ItemStack itemOnCursor;

	public Click()
	{

	}

	public Click(Player player, Slot slot, ClickType type, InventoryAction action, ItemStack itemOnCursor)
	{
		this.player = player;
		this.slot = slot;
		this.type = type;
		this.action = action;
		this.itemOnCursor = itemOnCursor;
	}

	// TODO: make getters

	public Player player()
	{
		return player;
	}

	public Menu menu()
	{
		return slot.menu();
	}

	public Slot slot()
	{
		return slot;
	}

	public ClickType type()
	{
		return type;
	}

	public InventoryAction action()
	{
		return action;
	}

	public ItemStack itemOnCursor()
	{
		return itemOnCursor;
	}

	@Override
	public String toString()
	{
		return "Click{" + "slot=" + slot + ", type=" + type + ", action=" + action + ", itemOnCursor=" + itemOnCursor + '}';
	}
}
