package steve6472.funnylib.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary
 */
public class MenuBuilder
{
	private final int rows;
	private final String title;

	private boolean allowPlayerInventory;
	private boolean recordHistory;
	private boolean offsetLimited;
	private int minOffsetX, maxOffsetX, minOffsetY, maxOffsetY;

	private BiFunction<Menu, Player, Response> onClose;
	private Consumer<MenuBuilder> customBuilder;

	private final ArbitraryData arbitraryData = new ArbitraryData();

	final Map<SlotLoc, SlotBuilder> slots = new HashMap<>();
	final Map<SlotLoc, SlotBuilder> stickySlots = new HashMap<>();
	final Map<SlotLoc, ISlotBuilder> slotBuilders = new HashMap<>();

	private MenuBuilder(int rows, String title)
	{
		this.rows = rows;
		this.title = title;
	}

	public static MenuBuilder create(int rows, String title)
	{
		return new MenuBuilder(rows, title);
	}

	public MenuBuilder setOnClose(BiFunction<Menu, Player, Response> onClose)
	{
		this.onClose = onClose;
		return this;
	}

	/*
	 * Slots
	 */

	public MenuBuilder slot(int index, SlotBuilder builder)
	{
		addSlot(new SlotLoc(index % 9, index / 9), builder);
		return this;
	}

	public MenuBuilder slot(int x, int y, SlotBuilder builder)
	{
		addSlot(new SlotLoc(x, y), builder);
		return this;
	}

	public MenuBuilder slots(int fromX, int fromY, int toX, int toY, SlotBuilder builder)
	{
		for (int i = fromX; i < toX; i++)
		{
			for (int j = fromY; j < toY; j++)
			{
				addSlot(new SlotLoc(i, j), builder);
			}
		}
		return this;
	}

	private void addSlot(SlotLoc key, SlotBuilder slot)
	{
		if (slot.isSticky())
		{
			stickySlots.put(key, slot);
		} else
		{
			slots.put(key, slot);
		}
	}

	/*
	 * Other ways of... doing slots
	 */

	public MenuBuilder slot(int x, int y, ISlotBuilder builder)
	{
		slotBuilders.put(new SlotLoc(x, y), builder);
		return this;
	}

	public MenuBuilder applyMask(Mask mask)
	{
		mask.applyMask(this);
		return this;
	}

	public MenuBuilder customBuilder(Consumer<MenuBuilder> customBuilder)
	{
		if (this.customBuilder != null)
		{
			throw new RuntimeException("Custom Builder already exists!");
		}
		this.customBuilder = customBuilder;
		return this;
	}

	/*
	 * Flags
	 */

	public MenuBuilder recordHistory()
	{
		recordHistory = true;
		return this;
	}

	public MenuBuilder allowPlayerInventory()
	{
		allowPlayerInventory = true;
		return this;
	}

	public MenuBuilder limitOffset(int minOffsetX, int maxOffsetX, int minOffsetY, int maxOffsetY)
	{
		this.minOffsetX = minOffsetX;
		this.maxOffsetX = maxOffsetX;
		this.minOffsetY = minOffsetY;
		this.maxOffsetY = maxOffsetY;
		this.offsetLimited = true;
		return this;
	}

	/*
	 * Arbitrary data
	 */

	public <T> MenuBuilder setData(String key, T data)
	{
		arbitraryData.setData(key, data);
		return this;
	}

	public <T> T getData(String key, Class<T> expectedType)
	{
		return arbitraryData.getData(key, expectedType);
	}

	public MenuBuilder removeData(String key)
	{
		arbitraryData.removeData(key);
		return this;
	}

	public MenuBuilder copyFrom(ArbitraryData otherData)
	{
		arbitraryData.copyFrom(otherData);
		return this;
	}

	public ArbitraryData getArbitraryData()
	{
		return arbitraryData;
	}

	/*
	 * Building Menu
	 */

	public Menu build()
	{
		if (customBuilder != null)
		{
			customBuilder.accept(this);
		}

		slotBuilders.forEach((k, v) ->
		{
			SlotBuilder builder = v.builder(this);
			addSlot(k, builder);
		});

		Inventory inv = Bukkit.createInventory(null, rows * 9, title);
		Menu menu = new Menu(inv);

		slots.forEach((slotLoc, slotBuilder) -> {
			Slot slot = slotBuilder.build();
			slot.holder = menu;
			slot.x = slotLoc.x();
			slot.y = slotLoc.y();
			menu.slots.put(slotLoc, slot);
			int index = slotLoc.toIndex(0, 0, rows);
			if (index < rows * 9 && index >= 0)
			{
				inv.setItem(index, slot.item());
			}
		});

		stickySlots.forEach((slotLoc, slotBuilder) -> {
			Slot slot = slotBuilder.build();
			slot.holder = menu;
			slot.x = slotLoc.x();
			slot.y = slotLoc.y();
			menu.stickySlots.put(slotLoc, slot);
			int index = slotLoc.toIndex(0, 0, rows);
			if (index < rows * 9 && index >= 0)
			{
				inv.setItem(index, slot.item());
			}
		});

		menu.rows = rows;
		menu.onClose = onClose;
		menu.allowPlayerInventory = allowPlayerInventory;
		menu.recordHistory = recordHistory;
		menu.minOffsetX = minOffsetX;
		menu.minOffsetY = minOffsetY;
		menu.maxOffsetX = maxOffsetX;
		menu.maxOffsetY = maxOffsetY;
		menu.offsetLimited = offsetLimited;
		menu.passedData = arbitraryData.copy();

		return menu;
	}
}
