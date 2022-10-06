package steve6472.funnylib.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.MiscUtil;
import steve6472.standalone.interactable.blocks.ElevatorControllerBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

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
	 * Presets
	 */

	private static ItemStack createToggleItem(boolean flag, String label)
	{
		Material mat = flag ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
		ChatColor color = flag ? ChatColor.GREEN : ChatColor.RED;
		return ItemStackBuilder
			.create(mat)
			.setName(label)
			.addLore(JSONMessage.create("Current: ").color(ChatColor.GRAY).then("" + flag).color(color))
			.setCustomModelData(1)
			.buildItemStack();
	}

	public MenuBuilder toggleSlot(int x, int y, String label, Function<ArbitraryData, Boolean> get, BiConsumer<ArbitraryData, Boolean> set)
	{
		slot(x, y, (builder) -> SlotBuilder.create(createToggleItem(get.apply(arbitraryData), label)).allow(ClickType.LEFT).allow(InventoryAction.PICKUP_ALL).onClick((c, cm) ->
		{
			boolean current = !get.apply(arbitraryData);
			set.accept(cm.getPassedData(), current);
			c.slot().setItem(createToggleItem(current, label));
			return Response.cancel();
		}));
		return this;
	}

	public MenuBuilder buttonSlot(int x, int y, Material material, String label, BiConsumer<Click, Menu> action)
	{
		ItemStack icon = ItemStackBuilder
			.create(material)
			.setName(label)
			.addLore(JSONMessage.create("Button").color(ChatColor.GRAY))
			.buildItemStack();

		slot(x, y, (builder) -> SlotBuilder.create(icon).allow(ClickType.LEFT).allow(InventoryAction.PICKUP_ALL).onClick((c, cm) ->
		{
			action.accept(c, cm);
			return Response.cancel();
		}));
		return this;
	}

	public MenuBuilder buttonSlotResponse(int x, int y, Material material, String label, BiFunction<Click, Menu, Response> action)
	{
		ItemStack icon = ItemStackBuilder
			.create(material)
			.setName(label)
			.addLore(JSONMessage.create("Button").color(ChatColor.GRAY))
			.buildItemStack();

		slot(x, y, (builder) -> SlotBuilder.create(icon).allow(ClickType.LEFT).allow(InventoryAction.PICKUP_ALL).onClick(action));
		return this;
	}

	public MenuBuilder itemSlot(int x, int y, Function<ArbitraryData, ItemStack> get, BiConsumer<ArbitraryData, ItemStack> set)
	{
		return itemSlot(x, y, get, set, i -> true);
	}

	public MenuBuilder itemSlot(int x, int y, Function<ArbitraryData, ItemStack> get, BiConsumer<ArbitraryData, ItemStack> set, Predicate<ItemStack> predicate)
	{
		slot(
			x,
			y,
			(builder) -> SlotBuilder.create(get.apply(builder.getArbitraryData()))
				.allow(InventoryAction.PICKUP_ALL, InventoryAction.PLACE_ALL, InventoryAction.PICKUP_HALF)
				.allow(ClickType.LEFT, ClickType.RIGHT)
				.onClick((c, cm) -> {

					if (c.type() == ClickType.RIGHT)
					{
						ItemStack current = get.apply(cm.getPassedData());
						if (c.itemOnCursor().getType().isAir() && !current.getType().isAir())
						{
							return Response.setItemToCursor(current);
						}
					}

					if (c.type() == ClickType.LEFT)
					{
						if (c.itemOnCursor().getType().isAir())
						{
							set.accept(cm.getPassedData(), MiscUtil.AIR);
							c.slot().setItem(MiscUtil.AIR);
							return Response.cancel();
						}

						if (predicate.test(c.itemOnCursor()))
						{
							set.accept(cm.getPassedData(), c.itemOnCursor().clone());
							ItemStack clone = c.itemOnCursor().clone();
							c.slot().setItem(clone);
						}
					}

					return Response.cancel();
				})

		);
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
