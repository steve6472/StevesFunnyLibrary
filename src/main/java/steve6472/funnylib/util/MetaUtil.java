package steve6472.funnylib.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import steve6472.funnylib.FunnyLib;

import java.util.List;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary <br>
 * Works only for working with meta set using the current plugin
 */
public class MetaUtil
{
	public static boolean hasMeta(Metadatable metadatable, String key)
	{
		List<MetadataValue> metadata = metadatable.getMetadata(key);
		for (MetadataValue metadatum : metadata)
		{
			if (metadatum.getOwningPlugin() == FunnyLib.getPlugin())
			{
				return true;
			}
		}

		return false;
	}

	public static <T> MutableMetadataValue<T> getMeta(Metadatable metadatable, Class<T> expectedType, String key)
	{
		List<MetadataValue> metadata = metadatable.getMetadata(key);

		for (MetadataValue metadatum : metadata)
		{
			if (metadatum.getOwningPlugin() == FunnyLib.getPlugin() && metadatum instanceof MutableMetadataValue<?> v)
			{
				Object value = v.value();
				if (value == null)
					return null;
				if (expectedType.isAssignableFrom(value.getClass()))
					return (MutableMetadataValue<T>) v;
			}
		}

		return null;
	}

	public static <T> T getValue(Metadatable metadatable, Class<T> expectedType, String key)
	{
		List<MetadataValue> metadata = metadatable.getMetadata(key);

		for (MetadataValue metadatum : metadata)
		{
			if (metadatum.getOwningPlugin() == FunnyLib.getPlugin() && metadatum instanceof MutableMetadataValue<?> v)
			{
				Object value = v.value();
				if (value == null)
				{
					return null;
				}
				if (expectedType.isAssignableFrom(value.getClass()))
				{
					return (T) value;
				}
			}
		}
		return null;
	}

	public static <T> void setMeta(Metadatable metadatable, String key, T o)
	{
		metadatable.setMetadata(key, new MutableMetadataValue<>(o));
	}

	public static void removeMeta(Metadatable metadatable, String key)
	{
		metadatable.removeMetadata(key, FunnyLib.getPlugin());
	}
}
