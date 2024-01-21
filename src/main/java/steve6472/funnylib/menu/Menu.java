package steve6472.funnylib.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.joml.Vector4i;
import steve6472.funnylib.util.MetaUtil;
import steve6472.funnylib.util.MiscUtil;

import java.util.*;

/**
 * Created by steve6472
 * Date: 6/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class Menu
{
	private Inventory inventory;
	private final Optional<LinkedList<Menu>> history;

	/**
	 * Used to control even calling when redirecting
	 * Should not be set directly
	 */
	boolean redirected;
	private boolean isBuilt = false;
	AnvilRedirectState anvilRedirectState = AnvilRedirectState.NONE;

	private String title;
	final int rows;
	int offsetX, offsetY;
	int minOffsetX, maxOffsetX, minOffsetY, maxOffsetY;
	boolean offsetLimited;
	boolean allowPlayerInventory;
	int windowWidth, windowHeight;

	final Map<SlotLoc, Slot> slots = new HashMap<>();
	final Map<SlotLoc, Slot> stickySlots = new HashMap<>();

	final List<Menu> windows = new ArrayList<>();
	int windowX, windowY;
	boolean stickyWindow;
	Menu parent;

	public Menu(int rows, String title, boolean enableHistory)
	{
		this.rows = rows;
		this.title = title;

		history = Optional.ofNullable(enableHistory ? new LinkedList<>() : null);
		setWindowBounds(9, rows);
	}

	/*
	 * Setup
	 */

	protected abstract void setup();

	public void limitOffset(int minOffsetX, int maxOffsetX, int minOffsetY, int maxOffsetY)
	{
		this.minOffsetX = minOffsetX;
		this.maxOffsetX = maxOffsetX;
		this.minOffsetY = minOffsetY;
		this.maxOffsetY = maxOffsetY;
		this.offsetLimited = true;
		setOffset(offsetX, offsetY);
	}

	public Vector4i getOffsetLimits()
	{
		return new Vector4i(minOffsetX, minOffsetY, maxOffsetX, maxOffsetY);
	}

	public void allowPlayerInventory()
	{
		allowPlayerInventory = true;
	}

	public void setWindowBounds(int width, int height)
	{
		this.windowWidth = width;
		this.windowHeight = height;
	}

	public void setWindowPosition(int x, int y)
	{
		this.windowX = x;
		this.windowY = y;
	}

	int getAbsoluteWindowX()
	{
		return windowX + (parent == null ? 0 : parent.getAbsoluteWindowX());
	}

	int getAbsoluteWindowY()
	{
		return windowY + (parent == null ? 0 : parent.getAbsoluteWindowY());
	}

	public void setStickyWindow(boolean sticky)
	{
		this.stickyWindow = sticky;
	}

	public void applyMask(Mask mask)
	{
		mask.applyMask(this);
	}

	public void addWindow(Menu menu)
	{
		menu.parent = this;
		menu.build();
		windows.add(menu);
	}

	public void removeWindow()
	{
		if (parent == null)
			return;

		parent.windows.remove(this);
		parent.reload();
	}

	public Menu getParent()
	{
		return parent;
	}

	// region offset
	public void move(int x, int y)
	{
		setOffset(offsetX + x, offsetY + y);
	}

	public void setOffset(int x, int y)
	{
		int oldX = offsetX;
		int oldY = offsetY;

		if (offsetLimited)
		{
			this.offsetX = Math.max(minOffsetX, Math.min(maxOffsetX, x));
			this.offsetY = Math.max(minOffsetY, Math.min(maxOffsetY, y));
		} else
		{
			this.offsetX = x;
			this.offsetY = y;
		}

		if (oldX != offsetX || oldY != offsetY)
		{
			reload();
		}
	}

	public int getOffsetX()
	{
		return offsetX;
	}

	public int getOffsetY()
	{
		return offsetY;
	}
	// endregion offset

	// region slots
	private boolean inMenuBounds(Menu menu, int x, int y)
	{
		return x >= menu.windowX && x < menu.windowX + menu.windowWidth && y >= menu.windowY && y < menu.windowY + menu.windowHeight;
	}

	public Slot getSlot(int x, int y)
	{
		SlotLoc loc = new SlotLoc(x, y);

		for (int i = windows.size() - 1; i >= 0; i--)
		{
			Menu menu = windows.get(i);

			// Ignore non-sticky windows
			if (!menu.stickyWindow)
				continue;

			if (!inMenuBounds(menu, x, y))
				continue;

			Slot slot = menu.getSlot(x - menu.windowX, y - menu.windowY);
			if (slot != null)
				return slot;
		}

		Slot stickySlot = stickySlots.get(loc);
		if (stickySlot != null)
			return stickySlot;

		for (int i = windows.size() - 1; i >= 0; i--)
		{
			Menu menu = windows.get(i);

			// Ignore sticky windows as they have been already iterated over
			if (menu.stickyWindow)
				continue;

			if (!inMenuBounds(menu, x + offsetX, y + offsetY))
				continue;

			Slot slot = menu.getSlot(x - menu.windowX + offsetX, y - menu.windowY + offsetY);
			if (slot != null)
				return slot;
		}

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

	public void removeSlot(int x, int y)
	{
		slots.remove(new SlotLoc(x, y));
	}

	public void removeStickySlot(int x, int y)
	{
		stickySlots.remove(new SlotLoc(x, y));
	}
	// endregion slots

	// region control
	public void reload()
	{
		Inventory inv = getInventory();
		if (inv == null)
			return;

		for (int i = 0; i < windowWidth * windowHeight; i++)
		{
			int x = (i % windowWidth);
			int y = (i / windowWidth);

			int index;
			int visibleX = x + windowX;
			int visibleY = y + windowY;

			Menu loop = parent;
			while (loop != null)
			{
				visibleX += loop.windowX;
				visibleY += loop.windowY;
				loop = loop.parent;
			}

			// TODO: replace the 9 with actual inventory width
			index = visibleX + visibleY * 9;

			if (index < 0 || index >= inv.getSize())
				continue;

			Slot slot = getSlot(x, y);
			if (slot != null)
			{
				inv.setItem(index, slot.getIcon());
			} else
			{
				inv.setItem(index, MiscUtil.AIR);
			}
		}
	}

	@Deprecated(forRemoval = true)
	public void clear()
	{
		slots.clear();
		stickySlots.clear();
		if (inventory != null)
			inventory.clear();
		windows.clear();
		offsetX = 0;
		offsetY = 0;
	}

	public Menu build()
	{
		if (isBuilt)
			return this;
		isBuilt = true;
		setup();
		reload();
		return this;
	}

	/**
	 * Should be called before opening AnvilGUI
	 */
	public void anvilRedirect()
	{
		anvilRedirectState = AnvilRedirectState.ANVIL_OPEN_1;
	}

	public boolean hasHistory()
	{
		return history.filter(historyList -> !historyList.isEmpty()).isPresent();
	}

	public Optional<LinkedList<Menu>> getHistory()
	{
		return history;
	}

	public Response goBackIntoThePast()
	{
		// I actually do not know what this does, IntelliJ suggested this
		return history.map(menus -> Response.redirect(menus.getLast())).orElseGet(Response::cancel);
	}

	public int getWindowWidth()
	{
		return windowWidth;
	}

	public int getWindowHeight()
	{
		return windowHeight;
	}

	public Inventory getInventory()
	{
		if (parent != null)
			return parent.getInventory();

		return inventory;
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
	// endregion control

	// region events
	public Response onClose(Player player)
	{
		return Response.allow();
	}
	// endregion events

	/*
	 * Opening
	 */

	public void showToPlayers(Player... players)
	{
		for (Player player : players)
		{
			showToPlayer(player);
		}
	}

	public void showToPlayer(Player player)
	{
		if (inventory == null)
			inventory = Bukkit.createInventory(null, rows * 9, title);
		build();
		player.openInventory(inventory);
		MetaUtil.setMeta(player, MenuListener.MENU_META_KEY, this);
	}
}
