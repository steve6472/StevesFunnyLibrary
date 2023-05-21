package steve6472.funnylib.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.joml.Vector3i;
import steve6472.funnylib.category.ICategorizable;
import steve6472.funnylib.item.builtin.MarkerItem;
import steve6472.funnylib.json.Itemizable;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;

/**
 * Created by steve6472
 * Date: 4/23/2023
 * Project: StevesFunnyLibrary <br>
 */
public final class Marker implements ICategorizable, Itemizable<Marker>
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
		ItemNBT itemNBT = ItemNBT.create(item);

		String name = itemNBT.getString("name", null);
		String icon = itemNBT.getString("icon", null);
		Vector3i location1 = itemNBT.get3i("location");

		return new Marker(location1.x, location1.y, location1.z, name, Material.valueOf(icon));
	}

	@Override
	public void toNBT(NBT compound)
	{
		compound.set3i("location", location);
		compound.setEnum("icon", icon);
		if (name != null)
			compound.setString("name", name);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		compound.get3i("location", location);
		this.setName(compound.getString("name", null));
		this.setIcon(compound.getEnum(Material.class, "icon", Material.BOOK));
	}
}
