package steve6472.funnylib.minigame.config;

import steve6472.funnylib.util.Preconditions;

/**
 * Created by steve6472
 * Date: 12/20/2023
 * Project: StevesFunnyLibrary <br>
 */
public class Value<T>
{
	private final ConfigType<T> type;
	private final String id;
	private final String name;
	private final boolean runtimeEdit;

	public static <T> Value<T> create(ConfigType<T> type, String name, String id, boolean runtimeEdit)
	{
		return new Value<>(type, name, id, runtimeEdit);
	}

	public static <T> Value<T> create(ConfigType<T> type, String name, String id)
	{
		return new Value<>(type, name, id, false);
	}

	private Value(ConfigType<T> type, String name, String id, boolean runtimeEdit)
	{
		Preconditions.checkId(id);
		this.type = type;
		this.name = name;
		this.id = id;
		this.runtimeEdit = runtimeEdit;
	}

	public ConfigType<T> getValueType()
	{
		return type;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public boolean allowRuntimeEdit()
	{
		return runtimeEdit;
	}

	@Override
	public String toString()
	{
		return "Value{" + "type=" + type + ", id='" + id + '\'' + ", name='" + name + '\'' + '}';
	}
}
