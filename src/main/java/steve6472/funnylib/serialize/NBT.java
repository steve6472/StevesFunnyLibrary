package steve6472.funnylib.serialize;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.util.MiscUtil;
import steve6472.funnylib.util.Preconditions;

import java.util.Locale;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 4/8/2023
 * Project: StevesFunnyLibrary <br>
 */
public class NBT
{
	protected PersistentDataContainer container;

	protected NBT()
	{
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

	public PersistentDataContainer getContainer()
	{
		return container;
	}

	public void save()
	{

	}

	private static NamespacedKey newKey(String key)
	{
		if (key.contains(":"))
		{
			if (key.startsWith(FunnyLib.getPlugin().getName().toLowerCase(Locale.ROOT) + ":"))
			{
				return new NamespacedKey(FunnyLib.getPlugin(), key.split(":")[1]);
			} else
			{
				throw new RuntimeException("Can not create NamespacedKey from '" + key + "'");
			}
		} else
		{
			return new NamespacedKey(FunnyLib.getPlugin(), key);
		}
	}

	public void remove(String key)
	{
		container.remove(newKey(key));
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
	 * Boolean
	 */

	public boolean getBoolean(String key)
	{
		Byte aByte = container.get(newKey(key), PersistentDataType.BYTE);
		Preconditions.checkNotNull(aByte);
		return aByte == 1;
	}

	public boolean getBoolean(String key, boolean defaultValue)
	{
		Byte aByte = container.get(newKey(key), PersistentDataType.BYTE);
		if (aByte == null)
			return defaultValue;
		else
			return aByte == 1;
	}

	public void setBoolean(String key, boolean value)
	{
		container.set(newKey(key), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
	}

	public boolean hasBoolean(String key)
	{
		return container.has(newKey(key), PersistentDataType.BYTE);
	}

	/*
	 * Short
	 */

	public short getShort(String key) {
		Short aShort = container.get(newKey(key), PersistentDataType.SHORT);
		Preconditions.checkNotNull(aShort);
		return aShort;
	}

	public short getShort(String key, short defaultValue) {
		Short aShort = container.get(newKey(key), PersistentDataType.SHORT);
		if (aShort == null)
			return defaultValue;
		else
			return aShort;
	}

	public void setShort(String key, short value) {
		container.set(newKey(key), PersistentDataType.SHORT, value);
	}

	public boolean hasShort(String key) {
		return container.has(newKey(key), PersistentDataType.SHORT);
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
	 * Float
	 */

	public float getFloat(String key)
	{
		Float aFloat = container.get(newKey(key), PersistentDataType.FLOAT);
		Preconditions.checkNotNull(aFloat);
		return aFloat;
	}

	public float getFloat(String key, float defaultValue)
	{
		Float aFloat = container.get(newKey(key), PersistentDataType.FLOAT);
		if (aFloat == null)
			return defaultValue;
		else
			return aFloat;
	}

	public void setFloat(String key, float value)
	{
		container.set(newKey(key), PersistentDataType.FLOAT, value);
	}

	public boolean hasFloat(String key)
	{
		return container.has(newKey(key), PersistentDataType.FLOAT);
	}

	/*
	 * Double
	 */

	public double getDouble(String key)
	{
		Double aDouble = container.get(newKey(key), PersistentDataType.DOUBLE);
		Preconditions.checkNotNull(aDouble);
		return aDouble;
	}

	public double getDouble(String key, double defaultValue)
	{
		Double aDouble = container.get(newKey(key), PersistentDataType.DOUBLE);
		if (aDouble == null)
			return defaultValue;
		else
			return aDouble;
	}

	public void setDouble(String key, double value)
	{
		container.set(newKey(key), PersistentDataType.DOUBLE, value);
	}

	public boolean hasDouble(String key)
	{
		return container.has(newKey(key), PersistentDataType.DOUBLE);
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
	 * Byte Array
	 */

	public byte[] getByteArray(String key)
	{
		byte[] bytes = container.get(newKey(key), PersistentDataType.BYTE_ARRAY);
		Preconditions.checkNotNull(bytes);
		return bytes;
	}

	public byte[] getByteArray(String key, byte[] defaultValue)
	{
		byte[] bytes = container.get(newKey(key), PersistentDataType.BYTE_ARRAY);
		if (bytes == null)
			return defaultValue;
		else
			return bytes;
	}

	public void setByteArray(String key, byte[] value)
	{
		container.set(newKey(key), PersistentDataType.BYTE_ARRAY, value);
	}

	public boolean hasByteArray(String key)
	{
		return container.has(newKey(key), PersistentDataType.BYTE_ARRAY);
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
		Preconditions.checkNotNull(compound, "Compound '" + key + "' not found in tag " + MiscUtil.dataToString(container));
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

	public void setCompoundArray(String key, @NotNull NBT[] compoundArray)
	{
		PersistentDataContainer[] array = new PersistentDataContainer[compoundArray.length];
		for (int i = 0; i < array.length; i++)
		{
			array[i] = compoundArray[i].container;
		}

		container.set(newKey(key), PersistentDataType.TAG_CONTAINER_ARRAY, array);
	}

	public NBT[] getCompoundArray(String key)
	{
		PersistentDataContainer[] persistentDataContainers = container.get(newKey(key), PersistentDataType.TAG_CONTAINER_ARRAY);
		Preconditions.checkNotNull(persistentDataContainers);
		NBT[] nbtArray = new NBT[persistentDataContainers.length];
		for (int i = 0; i < persistentDataContainers.length; i++)
		{
			nbtArray[i] = NBT.create(persistentDataContainers[i]);
		}
		return nbtArray;
	}

	public boolean hasCompoundArray(String key)
	{
		return container.has(newKey(key), PersistentDataType.TAG_CONTAINER_ARRAY);
	}

	/*
	 * Enum (string but stfu)
	 */

	public <T extends Enum<T>> T getEnum(@NotNull Class<T> clazz, String key)
	{
		String string = container.get(newKey(key), PersistentDataType.STRING);
		Preconditions.checkNotNull(string, "No Enum value found for key '" + key + "'");
		return Enum.valueOf(clazz, string);
	}

	public <T extends Enum<T>> T getEnum(@NotNull Class<T> clazz, String key, @Nullable T defaultValue)
	{
		String string = container.get(newKey(key), PersistentDataType.STRING);
		if (string == null)
			return defaultValue;
		else
			return Enum.valueOf(clazz, string);
	}

	public void setEnum(String key, @NotNull Enum<?> value)
	{
		container.set(newKey(key), PersistentDataType.STRING, value.name());
	}

	public boolean hasEnum(String key)
	{
		return container.has(newKey(key), PersistentDataType.STRING);
	}

	/*
	 * Item
	 */

	public ItemStack getItemStack(String key)
	{
		return MiscUtil.deserializeItemStack(new JSONObject(getString(key)));
	}

	public void setItemStack(String key, @NotNull ItemStack value)
	{
		JSONObject jsonObject = MiscUtil.serializeItemStack(value);
		setString(key, jsonObject.toString());
	}

	/*
	 * Fancy compounds
	 */

	/*
	 * 3i
	 */

	public void set3i(String key, @NotNull Vector3i vec)
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

	public Vector3i get3i(String key, @NotNull Vector3i store)
	{
		NBT compound = getCompound(key);
		return store.set(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
	}

	public boolean has3i(String key)
	{
		return hasCompound(key);
	}

	/*
	 * 3d
	 */

	public void set3d(String key, @NotNull Vector3d vec)
	{
		set3d(key, vec.x, vec.y, vec.z);
	}

	public void set3d(String key, double x, double y, double z)
	{
		NBT compound = createCompound();
		compound.setDouble("x", x);
		compound.setDouble("y", y);
		compound.setDouble("z", z);
		setCompound(key, compound);
	}

	public Vector3d get3d(String key)
	{
		return get3d(key, new Vector3d());
	}

	public Vector3d get3d(String key, @NotNull Vector3d store)
	{
		NBT compound = getCompound(key);
		return store.set(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"));
	}

	public boolean has3d(String key)
	{
		return hasCompound(key);
	}

	/*
	 * UUID
	 */

	public void setUUID(String key, @NotNull UUID uuid)
	{
		setString(key, uuid.toString());
	}

	public boolean hasUUID(String key)
	{
		return hasString(key);
	}

	public UUID getUUID(String key)
	{
		return UUID.fromString(getString(key));
	}

	public UUID getUUID(String key, @Nullable UUID defaultValue)
	{
		if (!hasUUID(key))
			return defaultValue;
		else
			return getUUID(key);
	}

	/*
	 * Location
	 */

	public void setLocation(String key, @NotNull Location location)
	{
		NBT compound = createCompound();

		if (location.getWorld() != null)
			compound.setUUID("world", location.getWorld().getUID());
		compound.set3d("pos", location.getX(), location.getY(), location.getZ());
		compound.setFloat("yaw", location.getYaw());
		compound.setFloat("pitch", location.getPitch());

		setCompound(key, compound);
	}

	public boolean hasLocation(String key)
	{
		return hasCompound(key);
	}

	public Location getLocation(String key)
	{
		NBT compound = getCompound(key);
		UUID worldUUID = compound.getUUID("world", null);
		World world = worldUUID == null ? null : Bukkit.getWorld(worldUUID);
		Vector3d pos = compound.get3d("pos");
		float yaw = compound.getFloat("yaw");
		float pitch = compound.getFloat("pitch");

		return new Location(world, pos.x, pos.y, pos.z, yaw, pitch);
	}

	public Location getLocation(String key, @Nullable Location defaultValue)
	{
		if (hasLong(key))
			return getLocation(key);
		else
			return defaultValue;
	}
}
