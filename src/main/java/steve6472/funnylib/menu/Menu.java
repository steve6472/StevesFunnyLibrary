package steve6472.funnylib.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import steve6472.funnylib.util.MetaUtil;
import steve6472.funnylib.util.MiscUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary
 */
public class Menu
{
	final Inventory inventory;

	boolean recordHistory;
	boolean allowPlayerInventory;
	boolean redirected;
	BiFunction<Menu, Player, Response> onClose;
	int offsetX, offsetY;
	int rows;
	int minOffsetX, maxOffsetX, minOffsetY, maxOffsetY;
	boolean offsetLimited;

	final Map<SlotLoc, Slot> slots = new HashMap<>();
	final Map<SlotLoc, Slot> stickySlots = new HashMap<>();

	Menu(Inventory inventory)
	{
		this.inventory = inventory;
	}

	public void showToPlayers(Player... players)
	{
		for (Player player : players)
		{
			showToPlayer(player);
		}
	}

	public void showToPlayer(Player player)
	{
		player.openInventory(inventory);
		MetaUtil.setMeta(player, MenuListener.MENU_META_KEY, this);
	}

	public void move(int x, int y)
	{
		if (offsetLimited)
		{
			this.offsetX = Math.max(minOffsetX, Math.min(maxOffsetX, offsetX + x));
			this.offsetY = Math.max(minOffsetY, Math.min(maxOffsetY, offsetY + y));
		} else
		{
			this.offsetX += x;
			this.offsetY += y;
		}
		reload();
	}

	public int getOffsetX()
	{
		return offsetX;
	}

	public int getOffsetY()
	{
		return offsetY;
	}

	public Slot getSlot(int x, int y)
	{
		SlotLoc loc = new SlotLoc(x, y);

		Slot stickySlot = stickySlots.get(loc);
		if (stickySlot != null)
			return stickySlot;

		loc = new SlotLoc(x + offsetX, y + offsetY);
		return slots.get(loc);
	}

	public void reload()
	{
		for (int i = 0; i < inventory.getSize(); i++)
		{
			int x = (i % 9);
			int y = (i / 9);

			Slot slot = getSlot(x, y);
			if (slot != null)
			{
				inventory.setItem(i, slot.item());
			} else
			{
				inventory.setItem(i, MiscUtil.AIR);
			}
		}
	}

	/*
	 * Event thingies
	 */

	Response callOnClose(Player player)
	{
		if (onClose == null) return Response.allow();

		return onClose.apply(this, player);
	}
}
