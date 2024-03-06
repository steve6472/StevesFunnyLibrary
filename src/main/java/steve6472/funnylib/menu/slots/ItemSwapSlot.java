package steve6472.funnylib.menu.slots;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.components.Disable;
import steve6472.funnylib.menu.components.DisableComponent;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Procedure;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 1/31/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ItemSwapSlot extends ItemSlot implements Disable<ItemSwapSlot>
{
	private final DisableComponent disabled = new DisableComponent(this, ItemStackBuilder.quick(Material.GRAY_DYE, "Disabled Swap Slot", ChatColor.DARK_GRAY));

	private ItemStack placedItem;
	private Supplier<ItemStack> clearStack;
	private Predicate<ItemStack> itemCheck = stack -> true;
	private Consumer<ItemStack> onPlace;
	private Procedure onClear;
	private boolean rightClickClear = true;

	public ItemSwapSlot(ItemStack placedItem, boolean isSticky)
	{
		super(addLore(false, placedItem == null ? null : placedItem.clone()), isSticky);
		this.placedItem = placedItem == null ? null : placedItem.clone();
	}

	public ItemSwapSlot(ItemStack placedItem, boolean rightClickClear, boolean isSticky)
	{
		super(addLore(rightClickClear, placedItem == null ? null : placedItem.clone()), isSticky);
		this.rightClickClear = rightClickClear;
		this.placedItem = placedItem == null ? null : placedItem.clone();
	}

	public ItemSwapSlot setItemCheck(@NotNull Predicate<ItemStack> itemCheck)
	{
		this.itemCheck = itemCheck;
		return this;
	}

	public ItemSwapSlot onClear(Procedure onClear)
	{
		this.onClear = onClear;
		return this;
	}

	/**
	 * Passes a clone of the ItemStack that was placed into slot <br/>
	 * This copy is not modifiable!
	 *
	 * @param onPlace action on place (called only when valid item is inserted)
	 * @return itself
	 */
	public ItemSwapSlot onPlace(Consumer<ItemStack> onPlace)
	{
		this.onPlace = onPlace;
		return this;
	}

	public ItemSwapSlot setRightClickClear(boolean rightClickClear)
	{
		this.rightClickClear = rightClickClear;
		updateSlot();
		return this;
	}

	public ItemSwapSlot setClearItem(Supplier<ItemStack> supplier)
	{
		this.clearStack = supplier;
		return this;
	}

	@Override
	public boolean canBeInteractedWith(Click click)
	{
		return ButtonSlot.interactionCheckWithDisable(this, click);
	}

	@Override
	public ItemStack getIcon()
	{
		if (isDisabled()) return getDisabledIcon();
		return super.getIcon();
	}

	@Override
	public Response onClick(Click click)
	{
		if (isDisabled())
			return getDisabledResponse();

		if (rightClickClear && click.type().isRightClick())
		{
			if (onClear != null)
				onClear.apply();
			setIcon(clearStack == null ? new ItemStack(Material.AIR) : clearStack.get());
			placedItem = null;
			return Response.cancel();
		}

		ItemStack cursorStack = click.itemOnCursor();
		if (cursorStack == null || cursorStack.getType().isAir())
		{
			if (placedItem == null)
				return Response.cancel();
			return Response.setItemToCursor(placedItem.clone());
		}

		if (itemCheck.test(cursorStack))
		{
			placedItem = cursorStack.clone();

			if (onPlace != null)
				onPlace.accept(cursorStack.clone());

			ItemStack clone = cursorStack.clone();
			addLore(rightClickClear, clone);

			setIcon(clone);
			return Response.cancel();
		}

		return Response.cancel();
	}

	private static ItemStack addLore(boolean rightClickClear, ItemStack itemStack)
	{
		if (itemStack == null || itemStack.getType().isAir())
			return itemStack;

		ItemStackBuilder.modify(itemStack, builder ->
		{
			if (rightClickClear)
			{
				builder.addLore();
				builder.addLore(JSONMessage.create("-".repeat(17), ChatColor.DARK_GRAY).setItalic(false));
				builder.addLore(JSONMessage.create("Right click to remove", ChatColor.RED).setItalic(true));
			}
		});
		return itemStack;
	}

	@Override
	public DisableComponent getDisableComponent()
	{
		return disabled;
	}

	@Override
	public ItemSwapSlot getThis()
	{
		return this;
	}
}
