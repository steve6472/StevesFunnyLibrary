package steve6472.funnylib.menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by steve6472
 * Date: 2/9/2024
 * Project: StevesFunnyLibrary <br>
 */
public class FakeInventoryClickEvent extends InventoryClickEvent
{
	private final ItemStack cursorStack;

	public FakeInventoryClickEvent(@NotNull InventoryView view, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action, @NotNull ItemStack cursorStack)
	{
		super(view, type, slot, click, action);
		this.cursorStack = cursorStack;
	}

	@Nullable
	@Override
	public ItemStack getCursor()
	{
		return cursorStack.clone();
	}
}
