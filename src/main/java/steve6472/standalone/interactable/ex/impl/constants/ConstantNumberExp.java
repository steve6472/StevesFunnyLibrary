package steve6472.standalone.interactable.ex.impl.constants;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.json.JSONObject;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.standalone.interactable.ex.*;

/**
 * Created by steve6472
 * Date: 10/22/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ConstantNumberExp extends Expression
{
	int number;

	private final ElementType LABEL = new ElementType("label", 0, () -> ItemStackBuilder.edit(ExpItems.CONSTANT_NUMBER.newItemStack()).setName(ChatColor.DARK_AQUA + "Number: " + ChatColor.WHITE + number).buildItemStack());

	public ConstantNumberExp(int number)
	{
		this.number = number;
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		return new ExpResult(number);
	}

	@Override
	public void build(ExpBuilder builder, int x, int y)
	{
		builder.setSlot(x, y, LABEL);
	}

	@Override
	public Response action(IElementType type, Click click, Menu menu, Expression expression)
	{
		if (click.type() == ClickType.LEFT)
			number++;
		else if (click.type() == ClickType.RIGHT)
			number--;

		else if (click.type() == ClickType.SHIFT_LEFT)
			number += 10;
		else if (click.type() == ClickType.SHIFT_RIGHT)
			number -= 10;

		menu.getSlot(click.slot().getX(), click.slot().getY()).setItem(LABEL.item());

		return Response.cancel();
	}

	@Override
	public int getHeight()
	{
		return 1;
	}

	@Override
	public int getWidth()
	{
		return 1;
	}

	@Override
	public Type getType()
	{
		return Type.INT;
	}

	@Override
	public IElementType[] getTypes()
	{
		return new IElementType[] {LABEL};
	}

	@Override
	public String stringify(boolean flag)
	{
		return "" + number;
	}

	@Override
	public void save(JSONObject json)
	{
		json.put("number", number);
	}
}
