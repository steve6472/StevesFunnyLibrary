package steve6472.standalone.interactable.ex.impl.bool;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.joml.Vector3i;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MiscUtil;
import steve6472.funnylib.serialize.NBT;
import steve6472.standalone.interactable.ex.*;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class AnyPlayerInArea extends Expression
{
	private static final BoundingBox BOX = new BoundingBox();

	ItemStack areaStack;

	private final ElementType AREA = new ElementType("area", 0, () ->
	{
		if (Items.getCustomItem(areaStack) == FunnyLib.AREA_LOCATION_MARKER)
		{
			List<String> lore = ItemStackBuilder.editNonStatic(areaStack).meta().getLore();

			ItemStackBuilder builder = ItemStackBuilder.editNonStatic(ExpItems.AREA_LOCATION_FULL.newItemStack());
			if (lore != null)
			{
				lore.forEach(builder::addLore);
			}
			return builder.buildItemStack();
		} else
		{
			return ExpItems.AREA_LOCATION_EMPTY.newItemStack();
		}
	});

	public AnyPlayerInArea(ItemStack area)
	{
		this.areaStack = area;
	}

	@Override
	public ExpResult execute(ExpContext context)
	{
		if (Items.getCustomItem(areaStack) == FunnyLib.AREA_LOCATION_MARKER)
		{
			ItemNBT itemData = ItemNBT.create(areaStack);
			Vector3i start = itemData.get3i("start");
			Vector3i end = itemData.get3i("end");
			BOX.resize(start.x, start.y, start.z, end.x + 1, end.y + 1, end.z + 1);
			for (Player player : context.getWorld().getPlayers())
			{
				if (player.getBoundingBox().overlaps(BOX))
				{
					return new ExpResult(true);
				}
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
				areaStack = click.itemOnCursor().clone();
				menu.getSlot(click.slot().getX(), click.slot().getY()).setItem(AREA.item());
			}
		}
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
		return Type.BOOL;
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
	public void save(JSONObject json)
	{
		json.put("area", MiscUtil.serializeItemStack(areaStack));
	}
}
