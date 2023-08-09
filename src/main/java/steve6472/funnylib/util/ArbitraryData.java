package steve6472.funnylib.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ArbitraryData
{
	private final Map<String, Datum> dataMap = new HashMap<>();

	public <T> void setData(String key, T data)
	{
		Datum datum = dataMap.get(key);
		if (datum != null)
		{
			if (data != null && !datum.type.isAssignableFrom(data.getClass()))
			{
				throw new RuntimeException("Tried to set '" + key + "' to type " + data.getClass().getSimpleName() + ", but " + datum.type.getSimpleName() + " is already present");
			} else
			{
				datum.datum = data;
				dataMap.put(key, datum);
			}
		} else
		{
			datum = new Datum();
			datum.type = data.getClass();
			datum.datum = data;
			dataMap.put(key, datum);
		}
	}

	public <T> T getData(String key, Class<T> expectedType)
	{
		Datum datum = dataMap.get(key);
		if (datum == null)
			return null;

		if (!expectedType.isAssignableFrom(datum.type))
		{
			throw new RuntimeException("Data type '" + expectedType.getSimpleName() + "' expected, got '" + datum.type.getSimpleName() + "'");
		} else
		{
			return (T) datum.datum;
		}
	}

	public Object getData(String key)
	{
		Datum datum = dataMap.get(key);
		if (datum == null)
			return null;

		return datum.datum;
	}

	public void removeData(String key)
	{
		dataMap.remove(key);
	}

	public ArbitraryData copyFrom(ArbitraryData otherData)
	{
		dataMap.putAll(otherData.dataMap);
		return this;
	}

	public void copyExisting(ArbitraryData otherData)
	{
		for (String key : dataMap.keySet())
		{
			Datum otherDatum = otherData.dataMap.get(key);

			if (otherDatum != null)
			{
				Datum datum = dataMap.get(key);
				if (otherDatum.type.isAssignableFrom(datum.type))
				{
					datum.datum = otherDatum.datum;
				}
			}
		}
	}

	public ArbitraryData copy()
	{
		ArbitraryData data = new ArbitraryData();
		dataMap.forEach((k, v) -> data.dataMap.put(k, v.copy()));
		return data;
	}

	public Set<String> keySet()
	{
		return dataMap.keySet();
	}

	public Collection<Datum> values()
	{
		return dataMap.values();
	}

	// filter

	private static class Datum
	{
		private Class<?> type;
		private Object datum;

		@Override
		public String toString()
		{
			return "Datum{" + "type=" + type + ", datum=" + datum + '}';
		}

		public Datum copy()
		{
			Datum d = new Datum();
			d.type = type;
			d.datum = datum;
			return d;
		}
	}

	@Override
	public String toString()
	{
		return "ArbitraryData{" + "dataMap=" + dataMap + '}';
	}
}
