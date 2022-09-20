package steve6472.standalone.interactable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**********************
 * Created by steve6472
 * On date: 4/30/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public class ReflectionHacker
{
	/*
	 * ClassName_MethodName
	 */
	private static Method craftEntity_getHandle;
	private static Method entity_moveTo;

	static
	{
		findClass(bukkitVersion() + ".entity.CraftEntity").ifPresentOrElse(c -> craftEntity_getHandle = findMethodNullable(c, "getHandle"), () -> craftEntity_getHandle = null);
		entity_moveTo = findMethodByParameters(craftEntity_getHandle, void.class, double.class, double.class, double.class, float.class, float.class);
	}

	public static void callEntityMoveTo(Entity entity, double x, double y, double z, float yaw, float pitch)
	{
		if (craftEntity_getHandle == null || entity_moveTo == null)
		{
			// Fail-safe method
			String format = String.format("execute as %s at @s run tp @s ~%s ~%s ~%s", entity.getUniqueId(), x, y, z);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), format);
		} else
		{
			try
			{
				entity_moveTo.invoke(craftEntity_getHandle.invoke(entity), x, y, z, yaw, pitch);
			} catch (IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
				entity_moveTo = null;

				Bukkit.getLogger().warning("Could not call Entity#moveTo");
				Bukkit.getLogger().warning("Will now default to fail-safe method");

				callEntityMoveTo(entity, x, y, z, yaw, pitch);
			}
		}
	}

	/*
	 * Utility functions
	 */

	@Nullable
	private static Method findMethodNullable(Class<?> clazz, String methodName, Class<?>... parameters)
	{
		try
		{
			return clazz.getDeclaredMethod(methodName, parameters);
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static Optional<Class<?>> findClass(String className)
	{
		try
		{
			return Optional.of(Class.forName(className));
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			return Optional.empty();
		}
	}

	private static String bukkitVersion()
	{
		return Bukkit.getServer().getClass().getPackage().getName();
	}

	@Nullable
	private static Method findMethodByParameters(Method fromReturnType, Class<?> returnType, Class<?>... params)
	{
		if (fromReturnType == null)
		{
			return null;
		}

		return findMethodByParameters(fromReturnType.getReturnType(), returnType, params);
	}

	@Nullable
	private static Method findMethodByParameters(Class<?> clazz, Class<?> returnType, Class<?>... params)
	{
		main: for (Method declaredMethod : clazz.getDeclaredMethods())
		{
			if (declaredMethod.getParameterCount() != params.length)
			{
				continue;
			}
			if (declaredMethod.getReturnType() != returnType)
			{
				continue;
			}

			Class<?>[] methodParams = declaredMethod.getParameterTypes();
			for (int i = 0; i < methodParams.length; i++)
			{
				if (params[i] != methodParams[i])
					continue main;
			}

			return declaredMethod;
		}

		return null;
	}
}
