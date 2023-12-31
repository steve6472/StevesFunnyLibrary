package steve6472.standalone.interactable.ex.impl.func;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
public class DebugHereExp extends Expression
{
	private static final ElementType DEBUG = new ElementType("debug", 0, () -> ExpItems.DEBUG_HERE.newItemStack());
	private static final ElementType INC = new ElementType("inc", 1, () -> ExpItems.DEBUG_HERE_INC.newItemStack());
	private static final ElementType DEC = new ElementType("dec", 2, () -> ExpItems.DEBUG_HERE_DEC.newItemStack());

	int id;
	private final ElementType ID = new ElementType("id", 3, () -> ItemStackBuilder.edit(ExpItems.DEBUG_HERE_ID.newItemStack()).setName(ChatColor.DARK_AQUA + "Debug ID: " + id).buildItemStack());

	public DebugHereExp()
	{
		super(Type.CONTROL, 5, 1);
	}

	public DebugHereExp(int id)
	{
		super(Type.CONTROL, 5, 1);
		this.id = id;
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		Bukkit.broadcastMessage("Here " + id);
		return ExpResult.PASS;
	}

	@Override
	public void build(ExpBuilder builder, int x, int y)
	{
		builder.setSlot(x + 1, y, DEBUG);
		builder.setSlot(x + 2, y, INC);
		builder.setSlot(x + 3, y, ID);
		builder.setSlot(x + 4, y, DEC);
	}

	@Override
	public Response action(IElementType type, Click click, Menu menu, Expression expression)
	{
		if (type == INC)
		{
			id++;
//			menu.getSlot(click.slot().getX() + 1, click.slot().getY()).setItem(ID.item());
		}

		if (type == DEC)
		{
			id--;
//			menu.getSlot(click.slot().getX() - 1, click.slot().getY()).setItem(ID.item());
		}

		if (type == ID)
			Bukkit.broadcastMessage("Current Here is " + id);

		return Response.cancel();
	}

	@Override
	public IElementType[] getTypes()
	{
		return new IElementType[]
			{
				DEBUG, INC, DEC, ID
			};
	}

	@Override
	public String stringify(boolean flag)
	{
		return "debug " + id;
	}

	@Override
	public void toNBT(NBT compound)
	{
		compound.setInt("id", id);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		id = compound.getInt("id", 0);
	}
}
