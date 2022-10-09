package steve6472.standalone.interactable.ex;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.Slot;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 10/7/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExpBuilder
{
	public static final String ELEMENT_TYPE = "element_type";

	private final Menu menu;
	private Expression currentExpression;

	public ExpBuilder(Menu menu)
	{
		this.menu = menu;
	}

	public void build(Expression expression, int x, int y)
	{
		this.currentExpression = expression;
		expression.build(this, x, y);
	}

	public void setCurrentExpression(Expression expression)
	{
		this.currentExpression = expression;
	}

	public void setSlot(int x, int y, IElementType elementType)
	{
		SlotBuilder slotBuilder = SlotBuilder.create(elementType.item());
		slotBuilder.allow(ClickType.LEFT, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT, ClickType.MIDDLE);
		slotBuilder.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.CLONE_STACK, InventoryAction.MOVE_TO_OTHER_INVENTORY);
		final Expression finalExp = currentExpression;
		slotBuilder.onClick((click, menu) -> finalExp.action(elementType, click, menu, null));
		Slot slot = slotBuilder.build();
		menu.setSlot(x, y, slot);

		ItemStackBuilder builder = ItemStackBuilder.editNonStatic(slot.item());
		builder.addLore(JSONMessage.create("Element: ").color(ChatColor.GRAY).then(elementType.label()).color(ChatColor.WHITE));
		builder.addLore("");
		builder.addLore(Integer.toHexString(currentExpression.hashCode()), ChatColor.DARK_GRAY);
		builder.customTagInt(ELEMENT_TYPE, elementType.ordinal());
		slot.setItem(builder.buildItemStack());
	}
}
