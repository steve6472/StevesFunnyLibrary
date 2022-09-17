package steve6472.funnylib.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.util.ItemStackBuilder;

/**
 * Created by steve6472
 * Date: 9/16/2022
 * Project: StevesFunnyLibrary <br>
 */
public class GenericItem extends CustomItem
{
	private final String id;
	private final String name;
	private final Material material;
	private final int customModelId;

	public GenericItem(String id, Material material, String name, int customModelId)
	{
		this.id = id;
		this.material = material;
		this.name = name;
		this.customModelId = customModelId;
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	protected ItemStack item()
	{
		ItemStackBuilder itemStackBuilder = ItemStackBuilder.create(material);
		if (customModelId != 0)
			itemStackBuilder.setCustomModelData(customModelId);
		return itemStackBuilder.setName(name, ChatColor.DARK_AQUA).buildItemStack();
	}
}
