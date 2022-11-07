package steve6472.funnylib.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	private List<JSONMessage> lore;
	private boolean glow;

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
		if (lore != null && !lore.isEmpty())
		{
			for (JSONMessage jsonMessage : lore)
			{
				itemStackBuilder.addLore(jsonMessage);
			}
		}

		if (glow)
		{
			itemStackBuilder.glow();
		}

		return itemStackBuilder.setName(name, ChatColor.DARK_AQUA).buildItemStack();
	}

	public static Builder builder(Material material, String id)
	{
		return new Builder(material, id);
	}

	public static class Builder
	{
		private final Material material;
		private final String id;

		JSONMessage name;
		List<JSONMessage> lore = new ArrayList<>();
		boolean glow;
		int customModelData;

		private Builder(Material material, String id)
		{
			this.material = material;
			this.id = id;
		}

		public Builder glow()
		{
			this.glow = true;
			return this;
		}

		public Builder customModel(int data)
		{
			this.customModelData = data;
			return this;
		}

		public Builder name(JSONMessage name)
		{
			this.name = name;
			return this;
		}

		public Builder name(String legacy)
		{
			this.name = JSONMessage.create(legacy);
			return this;
		}

		public Builder addLore(JSONMessage line)
		{
			this.lore.add(line);
			return this;
		}

		public Builder addLore(String legacy)
		{
			this.lore.add(JSONMessage.create(legacy));
			return this;
		}

		public Builder addLore(JSONMessage[] lines)
		{
			Collections.addAll(lore, lines);
			return this;
		}

		public Builder addLore(String[] legacyLines)
		{
			for (String legacyLine : legacyLines)
			{
				addLore(legacyLine);
			}
			return this;
		}

		public GenericItem build()
		{
			Preconditions.checkNotNull(name);
			GenericItem genericItem = new GenericItem(id, material, name.toLegacy(), customModelData);
			genericItem.lore = lore;
			genericItem.glow = glow;
			return genericItem;
		}
	}
}
