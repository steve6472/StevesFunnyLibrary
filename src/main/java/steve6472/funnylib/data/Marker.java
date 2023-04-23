package steve6472.funnylib.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.joml.Vector3i;
import org.json.JSONObject;
import steve6472.funnylib.category.ICategorizable;
import steve6472.funnylib.item.builtin.MarkerItem;
import steve6472.funnylib.json.INBT;
import steve6472.funnylib.json.Itemizable;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.NBT;

/**
 * Created by steve6472
 * Date: 4/23/2023
 * Project: StevesFunnyLibrary <br>
 */
public final class Marker implements ICategorizable, INBT, Itemizable<Marker>
{
	private final Vector3i location;
	private String name;
	private Material icon;

	public Marker(int x, int y, int z, String name, Material icon)
	{
		this.location = new Vector3i(x, y, z);
		this.name = name;
		this.icon = icon;
	}

	public Marker(int x, int y, int z, String name)
	{
		this(x, y, z, name, Material.PAPER);
	}

	public Location toLocation(World world)
	{
		return new Location(world, location.x, location.y, location.z);
	}

	public double distance(Vector vector)
	{
		return Math.sqrt(NumberConversions.square(location.x - vector.getX()) + NumberConversions.square(location.y - vector.getY()) + NumberConversions.square(location.z - vector.getZ()));
	}

	public double distance(Marker marker)
	{
		return Math.sqrt(NumberConversions.square(location.x - marker.x()) + NumberConversions.square(location.y - marker.y()) + NumberConversions.square(location.z - marker.z()));
	}

	public int x()
	{
		return location.x;
	}

	public int y()
	{
		return location.y;
	}

	public int z()
	{
		return location.z;
	}

	public String name()
	{
		return name;
	}

	public Material icon()
	{
		return icon;
	}

	public void setIcon(Material icon)
	{
		this.icon = icon;
	}

	@Override
	public String id()
	{
		return "marker";
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return "Marker[" + "x=" + location.x + ", " + "y=" + location.y + ", " + "z=" + location.z + ", " + "name=" + name + ", " + "icon=" + icon + ']';
	}

	public ItemStack toItem()
	{
		return MarkerItem.newMarker(this);
	}

	public static Marker fromItem(ItemStack item)
	{
		if (item.getType().isAir())
			return null;

		ItemStackBuilder edit = ItemStackBuilder.edit(item);
		int x = edit.getInt("x");
		int y = edit.getInt("y");
		int z = edit.getInt("z");
		String name = edit.nbt().getString("name", null);
		String icon = edit.nbt().getString("icon", null);

		return new Marker(x, y, z, name, Material.valueOf(icon));
	}

	@Override
	public void fromNBT(NBT compound)
	{
		compound.set3i("location", location);
		compound.setEnum("icon", icon);
		if (name != null)
			compound.setString("name", name);
	}

	@Override
	public void toNBT(NBT compound)
	{
		compound.get3i("location", location);
		this.setName(compound.getString("name", null));
		this.setIcon(compound.getEnum(Material.class, "icon", Material.BOOK));
	}

	@Override
	public void toJSON(JSONObject json)
	{
		json.put("x", x());
		json.put("y", y());
		json.put("z", z());
		if (name != null)
			json.put("name", name);
		json.put("icon", icon);
	}

	@Override
	public void fromJSON(JSONObject json)
	{
		location.set(json.getInt("x"), json.getInt("y"), json.getInt("z"));
		name = json.optString("name", null);
		icon = json.optEnum(Material.class, "icon", Material.PAPER);
	}
}
