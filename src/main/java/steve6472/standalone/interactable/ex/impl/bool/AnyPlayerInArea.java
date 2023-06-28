package steve6472.standalone.interactable.ex.impl.bool;

import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.data.AreaSelection;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.serialize.NBT;
import steve6472.standalone.interactable.ex.*;
import steve6472.standalone.interactable.ex.elements.ElementType;
import steve6472.standalone.interactable.ex.elements.IElementType;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class AnyPlayerInArea extends Expression
{
	private static final BoundingBox BOX = new BoundingBox();

	AreaSelection area;

	private final ElementType AREA = new ElementType("area", 0, () ->
	{
		if (area == null)
			return ExpItems.AREA_LOCATION_EMPTY.newItemStack();

		List<String> lore = ItemStackBuilder.editNonStatic(area.toItem()).meta().getLore();

		ItemStackBuilder builder = ItemStackBuilder.editNonStatic(ExpItems.AREA_LOCATION_FULL.newItemStack());
		if (lore != null)
		{
			lore.forEach(text -> builder.addLore(JSONMessage.create(text).setItalic(JSONMessage.ItalicType.FALSE)));
		}
		return builder.buildItemStack();
	});

	public AnyPlayerInArea()
	{
		super(Type.BOOL, 1, 1);
	}

	public AnyPlayerInArea(AreaSelection area)
	{
		super(Type.BOOL, 1, 1);
		this.area = area;
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		if (area == null)
			return new ExpResult(false);

		BOX.resize(area.getStart().x, area.getStart().y, area.getStart().z, area.getEnd().x + 1, area.getEnd().y + 1, area.getEnd().z + 1);
		for (Player player : context.getWorld().getPlayers())
		{
			if (player.getBoundingBox().overlaps(BOX))
			{
				return new ExpResult(true);
			}
		}

		return new ExpResult(false);
	}

	@Override
	public void build(ExpBuilder builder, int x, int y)
	{
		builder.setSlot(x, y, AREA);
	}

	@Override
	public Response action(IElementType type, Click click, Menu menu, Expression expression)
	{
		if (type == AREA)
		{
			if (Items.getCustomItem(click.itemOnCursor()) == FunnyLib.AREA_LOCATION_MARKER)
			{
				area = AreaSelection.fromItem(click.itemOnCursor());
				menu.getSlot(click.slot().getX(), click.slot().getY()).setItem(AREA.item());
			}
		}
		return Response.cancel();
	}

	@Override
	public IElementType[] getTypes()
	{
		return new IElementType[] {AREA};
	}

	@Override
	public String stringify(boolean flag)
	{
		return "Area";
	}

	@Override
	public void toNBT(NBT compound)
	{
		if (area != null)
		{
			NBT areaTag = compound.createCompound();
			area.toNBT(areaTag);
			compound.setCompound("area", areaTag);
		}
	}

	@Override
	public void fromNBT(NBT compound)
	{
		if (compound.hasCompound("area"))
		{
			area = new AreaSelection();
			area.fromNBT(compound.getCompound("area"));
		}
	}
}
