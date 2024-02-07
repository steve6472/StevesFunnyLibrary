package steve6472.funnylib.serialize;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import steve6472.funnylib.util.Preconditions;

/**
 * Created by steve6472
 * Date: 4/23/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ItemNBT extends NBT
{
	private ItemStack itemStack;
	private ItemMeta meta;
	private NBT protectedData;

	public ItemStack getItemStack()
	{
		return itemStack;
	}

	public ItemMeta getMeta()
	{
		return meta;
	}

	public void change(ItemStack itemStack, ItemMeta meta)
	{
		this.itemStack = itemStack;
		this.meta = meta;
		Preconditions.checkNotNull(meta, "Tried to manipulate NBT on air items");
		this.container = meta.getPersistentDataContainer();
	}

	public static ItemNBT create(ItemStack itemStack)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		Preconditions.checkNotNull(itemMeta, "Tried to manipulate NBT on air items");

		ItemNBT nbt = new ItemNBT();
		nbt.container = itemMeta.getPersistentDataContainer();
		nbt.itemStack = itemStack;
		nbt.meta = itemMeta;
		return nbt;
	}

	public static ItemNBT create(ItemStack itemStack, ItemMeta meta)
	{
		Preconditions.checkNotNull(meta, "Tried to manipulate NBT on air items");

		ItemNBT nbt = new ItemNBT();
		nbt.container = meta.getPersistentDataContainer();
		nbt.itemStack = itemStack;
		nbt.meta = meta;
		return nbt;
	}

	public PdcNBT protectedData()
	{
		// Lazy get _protected compound or create
		if (protectedData == null)
			protectedData = getOrCreateCompound("_protected");

		return PdcNBT.fromPDC(protectedData.getContainer());
	}

	public boolean hasCustomId()
	{
		return hasString("custom_id");
	}

	public String getCustomId()
	{
		return getString("custom_id");
	}

	public void setCustomId(String id)
	{
		setString("custom_id", id);
	}

	public void save()
	{
		if (protectedData != null)
			setCompound("_protected", protectedData);
		itemStack.setItemMeta(meta);
	}
}
