package steve6472.funnylib.json.codec.codecs;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.MiscUtil;

import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
@Deprecated(forRemoval = true)
public class MarkerCodec
{
	@Deprecated(forRemoval = true)
	public static SlotBuilder slotBuilder(Marker current, Consumer<Marker> set)
	{
		return SlotBuilder
			.create(current.toItem())
			.allow(InventoryAction.PICKUP_ALL, InventoryAction.PLACE_ALL, InventoryAction.PICKUP_HALF)
			.allow(ClickType.LEFT, ClickType.RIGHT)
			.onClick((c, cm) ->
			{
				if (c.type() == ClickType.RIGHT)
				{
					ItemStack currentItem = current.toItem();
					if (c.itemOnCursor().getType().isAir() && !currentItem.getType().isAir())
					{
						return Response.setItemToCursor(currentItem);
					}
				}

				if (c.itemOnCursor().getType().isAir())
				{
					set.accept(null);
					c.slot().setItem(MiscUtil.AIR);
					return Response.cancel();
				}

				CustomItem customItem = Items.getCustomItem(c.itemOnCursor());
				if (customItem != FunnyLib.LOCATION_MARKER)
					return Response.cancel();

				set.accept(Marker.fromItem(c.itemOnCursor()));
				ItemStack clone = c.itemOnCursor().clone();
				clone.setAmount(1);
				c.slot().setItem(clone);
				return Response.cancel();
			});
	}
}
