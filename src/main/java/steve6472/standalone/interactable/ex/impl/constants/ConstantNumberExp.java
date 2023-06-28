package steve6472.standalone.interactable.ex.impl.constants;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.standalone.interactable.ex.*;
import steve6472.standalone.interactable.ex.elements.ElementType;
import steve6472.standalone.interactable.ex.elements.IElementType;

/**
 * Created by steve6472
 * Date: 10/22/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ConstantNumberExp extends Expression
{
	int number;

	private final ElementType LABEL = new ElementType("label", 0, () -> ItemStackBuilder.edit(ExpItems.CONSTANT_NUMBER.newItemStack()).setName(ChatColor.DARK_AQUA + "Number: " + ChatColor.WHITE + number).buildItemStack());

	public ConstantNumberExp()
	{
		super(Type.INT, 1, 1);
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
	public void toNBT(NBT compound)
	{
		compound.setInt("number", number);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		number = compound.getInt("number", 0);
	}
}
