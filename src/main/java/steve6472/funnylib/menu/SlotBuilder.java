package steve6472.funnylib.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.util.MiscUtil;
import steve6472.funnylib.util.Preconditions;

import java.util.*;
import java.util.function.BiFunction;

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
		Preconditions.checkNotNull(item, "ItemStack can no be null! Use create() to create empty slot instead!");
		this.item = item;
	}

	public static SlotBuilder create(ItemStack item)
	{
		return new SlotBuilder(item);
	}

	public static SlotBuilder create()
	{
		return new SlotBuilder(MiscUtil.AIR);
	}

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
		slot.itemStack = item;
		slot.allowedClickTypes = allowedClickTypes;
		slot.allowedInventoryActions = allowedInventoryActions;
		slot.onClick = onClick;
		slot.conditionedClick = conditionedClick;
		slot.isSticky = isSticky;

		return slot;
	}
}
