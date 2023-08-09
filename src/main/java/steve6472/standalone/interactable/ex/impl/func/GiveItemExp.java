package steve6472.standalone.interactable.ex.impl.func;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.standalone.interactable.ex.*;
import steve6472.standalone.interactable.ex.elements.ElementType;
import steve6472.standalone.interactable.ex.elements.IElementType;
import steve6472.standalone.interactable.ex.elements.EventReferenceType;
import steve6472.standalone.interactable.ex.event.InputType;

/**
 * Created by steve6472
 * Date: 6/18/2023
 * Project: StevesFunnyLibrary <br>
 */
public class GiveItemExp extends Expression
{
	ItemStack itemToGive;
	CodeBlockExp count;

	private final ElementType INFO = new ElementType("info", 0, () -> ItemStackBuilder.quick(Material.REDSTONE_TORCH, "Information", ChatColor.GOLD));
	private final EventReferenceType<Player> TARGET = new EventReferenceType<>("target", 1, InputType.PLAYER);
	private final ElementType ITEM = new ElementType("item", 2, () ->
	{
		if (itemToGive == null || itemToGive.getType().isAir())
			return ItemStackBuilder.quick(Material.STRUCTURE_VOID, "Click with item to set", ChatColor.GRAY);
		return itemToGive;
	});

	public GiveItemExp()
	{
		super(Type.CONTROL, 4, 1);
		count = CodeBlockExp.executor(this, Type.INT).setPlaceholderIcon(ItemStackBuilder.quick(Material.IRON_NUGGET, "Count", ChatColor.GRAY));
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		if (count.execute(context) == ExpResult.DELAY) return ExpResult.DELAY;
		Object result = count.getResult().getRaw();
		if (!(result instanceof Integer itemCount)) return ExpResult.PASS;

		for (int i = 0; i < itemCount; i++)
		{
			Player p = context.getEventData().get(TARGET);
			if (p != null)
				p.getInventory().addItem(itemToGive);
		}

		count.reset();
		return ExpResult.PASS;
	}

	@Override
	public void build(ExpBuilder builder, int x, int y)
	{
		builder.setSlot(x, y, INFO);
		builder.setSlot(x + 1, y, TARGET);
		builder.setSlot(x + 2, y, ITEM);

		builder.build(count, x + 3, y);
	}

	@Override
	public Response action(IElementType type, Click click, Menu menu, Expression expression)
	{
		if (type == TARGET)
			return TARGET.action(click, menu, expression);

		if (type == ITEM)
		{
			if (click.itemOnCursor() == null || click.itemOnCursor().getType().isAir())
				itemToGive = null;
			else
				itemToGive = click.itemOnCursor().clone();
//			click.slot().setItem(ITEM.item());
		}

		return Response.cancel();
	}

	@Override
	public IElementType[] getTypes()
	{
		return new IElementType[] {INFO, TARGET, ITEM};
	}

	@Override
	public String stringify(boolean flag)
	{
		return "Give Item";
	}

	@Override
	public void toNBT(NBT compound)
	{
		if (itemToGive != null)
			compound.setItemStack("item", itemToGive);

		compound.setCompound("count", Expressions.saveExpression(compound.createCompound(), count));

		NBT target = compound.createCompound();
		TARGET.toNBT(target);
		compound.setCompound("target", target);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		if (compound.hasItemStack("item"))
			itemToGive = compound.getItemStack("item");

		count = Expressions.loadExpression(compound.getOrCreateCompound("count"), this, CodeBlockExp.class);

		NBT target = compound.getOrCreateCompound("target");
		TARGET.fromNBT(target);
	}
}
