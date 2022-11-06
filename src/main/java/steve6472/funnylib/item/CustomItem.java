package steve6472.funnylib.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONObject;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerEntityContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.json.codec.Codec;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JsonDataType;
import steve6472.funnylib.util.Preconditions;

/**********************
 * Created by steve6472
 * On date: 4/6/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public abstract class CustomItem
{
	public abstract String id();

	protected abstract ItemStack item();

	public ItemStack newItemStack()
	{
		ItemStack itemStack = ItemStackBuilder.editNonStatic(item()).setCustomId(id()).buildItemStack();
		saveItemData(itemStack, createData());
		return itemStack;
	}

	public ItemData createData()
	{
		return new ItemData();
	}

	/*
	 * Events
	 */

	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result) {}
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result) {}
	public void useOnEntity(PlayerEntityContext context, CancellableResult result) {}

	@Override
	public String toString()
	{
		return "CustomItem{id='" + id() + "', item=" + item().toString() + "}";
	}

	/*
	 * Item Data saving/loading
	 */

	public static void saveItemData(ItemStack itemStack, ItemData itemData)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		Preconditions.checkNotNull(itemMeta, "ItemMeta is null (ItemStack is AIR)");
		JSONObject value = Codec.saveAll(itemData);
		NamespacedKey namespacedKey = new NamespacedKey(FunnyLib.getPlugin(), "item_data");
		if (value.isEmpty())
		{
			itemMeta.getPersistentDataContainer().remove(namespacedKey);
		} else
		{
			itemMeta
				.getPersistentDataContainer()
				.set(namespacedKey, JsonDataType.JSON, value);
		}
		itemStack.setItemMeta(itemMeta);
	}

	public static ItemData loadItemData(ItemStack itemStack)
	{
		CustomItem customItem = Items.getCustomItem(itemStack);
		Preconditions.checkNotNull(customItem, "ItemStack is not of CustomItem");
		return loadItemData(itemStack, customItem);
	}

	public static ItemData loadItemData(ItemStack itemStack, CustomItem customItem)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		Preconditions.checkNotNull(itemMeta);
		JSONObject itemData = itemMeta
			.getPersistentDataContainer()
			.get(new NamespacedKey(FunnyLib.getPlugin(), "item_data"), JsonDataType.JSON);
		return Codec.loadAll(customItem.createData(), itemData);
	}
}
