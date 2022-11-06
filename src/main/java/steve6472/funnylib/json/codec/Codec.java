package steve6472.funnylib.json.codec;

import org.json.JSONArray;
import org.json.JSONObject;
import oshi.util.tuples.Triplet;
import steve6472.funnylib.json.codec.ann.*;
import steve6472.funnylib.json.codec.codecs.ObjectCodec;
import steve6472.funnylib.util.TriFunction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

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
		//noinspection unchecked
		toJson((T) obj, json);
	}

	static final Map<Class<?>, Codec<?>> CODECS = new HashMap<>();
	static final Map<Class<?>, Triplet<Class<? extends Annotation>, TriFunction<JSONObject, String, ?, Object>, BiFunction<Object, ?, Object>>> DEFAULT_ANNOTATION_CODECS = new HashMap<>();

	private static <A, R> R nullDefault(A ann, Function<A, R> nonNull, R whenNull)
	{
		if (ann == null)
		{
			return whenNull;
		} else
		{
			return nonNull.apply(ann);
		}
	}

	static
	{
		regDefAnnCodec(Integer.class, SaveInt.class, (json, key, ann) -> json.optInt(key, nullDefault(ann, SaveInt::defVal, 0)), (got, ann) -> nullDefault(got, n -> n, 0));
		regDefAnnCodec(Double.class, SaveDouble.class, (json, key, ann) -> json.optDouble(key, nullDefault(ann, SaveDouble::defVal, 0.0)), (got, ann) -> nullDefault(got, n -> n, 0.0));
		regDefAnnCodec(Float.class, SaveFloat.class, (json, key, ann) -> json.optFloat(key, nullDefault(ann, SaveFloat::defVal, 0.0f)), (got, ann) -> nullDefault(got, n -> n, 0.0f));
		regDefAnnCodec(Long.class, SaveLong.class, (json, key, ann) -> json.optLong(key, nullDefault(ann, SaveLong::defVal, 0L)), (got, ann) -> nullDefault(got, n -> n, 0L));
		regDefAnnCodec(Boolean.class, SaveBool.class, (json, key, ann) -> json.optBoolean(key, nullDefault(ann, SaveBool::defVal, false)), (got, ann) -> nullDefault(got, n -> n, false));

		regDefAnnCodec(int.class, SaveInt.class, (json, key, ann) -> json.optInt(key, nullDefault(ann, SaveInt::defVal, 0)), (got, ann) -> nullDefault(got, n -> n, 0));
		regDefAnnCodec(double.class, SaveDouble.class, (json, key, ann) -> json.optDouble(key, nullDefault(ann, SaveDouble::defVal, 0.0)), (got, ann) -> nullDefault(got, n -> n, 0.0));
		regDefAnnCodec(float.class, SaveFloat.class, (json, key, ann) -> json.optFloat(key, nullDefault(ann, SaveFloat::defVal, 0.0f)), (got, ann) -> nullDefault(got, n -> n, 0.0f));
		regDefAnnCodec(long.class, SaveLong.class, (json, key, ann) -> json.optLong(key, nullDefault(ann, SaveLong::defVal, 0L)), (got, ann) -> nullDefault(got, n -> n, 0L));
		regDefAnnCodec(boolean.class, SaveBool.class, (json, key, ann) -> json.optBoolean(key, nullDefault(ann, SaveBool::defVal, false)), (got, ann) -> nullDefault(got, n -> n, false));

		regDefAnnCodec(String.class, SaveString.class, (json, key, ann) -> json.optString(key, nullDefault(ann, SaveString::defVal, "")), (got, ann) -> nullDefault(got, n -> n, ""));
		regDefAnnCodec(double[].class, SaveDoubleArr.class, (json, key, ann) -> Objects.requireNonNullElse(json.optJSONArray(key), new JSONArray()).toList().stream().mapToDouble(d -> (double) d).toArray(), (got, ann) -> new JSONArray(nullDefault(got, n -> n, 0.0)));
	}

	public static <A extends Annotation> void regDefAnnCodec(Class<?> clazz, Class<A> anno, TriFunction<JSONObject, String, A, Object> setField, BiFunction<Object, A, Object> castFromField)
	{
		DEFAULT_ANNOTATION_CODECS.put(clazz, new Triplet<>(anno, setField, castFromField));
	}

	public static <T> void registerCodec(Codec<T> codec)
	{
		CODECS.put(codec.getClass(), codec);
	}

	public static JSONObject save(Object obj)
	{
		try
		{
			return save_(obj, false);
		} catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static JSONObject saveAll(Object obj)
	{
		try
		{
			return save_(obj, true);
		} catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static <T> T load(T obj, JSONObject jsonObject)
	{
		try
		{
			load_(obj, jsonObject, false);
			return obj;
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static <T> T loadAll(T obj, JSONObject jsonObject)
	{
		try
		{
			load_(obj, jsonObject, true);
			return obj;
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static JSONObject save_(Object object, boolean saveAll) throws IllegalAccessException
	{
		JSONObject json = new JSONObject();

		if (object == null)
		{
			json.put("__null__", true);
			return json;
		}

		for (Field declaredField : object.getClass().getDeclaredFields())
		{
			declaredField.setAccessible(true);
			if (declaredField.isAnnotationPresent(SaveInt.class))
			{
				json.put(declaredField.getName(), (int) declaredField.get(object));
			}
			if (declaredField.isAnnotationPresent(SaveLong.class))
			{
				json.put(declaredField.getName(), (long) declaredField.get(object));
			}
			else if (declaredField.isAnnotationPresent(SaveDouble.class))
			{
				json.put(declaredField.getName(), (double) declaredField.get(object));
			}
			else if (declaredField.isAnnotationPresent(SaveFloat.class))
			{
				json.put(declaredField.getName(), (float) declaredField.get(object));
			}
			else if (declaredField.isAnnotationPresent(SaveBool.class))
			{
				json.put(declaredField.getName(), (boolean) declaredField.get(object));
			}
			else if (declaredField.isAnnotationPresent(SaveString.class))
			{
				json.put(declaredField.getName(), declaredField.get(object));
			}
			else if (declaredField.isAnnotationPresent(SaveDoubleArr.class))
			{
				json.put(declaredField.getName(), new JSONArray(declaredField.get(object)));
			}
			else if (declaredField.isAnnotationPresent(Save.class))
			{
				Save annotation = declaredField.getAnnotation(Save.class);
				if (annotation.value() != ObjectCodec.class)
				{
					Codec<?> codec = Codec.CODECS.get(annotation.value());
					if (codec == null)
						throw new RuntimeException("Codec for type " + annotation.value() + " not found!");
					JSONObject objSave = new JSONObject();
					codec.toJSON(declaredField.get(object), objSave);
					json.put(declaredField.getName(), objSave);
				} else
				{
					json.put(declaredField.getName(), save_(declaredField.get(object), saveAll));
				}
			} else if (saveAll)
			{
				Triplet<Class<? extends Annotation>, TriFunction<JSONObject, String, ?, Object>, BiFunction<Object, ?, Object>> var = DEFAULT_ANNOTATION_CODECS.get(declaredField.getType());
				if (var != null)
				{
					json.put(declaredField.getName(), var.getC().apply(declaredField.get(object), null));
				} else
				{
					json.put(declaredField.getName(), save_(declaredField.get(object), saveAll));
				}
			}
		}

		return json;
	}

	private static void load_(Object object, JSONObject json, boolean loadAll) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException
	{
		for (Field declaredField : object.getClass().getDeclaredFields())
		{
			declaredField.setAccessible(true);
			if (declaredField.isAnnotationPresent(SaveInt.class))
			{
				declaredField.set(object, json.optInt(declaredField.getName(), declaredField.getAnnotation(SaveInt.class).defVal()));
			}
			if (declaredField.isAnnotationPresent(SaveLong.class))
			{
				declaredField.set(object, json.optLong(declaredField.getName(), declaredField.getAnnotation(SaveLong.class).defVal()));
			}
			else if (declaredField.isAnnotationPresent(SaveDouble.class))
			{
				declaredField.set(object, json.optDouble(declaredField.getName(), declaredField.getAnnotation(SaveDouble.class).defVal()));
			}
			else if (declaredField.isAnnotationPresent(SaveFloat.class))
			{
				declaredField.set(object, json.optFloat(declaredField.getName(), declaredField.getAnnotation(SaveFloat.class).defVal()));
			}
			else if (declaredField.isAnnotationPresent(SaveBool.class))
			{
				declaredField.set(object, json.optBoolean(declaredField.getName(), declaredField.getAnnotation(SaveBool.class).defVal()));
			}
			else if (declaredField.isAnnotationPresent(SaveString.class))
			{
				declaredField.set(object, json.optString(declaredField.getName(), declaredField.getAnnotation(SaveString.class).defVal()));
			}
			else if (declaredField.isAnnotationPresent(SaveDoubleArr.class))
			{
//				JSONArray objects = json.optJSONArray(declaredField.getName());
//				double[] arr = new double[objects.length()];
//				for (int i = 0; i < objects.length(); i++)
//				{
//					arr[i] = objects.getDouble(i);
//				}
				// A cheeky one liner here
				declaredField.set(object, Objects.requireNonNullElse(json.optJSONArray(declaredField.getName()), new JSONArray()).toList().stream().mapToDouble(d -> (double) d).toArray());
			}
			else if (declaredField.isAnnotationPresent(Save.class))
			{
				Save annotation = declaredField.getAnnotation(Save.class);
				if (annotation.value() != ObjectCodec.class)
				{
					Codec<?> codec = Codec.CODECS.get(annotation.value());
					if (codec == null)
						throw new RuntimeException("Codec for type " + annotation.value() + " not found!");
					JSONObject jsonData = json.optJSONObject(declaredField.getName(), null);
					if (jsonData == null)
					{
						declaredField.set(object, null);
					} else
					{
						Object o = codec.fromJson(jsonData);
						declaredField.set(object, o);
					}
				} else
				{
					if (json.has(declaredField.getName()))
					{
						JSONObject jsonObject = json.getJSONObject(declaredField.getName());

						if (jsonObject.optBoolean("__null__"))
						{
							declaredField.set(object, null);
						} else
						{
							final Constructor<?> declaredConstructors = declaredField.getType().getDeclaredConstructor();
							final Object o = declaredConstructors.newInstance();

							load_(o, jsonObject, loadAll);

							declaredField.set(object, o);
						}
					} else
					{
						declaredField.set(object, null);
					}
				}
			} else if (loadAll)
			{
				Triplet<Class<? extends Annotation>, TriFunction<JSONObject, String, ?, Object>, BiFunction<Object, ?, Object>> var = DEFAULT_ANNOTATION_CODECS.get(declaredField.getType());
				if (var != null)
				{
					declaredField.set(object, var.getB().apply(json, declaredField.getName(), null));
				} else
				{
					if (json.has(declaredField.getName()))
					{
						JSONObject jsonObject = json.getJSONObject(declaredField.getName());

						if (jsonObject.optBoolean("__null__"))
						{
							declaredField.set(object, null);
						} else
						{
							final Constructor<?> declaredConstructors = declaredField.getType().getDeclaredConstructor();
							final Object o = declaredConstructors.newInstance();

							load_(o, jsonObject, loadAll);

							declaredField.set(object, o);
						}
					} else
					{
						declaredField.set(object, null);
					}
				}
			}
		}
	}
}
