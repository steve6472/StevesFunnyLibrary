package steve6472.funnylib.json.codec.codecs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.category.ICategorizable;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.MarkerItem;
import steve6472.funnylib.json.codec.Codec;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MiscUtil;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class MarkerCodec extends Codec<MarkerCodec.Marker>
{
	public static final class Marker implements ICategorizable
	{
		private final int x;
		private final int y;
		private final int z;
		private String name;
		private Material icon;

		public Marker(int x, int y, int z, String name, Material icon)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.name = name;
			this.icon = icon;
		}

		public Marker(int x, int y, int z, String name)
		{
			this(x, y, z, name, Material.PAPER);
		}

		public Location toLocation(World world)
		{
			return new Location(world, x, y, z);
		}

		public double distance(Vector vector)
		{
			return Math.sqrt(NumberConversions.square(x - vector.getX()) + NumberConversions.square(y - vector.getY()) + NumberConversions.square(z - vector.getZ()));
		}

		public double distance(Marker marker)
		{
			return Math.sqrt(NumberConversions.square(x - marker.x()) + NumberConversions.square(y - marker.y()) + NumberConversions.square(z - marker.z()));
		}

		public int x() { return x; }
		public int y() { return y; }
		public int z() { return z; }
		public String name() { return name; }
		public Material icon() { return icon; }

		public void setIcon(Material icon) { this.icon = icon; }
		public void setName(String name) { this.name = name; }

		@Override
		public String toString()
		{
			return "Marker[" + "x=" + x + ", " + "y=" + y + ", " + "z=" + z + ", " + "name=" + name + ", " + "icon=" + icon + ']';
		}
	}

	@Override
	public Marker fromJson(JSONObject json)
	{
		if (json.optBoolean("null", false))
			return null;

		return new Marker(
			json.getInt("x"),
			json.getInt("y"),
			json.getInt("z"),
			json.optString("name", null),
			json.optEnum(Material.class, "icon", Material.PAPER)
		);
	}

	@Override
	public void toJson(Marker obj, JSONObject json)
	{
		if (obj == null)
		{
			json.put("null", true);
			return;
		}
		json.put("x", obj.x());
		json.put("y", obj.y());
		json.put("z", obj.z());
		if (obj.name != null)
			json.put("name", obj.name);
		json.put("icon", obj.icon);
	}

	public static ItemStack toItem(Marker marker)
	{
		if (marker == null)
			return MiscUtil.AIR;

		return MarkerItem.newMarker(marker);
	}

	public static Marker toMarker(ItemStack item)
	{
		if (item.getType().isAir())
			return null;

		ItemStackBuilder edit = ItemStackBuilder.edit(item);
		int x = edit.getCustomTagInt("x");
		int y = edit.getCustomTagInt("y");
		int z = edit.getCustomTagInt("z");
		String name = edit.getCustomTagString("name");

		return new Marker(x, y, z, name);
	}

	public static SlotBuilder slotBuilder(Marker current, Consumer<Marker> set)
	{
		return SlotBuilder
			.create(toItem(current))
			.allow(InventoryAction.PICKUP_ALL, InventoryAction.PLACE_ALL, InventoryAction.PICKUP_HALF)
			.allow(ClickType.LEFT, ClickType.RIGHT)
			.onClick((c, cm) ->
			{
				if (c.type() == ClickType.RIGHT)
				{
					ItemStack currentItem = toItem(current);
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

				set.accept(toMarker(c.itemOnCursor()));
				ItemStack clone = c.itemOnCursor().clone();
				clone.setAmount(1);
				c.slot().setItem(clone);
				return Response.cancel();
			});
	}
}
