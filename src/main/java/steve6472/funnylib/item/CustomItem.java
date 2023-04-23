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
import steve6472.funnylib.util.NBT;
import steve6472.funnylib.util.Preconditions;

/**********************
 * Created by steve6472
 * On date: 4/6/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public abstract class CustomItem
{
	private static final NamespacedKey ITEM_DATA_KEY = new NamespacedKey(FunnyLib.getPlugin(), "item_data");

	public abstract String id();

	protected abstract ItemStack item();

	public ItemStack newItemStack()
	{
		ItemStack itemStack = ItemStackBuilder.editNonStatic(item()).setCustomId(id()).buildItemStack();
		NBT nbt = NBT.create(itemStack);
		initCustomData(nbt);
		nbt.save();
		return itemStack;
	}

	/**
	 * Set default data for item
	 * @param nbt nbt wrapper
	 */
	protected void initCustomData(NBT nbt)
	{

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
}
