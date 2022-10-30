package steve6472.funnylib.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.util.MetaUtil;
import steve6472.funnylib.util.MiscUtil;

import java.util.HashMap;
import java.util.Iterator;
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
	ArbitraryData passedData;

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

	public void setOffset(int x, int y)
	{
		if (offsetLimited)
		{
			this.offsetX = Math.max(minOffsetX, Math.min(maxOffsetX, x));
			this.offsetY = Math.max(minOffsetY, Math.min(maxOffsetY, y));
		} else
		{
			this.offsetX = x;
			this.offsetY = y;
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

	public void setSlot(int x, int y, Slot slot)
	{
		slot.holder = this;
		slot.x = x;
		slot.y = y;
		if (slot.isSticky)
		{
			stickySlots.put(new SlotLoc(x, y), slot);
		} else
		{
			slots.put(new SlotLoc(x, y), slot);
		}
	}

	public void setSlot(int x, int y, SlotBuilder builder)
	{
		setSlot(x, y, builder.build());
	}

	public void removeSlot(int x, int y)
	{
		slots.remove(new SlotLoc(x, y));
	}

	public void removeStickySlot(int x, int y)
	{
		stickySlots.remove(new SlotLoc(x, y));
	}

	public void applyMask(Mask mask)
	{
		mask.applyMask(this);
	}

	public void overlay(Menu below, int minX, int minY, int maxX, int maxY)
	{
		// Expressions GUI size
		for (int i = 0; i < 54; i++)
		{
			int x = (i % 9);
			int y = (i / 9);

			if (x + minX < minX || x + minX > maxX || y + minY < minY || y + minY > maxY)
				continue;
//			System.out.print("" + x + ", " + y + " | ");

			Slot slot = getSlot(x, y);

			int sx = x + minX + below.offsetX;
			int sy = y + minY + below.offsetY;

			below.removeStickySlot(x + minX, y + minY);
			if (slot == null)
			{
				below.removeSlot(sx, sy);
			} else
			{
				below.setSlot(sx, sy, slot);
			}
		}
		below.reload();
//		System.out.println();
	}

	/**
	 * Ignores slicky slots
	 * supports negative values somewhat
	 */
	public void insertLineBelow(int y, int count)
	{
		HashMap<SlotLoc, Slot> toAdd = new HashMap<>();

		for (Iterator<SlotLoc> iterator = slots.keySet().iterator(); iterator.hasNext(); )
		{
			SlotLoc slotLoc = iterator.next();
			if (slotLoc.y() > y)
			{
				toAdd.put(new SlotLoc(slotLoc.x(), slotLoc.y() + count), slots.get(slotLoc));
				iterator.remove();
			}
		}

		slots.putAll(toAdd);
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

	public void clear()
	{
		slots.clear();
		stickySlots.clear();
		inventory.clear();
		offsetX = 0;
		offsetY = 0;
		metadataMap.clear();
	}

	public ArbitraryData getPassedData()
	{
		return passedData;
	}

	/*
	 * Event thingies
	 */

	Response callOnClose(Player player)
	{
		if (onClose == null) return Response.allow();

		return onClose.apply(this, player);
	}

	/*
	 * Metadata
	 */

	private final Map<String, Object> metadataMap = new HashMap<>();

	public void setMetadata(@NotNull String key, @NotNull Object value)
	{
		metadataMap.put(key, value);
	}

	public <T> T getMetadata(@NotNull String key, @NotNull Class<T> expectedType)
	{
		Object o = metadataMap.get(key);
		if (expectedType.isAssignableFrom(o.getClass()))
		{
			//noinspection unchecked
			return (T) o;
		}
		return null;
	}

	public boolean hasMetadata(@NotNull String key)
	{
		return metadataMap.containsKey(key);
	}

	public void removeMetadata(@NotNull String key)
	{
		metadataMap.remove(key);
	}

	public Map<String, Object> getMetadataMap()
	{
		return metadataMap;
	}
}
