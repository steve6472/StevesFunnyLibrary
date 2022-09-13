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

	private BiFunction<Menu, Player, Response> onClose;
	private Consumer<MenuBuilder> customBuilder;

	final Map<SlotLoc, SlotBuilder> slots = new HashMap<>();
	final Map<SlotLoc, SlotBuilder> stickySlots = new HashMap<>();

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
			stickySlots.putIfAbsent(key, slot);
		else
			slots.putIfAbsent(key, slot);
	}

	public MenuBuilder customBuilder(Consumer<MenuBuilder> customBuilder)
	{
		this.customBuilder = customBuilder;
		return this;
	}

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

	public Menu build()
	{
		if (customBuilder != null)
		{
			customBuilder.accept(this);
		}

		Inventory inv = Bukkit.createInventory(null, rows * 9, title);
		Menu menu = new Menu(inv);

		slots.forEach((slotLoc, slotBuilder) -> {
			Slot slot = slotBuilder.build();
			slot.holder = menu;
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
			menu.stickySlots.put(slotLoc, slot);
			int index = slotLoc.toIndex(0, 0, rows);
			if (index < rows * 9 && index >= 0)
			{
				inv.setItem(index, slot.item());
			}
		});

		menu.onClose = onClose;
		menu.allowPlayerInventory = allowPlayerInventory;
		menu.recordHistory = recordHistory;

		return menu;
	}
}
