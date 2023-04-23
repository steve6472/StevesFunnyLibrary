package steve6472.funnylib.category;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 4/22/2023
 * Project: StevesFunnyLibrary <br>
 */
class Folder implements ICategorizable
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
	public void toJSON(JSONObject json)
	{
		JSONObject nestedJson = new JSONObject();
		nestedStorage.save(nestedJson);

		json.put("nested_storage", nestedJson);
		json.put("name", name);
		json.put("icon", icon.name());
	}

	@Override
	public void fromJSON(JSONObject json)
	{
		JSONObject nestedJson = json.getJSONObject("nested_storage");

		name = json.getString("name");
		icon = Material.valueOf(json.getString("icon"));

		nestedStorage.load(nestedJson);
	}
}
