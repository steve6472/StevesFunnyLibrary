package steve6472.funnylib.minigame.config;

import java.util.Set;

/**
 * Created by steve6472
 * Date: 12/26/2023
 * Project: StevesFunnyLibrary <br>
 */
public interface ConfigType<T>
{
	Set<Class<?>> allowedTypes();

	String getName();

	default boolean isValueAllowed(Object value)
	{
		Class<?> valueClass = value.getClass();
		for (Class<?> allowedType : allowedTypes())
		{
			if (allowedType.isAssignableFrom(valueClass))
				return true;
		}
		return false;
	}
}
