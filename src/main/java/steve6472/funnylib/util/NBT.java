package steve6472.funnylib.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.joml.Vector3i;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;

/**
 * Created by steve6472
 * Date: 4/8/2023
 * Project: StevesFunnyLibrary <br>
 */
public class NBT
{
	private PersistentDataContainer container;
	private ItemStack itemStack;
	private ItemMeta meta;

	private NBT()
	{
	}

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

	/**
	 * Used for nested compounds
	 */
	private static NBT create(PersistentDataContainer container)
	{
		NBT nbt = new NBT();
		nbt.container = container;
		return nbt;
	}

	public static NBT newCompound(PersistentDataContainer persistentDataContainer)
	{
		return NBT.create(persistentDataContainer.getAdapterContext().newPersistentDataContainer());
	}

	public static NBT create(ItemStack itemStack)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		Preconditions.checkNotNull(itemMeta, "Tried to manipulate NBT on air items");

		NBT nbt = new NBT();
		nbt.container = itemMeta.getPersistentDataContainer();
		nbt.itemStack = itemStack;
		nbt.meta = itemMeta;
		return nbt;
	}

	public static NBT create(ItemStack itemStack, ItemMeta meta)
	{
		Preconditions.checkNotNull(meta, "Tried to manipulate NBT on air items");

		NBT nbt = new NBT();
		nbt.container = meta.getPersistentDataContainer();
		nbt.itemStack = itemStack;
		nbt.meta = meta;
		return nbt;
	}

	private static NamespacedKey newKey(String key)
	{
		return new NamespacedKey(FunnyLib.getPlugin(), key);
	}

	public ItemStack save()
	{
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public void remove(String key)
	{
		container.remove(newKey(key));
	}

	/*
	 * Long
	 */

	public long getLong(String key)
	{
		Long aLong = container.get(newKey(key), PersistentDataType.LONG);
		Preconditions.checkNotNull(aLong);
		return aLong;
	}

	public long getLong(String key, long defaultValue)
	{
		Long aLong = container.get(newKey(key), PersistentDataType.LONG);
		if (aLong == null)
			return defaultValue;
		else
			return aLong;
	}

	public void setLong(String key, long value)
	{
		container.set(newKey(key), PersistentDataType.LONG, value);
	}

	public boolean hasLong(String key)
	{
		return container.has(newKey(key), PersistentDataType.LONG);
	}

	/*
	 * Integer
	 */

	public int getInt(String key)
	{
		Integer integer = container.get(newKey(key), PersistentDataType.INTEGER);
		Preconditions.checkNotNull(integer);
		return integer;
	}

	public int getInt(String key, int defaultValue)
	{
		Integer integer = container.get(newKey(key), PersistentDataType.INTEGER);
		if (integer == null)
			return defaultValue;
		else
			return integer;
	}

	public void setInt(String key, int value)
	{
		container.set(newKey(key), PersistentDataType.INTEGER, value);
	}

	public boolean hasInt(String key)
	{
		return container.has(newKey(key), PersistentDataType.INTEGER);
	}

	/*
	 * Byte
	 */

	public byte getByte(String key)
	{
		Byte aByte = container.get(newKey(key), PersistentDataType.BYTE);
		Preconditions.checkNotNull(aByte);
		return aByte;
	}

	public byte getByte(String key, byte defaultValue)
	{
		Byte aByte = container.get(newKey(key), PersistentDataType.BYTE);
		if (aByte == null)
			return defaultValue;
		else
			return aByte;
	}

	public void setByte(String key, byte value)
	{
		container.set(newKey(key), PersistentDataType.BYTE, value);
	}

	public boolean hasByte(String key)
	{
		return container.has(newKey(key), PersistentDataType.BYTE);
	}

	/*
	 * String
	 */

	public String getString(String key)
	{
		String string = container.get(newKey(key), PersistentDataType.STRING);
		Preconditions.checkNotNull(string, "No String value found for key '" + key + "'");
		return string;
	}

	public String getString(String key, String defaultValue)
	{
		String string = container.get(newKey(key), PersistentDataType.STRING);
		if (string == null)
			return defaultValue;
		else
			return string;
	}

	public void setString(String key, String value)
	{
		container.set(newKey(key), PersistentDataType.STRING, value);
	}

	public boolean hasString(String key)
	{
		return container.has(newKey(key), PersistentDataType.STRING);
	}

	/*
	 * Enum (string but stfu)
	 */

	public <T extends Enum<T>> T getEnum(Class<T> clazz, String key)
	{
		String string = container.get(newKey(key), PersistentDataType.STRING);
		Preconditions.checkNotNull(string, "No Enum value found for key '" + key + "'");
		return Enum.valueOf(clazz, string);
	}

	public <T extends Enum<T>> T getEnum(Class<T> clazz, String key, T defaultValue)
	{
		String string = container.get(newKey(key), PersistentDataType.STRING);
		if (string == null)
			return defaultValue;
		else
			return Enum.valueOf(clazz, string);
	}

	public void setEnum(String key, Enum<?> value)
	{
		container.set(newKey(key), PersistentDataType.STRING, value.name());
	}

	public boolean hasEnum(String key)
	{
		return container.has(newKey(key), PersistentDataType.STRING);
	}

	/*
	 * Int Array
	 */

	public int[] getIntArray(String key)
	{
		int[] ints = container.get(newKey(key), PersistentDataType.INTEGER_ARRAY);
		Preconditions.checkNotNull(ints);
		return ints;
	}

	public int[] getIntArray(String key, int[] defaultValue)
	{
		int[] ints = container.get(newKey(key), PersistentDataType.INTEGER_ARRAY);
		if (ints == null)
			return defaultValue;
		else
			return ints;
	}

	public void setIntArray(String key, int[] value)
	{
		container.set(newKey(key), PersistentDataType.INTEGER_ARRAY, value);
	}

	public boolean hasIntArray(String key)
	{
		return container.has(newKey(key), PersistentDataType.INTEGER_ARRAY);
	}

	/*
	 * Long Array
	 */

	public long[] getLongArray(String key)
	{
		long[] longs = container.get(newKey(key), PersistentDataType.LONG_ARRAY);
		Preconditions.checkNotNull(longs);
		return longs;
	}

	public long[] getLongArray(String key, long[] defaultValue)
	{
		long[] longs = container.get(newKey(key), PersistentDataType.LONG_ARRAY);
		if (longs == null)
			return defaultValue;
		else
			return longs;
	}

	public void setLongArray(String key, long[] value)
	{
		container.set(newKey(key), PersistentDataType.LONG_ARRAY, value);
	}

	public boolean hasLongArray(String key)
	{
		return container.has(newKey(key), PersistentDataType.LONG_ARRAY);
	}

	/*
	 * Compound
	 */

	public NBT createCompound()
	{
		PersistentDataContainer newCompound = container.getAdapterContext().newPersistentDataContainer();
		return NBT.create(newCompound);
	}

	public NBT getCompound(String key)
	{
		PersistentDataContainer compound = container.get(newKey(key), PersistentDataType.TAG_CONTAINER);
		Preconditions.checkNotNull(compound);
		return NBT.create(compound);
	}

	public void setCompound(String key, PersistentDataContainer container)
	{
		this.container.set(newKey(key), PersistentDataType.TAG_CONTAINER, container);
	}

	public void setCompound(String key, NBT compound)
	{
		setCompound(key, compound.container);
	}

	public boolean hasCompound(String key)
	{
		return container.has(newKey(key), PersistentDataType.TAG_CONTAINER);
	}

	/*
	 * Compound Array
	 */

	public NBT[] createCompoundArray(int len)
	{
		NBT[] nbtArray = new NBT[len];
		for (int i = 0; i < len; i++)
		{
			nbtArray[i] = NBT.create(container.getAdapterContext().newPersistentDataContainer());
		}

		return nbtArray;
	}

	public void setCompoundArray(String key, NBT[] compoundArray)
	{
		PersistentDataContainer[] array = new PersistentDataContainer[compoundArray.length];
		for (int i = 0; i < array.length; i++)
		{
			array[i] = compoundArray[i].container;
		}

		container.set(newKey(key), PersistentDataType.TAG_CONTAINER_ARRAY, array);
	}

	public boolean hasCompoundArray(String key)
	{
		return container.has(newKey(key), PersistentDataType.TAG_CONTAINER_ARRAY);
	}

	/*
	 * Item
	 */

	public ItemStack getItemStack(String key)
	{
		return MiscUtil.deserializeItemStack(new JSONObject(getString(key)));
	}

	public void setItemStack(String key, ItemStack value)
	{
		JSONObject jsonObject = MiscUtil.serializeItemStack(value);
		setString(key, jsonObject.toString());
	}

	/*
	 * Fancy compounds
	 */

	public void set3i(String key, Vector3i vec)
	{
		set3i(key, vec.x, vec.y, vec.z);
	}

	public void set3i(String key, int x, int y, int z)
	{
		NBT compound = createCompound();
		compound.setInt("x", x);
		compound.setInt("y", y);
		compound.setInt("z", z);
		setCompound(key, compound);
	}

	public Vector3i get3i(String key)
	{
		return get3i(key, new Vector3i());
	}

	public Vector3i get3i(String key, Vector3i store)
	{
		NBT compound = getCompound(key);
		return store.set(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
	}

	public boolean has3i(String key)
	{
		return hasCompound(key);
	}
}
