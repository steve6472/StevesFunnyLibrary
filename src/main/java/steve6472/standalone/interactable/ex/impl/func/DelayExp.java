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
public class DelayExp extends Expression
{
	private static final ElementType DELAY = new ElementType("delay", 0, () -> ExpItems.DELAY.newItemStack());
	private static final ElementType INC = new ElementType("inc", 1, () -> ExpItems.DELAY_INC.newItemStack());
	private static final ElementType DEC = new ElementType("dec", 2, () -> ExpItems.DELAY_DEC.newItemStack());

	long ticks;
	private final ElementType LABEL = new ElementType("label", 3, () -> ItemStackBuilder.edit(ExpItems.DELAY_ID.newItemStack()).setName(ChatColor.DARK_AQUA + "Current Delay: " + ticks + " ticks").buildItemStack());

	public DelayExp()
	{
		super(Type.CONTROL, 5, 1);
	}

	public DelayExp(long ticks)
	{
		super(Type.CONTROL, 5, 1);
		this.ticks = ticks;
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		Bukkit.broadcastMessage("Delay for " + ticks + " ticks");
		context.delay(ticks);
		return ExpResult.DELAY;
	}

	@Override
	public void build(ExpBuilder builder, int x, int y)
	{
		builder.setSlot(x + 1, y, DELAY);
		builder.setSlot(x + 2, y, INC);
		builder.setSlot(x + 3, y, LABEL);
		builder.setSlot(x + 4, y, DEC);
	}

	@Override
	public Response action(IElementType type, Click click, Menu menu, Expression expression)
	{
		if (type == INC)
		{
			ticks++;
//			menu.getSlot(click.slot().getX() + 1, click.slot().getY()).setItem(LABEL.item());
		}

		if (type == DEC)
		{
			ticks--;
//			menu.getSlot(click.slot().getX() - 1, click.slot().getY()).setItem(LABEL.item());
		}

		if (type == LABEL)
			Bukkit.broadcastMessage("Current Delay is " + ticks);

		return Response.cancel();
	}

	@Override
	public IElementType[] getTypes()
	{
		return new IElementType[]
			{
				DELAY, INC, DEC, LABEL
			};
	}

	@Override
	public String stringify(boolean flag)
	{
		return "delay for " + ticks + " ticks";
	}

	@Override
	public void toNBT(NBT compound)
	{
		compound.setLong("ticks", ticks);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		ticks = compound.getLong("ticks", 0);
	}
}
