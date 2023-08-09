package steve6472.funnylib.category;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 4/22/2023
 * Project: StevesFunnyLibrary <br>
 */
class Folder implements Categorizable
{
	private String name;
	private Material icon;

	private final GenericStorage nestedStorage;

	public Folder(GenericStorage nestedStorage)
	{
		name = "Folder";
		icon = Material.CHEST;
		this.nestedStorage = nestedStorage;
	}

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public Material icon()
	{
		return icon;
	}

	@Override
	public void setIcon(Material icon)
	{
		this.icon = icon;
	}

	@Override
	public String id()
	{
		return "folder";
	}

	public ItemStack createIcon()
	{
		return ItemStackBuilder
			.create(icon)
			.setName(JSONMessage.create(name).color("#FFA500"))
			.addLore(JSONMessage.create("Items inside folder: ").color(ChatColor.DARK_GRAY).then("" + nestedStorage.getItemList().size(), ChatColor.WHITE).setItalic(JSONMessage.ItalicType.FALSE))
			.buildItemStack();
	}

	public GenericStorage getNestedStorage()
	{
		return nestedStorage;
	}

	public void setPrevious(GenericStorage previous)
	{
		nestedStorage.setPrevious(previous);
	}

	@Override
	public void toNBT(NBT compound)
	{
		NBT nestedNbt = compound.createCompound();
		nestedStorage.save(nestedNbt);

		compound.setCompound("nested_storage", nestedNbt);
		compound.setString("name", name);
		compound.setEnum("icon", icon);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		NBT nestedNbt = compound.getCompound("nested_storage");

		name = compound.getString("name");
		icon = compound.getEnum(Material.class, "icon");

		nestedStorage.load(nestedNbt);
	}
}
