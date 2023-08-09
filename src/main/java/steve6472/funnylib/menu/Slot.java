package steve6472.funnylib.menu;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.slots.IconSlot;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary
 */
public abstract class Slot
{
	Menu holder;
//	Set<ClickType> allowedClickTypes;
//	Set<InventoryAction> allowedInventoryActions;
//	Map<ClickType, BiFunction<Click, Menu, Response>> conditionedClick;

	final boolean isSticky;
	int x, y;

	public Slot(boolean isSticky)
	{
		this.isSticky = isSticky;
	}

	public ItemStack getIcon()
	{
		return new ItemStack(Material.AIR);
	}

	public Menu menu()
	{
		return holder;
	}

	public boolean isSticky()
	{
		return isSticky;
	}

	public abstract boolean canBeInteractedWith(Click click);

	/*
	 * Event thingies
	 */

	public abstract Response onClick(Click click);

	public Slot updateSlot()
	{
		return updateSlot(getIcon());
	}

	public Slot updateSlot(ItemStack itemStack)
	{
		if (holder == null)
			return this;

		int index;
		int visibleX = x - holder.windowX + holder.offsetX;
		int visibleY = y - holder.windowY + holder.offsetY;

		Menu loop = holder.parent;
		while (loop != null)
		{
			visibleX -= loop.windowX + loop.offsetX;
			visibleY -= loop.windowY + loop.offsetY;
			loop = loop.parent;
		}

//		if (this instanceof IconSlot is)
//		{
//			System.out.println(visibleX + " " + visibleY + " " + is.getIcon());
//		}

		if (isSticky)
		{
			index = x + y * holder.windowWidth;
		} else
		{
//			if (x - holder.offsetX < 0 || x - holder.offsetX > 8 || y - holder.offsetY < 0 || y - holder.offsetY > holder.rows) return this;
			index = (visibleX) + (visibleY) * holder.windowWidth;
		}

		if (index >= 54)
			return this;

		Inventory inventory = holder.getInventory();
		if (inventory != null)
			inventory.setItem(index, itemStack);

		return this;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public String toString()
	{
		return "Slot{" + "itemStack=" + getIcon() + ", x=" + x + ", y=" + y + ", isSticky=" + isSticky + '}';
	}
}
