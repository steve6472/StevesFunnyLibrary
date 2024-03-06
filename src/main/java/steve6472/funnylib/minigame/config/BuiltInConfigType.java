package steve6472.funnylib.minigame.config;

import org.joml.Vector3i;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.data.Marker;

import java.util.List;
import java.util.Set;

/**
 * Created by steve6472
 * Date: 12/26/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BuiltInConfigType<T> implements ConfigType<T>
{
	/*
	 * Primitives
	 */
	public static final ConfigType<Integer> INT = new BuiltInConfigType<>("INT", int.class, Integer.class);

	public static final ConfigType<Double> DOUBLE = new BuiltInConfigType<>("DOUBLE", double.class, Double.class);
	public static final ConfigType<String> STRING = new BuiltInConfigType<>("STRING", String.class);
	public static final ConfigType<Boolean> BOOLEAN = new BuiltInConfigType<>("BOOLEAN", boolean.class, Boolean.class);

	public static final ConfigType<String> ID_MATCH = new BuiltInConfigType<>("ID_MATCH", String.class);
	public static final ConfigType<List<String>> STRING_LIST = new BuiltInConfigType<>("STRING_LIST", List.class);

	/*
	 * Custom
	 */
	public static final ConfigType<GameStructure> STRUCTURE = new BuiltInConfigType<>("STRUCTURE", GameStructure.class);
	public static final ConfigType<Marker> MARKER = new BuiltInConfigType<>("MARKER", Marker.class);
	public static final ConfigType<Vector3i> VEC_3I = new BuiltInConfigType<>("VEC_3I", Vector3i.class);

	private final Set<Class<?>> allowedTypes;
	private final String name;

	BuiltInConfigType(String name, Set<Class<?>> allowedTypes)
	{
		this.name = name;
		this.allowedTypes = allowedTypes;
	}

	BuiltInConfigType(String name, Class<?>... allowedTypes)
	{
		this.name = name;
		this.allowedTypes = Set.of(allowedTypes);
	}

	@Override
	public Set<Class<?>> allowedTypes()
	{
		return allowedTypes;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return "BuiltInConfigType{" + "allowedTypes=" + allowedTypes + ", name='" + name + '\'' + '}';
	}
}
