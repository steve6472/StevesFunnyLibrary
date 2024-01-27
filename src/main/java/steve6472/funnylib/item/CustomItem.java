package steve6472.funnylib.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerEntityContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.util.ItemStackBuilder;

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
		ItemNBT nbt = ItemNBT.create(itemStack);
		initCustomData(nbt);
		nbt.save();
		return itemStack;
	}

	/**
	 * Set default data for item
	 * @param nbt nbt wrapper
	 */
	protected void initCustomData(ItemNBT nbt)
	{

	}

	/*
	 * Events
	 */

	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result) {}
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result) {}
	public void useOnEntity(PlayerEntityContext context, CancellableResult result) {}

	public void onDrop(PlayerItemContext context, CancellableResult result) {}

	@Override
	public String toString()
	{
		return "CustomItem{id='" + id() + "', item=" + item().toString() + "}";
	}
}
