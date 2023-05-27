package steve6472.funnylib.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Preconditions;
import steve6472.funnylib.util.SkullCreator;
import steve6472.standalone.Skins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 9/16/2022
 * Project: StevesFunnyLibrary <br>
 */
public class GenericHeadItem extends CustomItem
{
	private final String headUrl;
	private final String id;
	private final String name;

	private List<JSONMessage> lore;
	private boolean glow;
	private boolean unique;

	public GenericHeadItem(String headUrl, String id, String name)
	{
		this.headUrl = headUrl;
		this.id = id;
		this.name = name;
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	protected ItemStack item()
	{
		ItemStackBuilder itemStackBuilder = ItemStackBuilder.editNonStatic(SkullCreator.itemFromUrl(Skins.toUrl(headUrl)));
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

		if (unique)
		{
			itemStackBuilder.setString("uuid", UUID.randomUUID().toString());
		}

		return itemStackBuilder.setName(name, ChatColor.DARK_AQUA).buildItemStack();
	}

	public static Builder builder(String headUrl, String id)
	{
		return new Builder(headUrl, id);
	}

	public static class Builder
	{
		private final String headUrl;
		private final String id;

		JSONMessage name;
		List<JSONMessage> lore = new ArrayList<>();
		boolean glow;
		boolean unique;

		private Builder(String headUrl, String id)
		{
			this.headUrl = headUrl;
			this.id = id;
		}

		public Builder glow()
		{
			this.glow = true;
			return this;
		}

		public Builder unique()
		{
			this.unique = true;
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

		public GenericHeadItem build()
		{
			Preconditions.checkNotNull(name);
			GenericHeadItem genericItem = new GenericHeadItem(headUrl, id, name.toLegacy());
			genericItem.lore = lore;
			genericItem.glow = glow;
			genericItem.unique = unique;
			return genericItem;
		}
	}
}
