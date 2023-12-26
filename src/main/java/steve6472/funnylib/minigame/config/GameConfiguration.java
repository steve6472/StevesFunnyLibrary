package steve6472.funnylib.minigame.config;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.json.JsonConfig;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by steve6472
 * Date: 12/19/2023
 * Project: StevesFunnyLibrary <br>
 */
public class GameConfiguration
{
	private final ConfigTypeRegistry configTypeRegistry;
	private final Map<Value<?>, Object> values;
	public final String minigameId;
	private final JsonConfig config;

	public JSONMessage name = JSONMessage.create("unnamed").color(ChatColor.GRAY);
	public JSONMessage shortDescription = JSONMessage.create("no description").color(ChatColor.GRAY);

	public GameConfiguration(ConfigTypeRegistry configTypeRegistry, String minigameId)
	{
		this.configTypeRegistry = configTypeRegistry;
		this.minigameId = minigameId;
		values = new HashMap<>();
		config = new JsonConfig(minigameId, "minigames", FunnyLib.getPlugin());
	}

	public GameConfiguration setName(@NotNull JSONMessage name)
	{
		Preconditions.checkNotNull(name);
		this.name = name;
		return this;
	}

	public GameConfiguration setDescription(@NotNull JSONMessage shortDescription)
	{
		Preconditions.checkNotNull(shortDescription);
		this.shortDescription = shortDescription;
		return this;
	}

	public GameConfiguration registerValue(Value<?> value)
	{
		if (!configTypeRegistry.isValidType(value))
		{
			throw new RuntimeException("Invalid type '" + value.getValueType() + "'");
		}

		for (Value<?> key : values.keySet())
		{
			if (key.getId().equals(value.getId()))
			{
				throw new RuntimeException("Value with id " + value.getId() + " already exists!");
			}
		}
		values.put(value, null);
		return this;
	}

	public void setValue(Value<?> value, Object object)
	{
		if (!values.containsKey(value))
			throw new IllegalStateException("Tried to set to non-registrated value!");

		if (object != null && !value.getValueType().isValueAllowed(object))
		{
			throw new RuntimeException("Value '" + value.getName() + "' (" + value.getId() + ") is of invalid type -> " + value.getValueType() + " expected, got " + object.getClass().getName());
		}

		values.put(value, object);
	}

	public <T> T getValue(Value<T> value)
	{
		//noinspection unchecked
		return (T) values.get(value);
	}

	public Map<Value<?>, Object> getValues()
	{
		// I wanted to expose only immutable copy of the map
		// But that Map.copyOf does not allow nulls.....
		return values;
	}

	public void load()
	{
		config.loadJsonConfig();
		JSONObject jsonConfig = config.getJsonConfig();

		Set<Value<?>> keySet = values.keySet();
		for (Value<?> value : keySet)
		{
			setValue(value, configTypeRegistry.load(value, jsonConfig));
		}
	}

	public void save()
	{
		JSONObject jsonConfig = config.getJsonConfig();

		for (Value<?> value : values.keySet())
		{
			//noinspection unchecked
			Value<Object> val = (Value<Object>) value;
			Object obj = getValue(value);

			configTypeRegistry.save(val, obj, jsonConfig);

			values.replace(value, null);
		}
		config.saveJsonConfig();
	}

	public void validate()
	{
		for (Value<?> value : values.keySet())
		{
			Object obj = getValue(value);
			if (obj == null)
				throw new RuntimeException("Value '" + value.getName() + "' (" + value.getId() + ") is null!");
			if (!value.getValueType().isValueAllowed(obj))
				throw new RuntimeException("Value '" + value.getName() + "' (" + value.getId() + ") is of invalid type -> " + value.getValueType() + " expected, got " + obj.getClass().getName());
		}
	}
}
