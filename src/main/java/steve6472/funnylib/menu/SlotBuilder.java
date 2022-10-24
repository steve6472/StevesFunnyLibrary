package steve6472.funnylib.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.MiscUtil;
import steve6472.funnylib.util.Preconditions;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary
 */
public class SlotBuilder
{
	private final ItemStack item;
	private Set<ClickType> allowedClickTypes = new HashSet<>();
	private Set<InventoryAction> allowedInventoryActions = new HashSet<>();
	private Map<ClickType, BiFunction<Click, Menu, Response>> conditionedClick = new HashMap<>();
	private BiFunction<Click, Menu, Response> onClick;
	private boolean isSticky;

	private SlotBuilder(ItemStack item)
	{
		if (item == null)
		{
			this.item = MiscUtil.AIR;
		} else
		{
			this.item = item;
		}
	}

	public static SlotBuilder create(ItemStack item)
	{
		return new SlotBuilder(item);
	}

	public static SlotBuilder create()
	{
		return new SlotBuilder(MiscUtil.AIR);
	}

	// region Presets

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

	public static ISlotBuilder toggleSlot(String label, Function<ArbitraryData, Boolean> get, BiConsumer<ArbitraryData, Boolean> set)
	{
		return builder -> SlotBuilder.create(createToggleItem(get.apply(builder.getArbitraryData()), label)).allow(ClickType.LEFT).allow(InventoryAction.PICKUP_ALL).onClick((c, cm) ->
		{
			boolean current = !get.apply(builder.getArbitraryData());
			set.accept(cm.getPassedData(), current);
			c.slot().setItem(createToggleItem(current, label));
			return Response.cancel();
		});
	}

	public static ISlotBuilder buttonSlot(ItemStack icon, BiConsumer<Click, Menu> action)
	{
		return builder -> SlotBuilder.create(icon).allow(ClickType.LEFT).allow(InventoryAction.PICKUP_ALL).onClick((c, cm) ->
		{
			action.accept(c, cm);
			return Response.cancel();
		});
	}

	public static ISlotBuilder stickyButtonSlot(ItemStack icon, BiConsumer<Click, Menu> action)
	{
		return builder -> SlotBuilder.create(icon).setSticky().allow(ClickType.LEFT).allow(InventoryAction.PICKUP_ALL).onClick((c, cm) ->
		{
			action.accept(c, cm);
			return Response.cancel();
		});
	}

	public static SlotBuilder stickyButtonSlot_(ItemStack icon, BiConsumer<Click, Menu> action)
	{
		return SlotBuilder.create(icon).setSticky().allow(ClickType.LEFT).allow(InventoryAction.PICKUP_ALL).onClick((c, cm) ->
		{
			action.accept(c, cm);
			return Response.cancel();
		});
	}

	public static ISlotBuilder buttonSlot(Material material, String label, BiConsumer<Click, Menu> action)
	{
		ItemStack icon = ItemStackBuilder
			.create(material)
			.setName(label)
			.addLore(JSONMessage.create("Button").color(ChatColor.GRAY))
			.buildItemStack();

		return builder -> SlotBuilder.create(icon).allow(ClickType.LEFT).allow(InventoryAction.PICKUP_ALL).onClick((c, cm) ->
		{
			action.accept(c, cm);
			return Response.cancel();
		});
	}

	public static ISlotBuilder buttonSlotResponse(Material material, String label, BiFunction<Click, Menu, Response> action)
	{
		ItemStack icon = ItemStackBuilder
			.create(material)
			.setName(label)
			.addLore(JSONMessage.create("Button").color(ChatColor.GRAY))
			.buildItemStack();

		return builder -> SlotBuilder.create(icon).allow(ClickType.LEFT).allow(InventoryAction.PICKUP_ALL).onClick(action);
	}

	public static ISlotBuilder itemSlot(Function<ArbitraryData, ItemStack> get, BiConsumer<ArbitraryData, ItemStack> set)
	{
		return itemSlot(get, set, i -> true);
	}

	public static ISlotBuilder itemSlot(Function<ArbitraryData, ItemStack> get, BiConsumer<ArbitraryData, ItemStack> set, Predicate<ItemStack> predicate)
	{
		return builder -> SlotBuilder.create(get.apply(builder.getArbitraryData()))
			.allow(InventoryAction.PICKUP_ALL, InventoryAction.PLACE_ALL, InventoryAction.PICKUP_HALF)
			.allow(ClickType.LEFT, ClickType.RIGHT)
			.onClick((c, cm) ->
			{
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
			});
	}

	// endregion

	/*
	 * Click types
	 */

	public SlotBuilder allow(ClickType... clickTypes)
	{
		Collections.addAll(allowedClickTypes, clickTypes);
		return this;
	}

	public SlotBuilder allow(InventoryAction... inventoryActions)
	{
		Collections.addAll(allowedInventoryActions, inventoryActions);
		return this;
	}

	public SlotBuilder setSticky()
	{
		isSticky = true;
		return this;
	}

	public boolean isSticky()
	{
		return isSticky;
	}

	/**
	 * Runs on all allowed clicks
	 * @param onClick function to run
	 * @return Builder
	 */
	public SlotBuilder onClick(BiFunction<Click, Menu, Response> onClick)
	{
		this.onClick = onClick;
		return this;
	}

	/**
	 * Has priority over universal click. <br>
	 * The universal click will not run if this runs.
	 * @param type type of click, automatically allowed
	 * @param onClick function to run
	 * @return Builder
	 */
	public SlotBuilder onClick(ClickType type, BiFunction<Click, Menu, Response> onClick)
	{
		allow(type);
		conditionedClick.put(type, onClick);
		return this;
	}

	public Slot build()
	{
		Slot slot = new Slot();
		slot.itemStack = item.clone();
		slot.allowedClickTypes = allowedClickTypes;
		slot.allowedInventoryActions = allowedInventoryActions;
		slot.onClick = onClick;
		slot.conditionedClick = conditionedClick;
		slot.isSticky = isSticky;

		return slot;
	}
}
