package steve6472.funnylib.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.category.ICategorizable;
import steve6472.funnylib.item.builtin.AreaMarkerItem;
import steve6472.funnylib.json.Itemizable;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;

/**
 * Created by steve6472
 * Date: 4/23/2023
 * Project: StevesFunnyLibrary <br>
 */
public final class AreaSelection implements ICategorizable, Itemizable<AreaSelection>
{
	private final Vector3i start, end;
	private String name;
	private Material icon;

	public AreaSelection(@NotNull Vector3i start, @NotNull Vector3i end, @Nullable String name, @NotNull Material icon)
	{
		this.start = start;
		this.end = end;
		this.name = name;
		this.icon = icon;
	}

	public AreaSelection(@NotNull Vector3i start, @NotNull Vector3i end, @Nullable String name)
	{
		this(start, end, name, Material.PAPER);
	}

	public AreaSelection()
	{
		this(new Vector3i(), new Vector3i(), null, Material.PAPER);
	}

	public AreaSelection(NBT nbt)
	{
		this(new Vector3i(), new Vector3i(), null, Material.PAPER);
		fromNBT(nbt);
	}

	public Vector3i getStart()
	{
		return start;
	}
	public Vector3i getEnd()
	{
		return end;
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

	public BoundingBox toBoundingBox()
	{
		return new BoundingBox(start.x, start.y, start.z, end.x + 1, end.y + 1, end.z + 1);
	}

	@Override
	public String id()
	{
		return "area_selection";
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ItemStack toItem()
	{
		ItemNBT nbt = ItemNBT.create(FunnyLib.AREA_LOCATION_MARKER.newItemStack());
		toNBT(nbt);
		AreaMarkerItem.updateLore(nbt);
		nbt.save();
		return nbt.getItemStack();
	}

	public static AreaSelection fromItem(ItemStack item)
	{
		if (item.getType().isAir())
			return null;

		ItemNBT nbt = ItemNBT.create(item);
		AreaSelection areaSelection = new AreaSelection(new Vector3i(), new Vector3i(), null, Material.PAPER);
		areaSelection.fromNBT(nbt);
		return areaSelection;
	}

	@Override
	public void toNBT(NBT compound)
	{
		compound.set3i("start", start);
		compound.set3i("end", end);
		compound.setEnum("icon", icon);
		if (name != null)
			compound.setString("name", name);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		compound.get3i("start", start);
		compound.get3i("end", end);
		this.setName(compound.getString("name", null));
		this.setIcon(compound.getEnum(Material.class, "icon", Material.PAPER));
	}
}
