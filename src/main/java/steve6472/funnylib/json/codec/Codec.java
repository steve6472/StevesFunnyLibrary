package steve6472.funnylib.json.codec;

import org.json.JSONObject;
import steve6472.funnylib.json.codec.ann.*;
import steve6472.funnylib.json.codec.codecs.ObjectCodec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class Codec<T>
{
	public abstract T fromJson(JSONObject json);
	public abstract void toJson(T obj, JSONObject json);

	void toJSON(Object obj, JSONObject json)
	{
		toJson((T) obj, json);
	}


	static final Map<Class<?>, Codec<?>> CODECS = new HashMap<>();

	public static <T> void registerCodec(Codec<T> codec)
	{
		CODECS.put(codec.getClass(), codec);
	}

	public static JSONObject save(Object obj)
	{
		try
		{
			return save_(obj);
		} catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static <T> T load(T obj, JSONObject jsonObject)
	{
		try
		{
			load_(obj, jsonObject);
			return obj;
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static JSONObject save_(Object object) throws IllegalAccessException
	{
		JSONObject json = new JSONObject();

		for (Field declaredField : object.getClass().getDeclaredFields())
		{
			if (declaredField.isAnnotationPresent(SaveInt.class))
			{
				json.put(declaredField.getName(), (int) declaredField.get(object));
			}
			else if (declaredField.isAnnotationPresent(SaveDouble.class))
			{
				json.put(declaredField.getName(), (double) declaredField.get(object));
			}
			else if (declaredField.isAnnotationPresent(SaveFloat.class))
			{
				json.put(declaredField.getName(), (float) declaredField.get(object));
			}
			else if (declaredField.isAnnotationPresent(SaveString.class))
			{
				json.put(declaredField.getName(), declaredField.get(object));
			}
			else if (declaredField.isAnnotationPresent(Save.class))
			{
				Save annotation = declaredField.getAnnotation(Save.class);
				if (annotation.type() != ObjectCodec.class)
				{
					Codec<?> codec = Codec.CODECS.get(annotation.type());
					if (codec == null)
						throw new RuntimeException("Codec for type " + annotation.type() + " not found!");
					JSONObject objSave = new JSONObject();
					codec.toJSON(declaredField.get(object), objSave);
					json.put(declaredField.getName(), objSave);
				} else
				{
					json.put(declaredField.getName(), save_(declaredField.get(object)));
				}
			}
		}

		return json;
	}

	private static void load_(Object object, JSONObject json) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException
	{
		for (Field declaredField : object.getClass().getDeclaredFields())
		{
			if (declaredField.isAnnotationPresent(SaveInt.class))
			{
				declaredField.set(object, json.optInt(declaredField.getName(), declaredField.getAnnotation(SaveInt.class).defVal()));
			}
			else if (declaredField.isAnnotationPresent(SaveDouble.class))
			{
				declaredField.set(object, json.optDouble(declaredField.getName(), declaredField.getAnnotation(SaveDouble.class).defVal()));
			}
			else if (declaredField.isAnnotationPresent(SaveFloat.class))
			{
				declaredField.set(object, json.optFloat(declaredField.getName(), declaredField.getAnnotation(SaveFloat.class).defVal()));
			}
			else if (declaredField.isAnnotationPresent(SaveString.class))
			{
				declaredField.set(object, json.optString(declaredField.getName(), declaredField.getAnnotation(SaveString.class).defVal()));
			}
			else if (declaredField.isAnnotationPresent(Save.class))
			{
				Save annotation = declaredField.getAnnotation(Save.class);
				if (annotation.type() != ObjectCodec.class)
				{
					Codec<?> codec = Codec.CODECS.get(annotation.type());
					if (codec == null)
						throw new RuntimeException("Codec for type " + annotation.type() + " not found!");
					Object o = codec.fromJson(json.getJSONObject(declaredField.getName()));
					declaredField.set(object, o);
				} else
				{
					if (json.has(declaredField.getName()))
					{
						final Constructor<?> declaredConstructors = declaredField.getType().getDeclaredConstructor();
						final Object o = declaredConstructors.newInstance();

						load_(o, json.getJSONObject(declaredField.getName()));

						declaredField.set(object, o);
					} else
					{
						declaredField.set(object, null);
					}
				}
			}
		}
	}
}
