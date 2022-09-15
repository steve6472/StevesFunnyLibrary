package steve6472.funnylib.blocks.stateengine.properties;

/**********************
 * Created by steve6472 (Mirek Jozefek)
 * On date: 03.07.2020
 * Project: CaveGame
 *
 ***********************/
public class EnumProperty<T extends Comparable<T>> extends Property<T>
{
	private final T[] possibleValues;
	private final Class<T> clazz;

	public static <T extends Comparable<T>> EnumProperty<T> create(String name, Class<T> clazz, T... possibleValues)
	{
		return new EnumProperty<>(name, clazz, possibleValues);
	}

	private EnumProperty(String name, Class<T> clazz, T[] possibleValues)
	{
		super(name);
		this.possibleValues = possibleValues;
		this.clazz = clazz;
	}

	public Class<T> getClazz()
	{
		return clazz;
	}

	@Override
	public T[] getPossibleValues()
	{
		return possibleValues;
	}

	@Override
	public String toString(Comparable<?> obj)
	{
		return ((Enum<?>) obj).name();
	}

	@Override
	public T fromString(String str)
	{
		for (T possibleValue : possibleValues)
		{
			if (((Enum<?>) possibleValue).name().equals(str))
				return possibleValue;

		}
		throw new RuntimeException("'" + str + "' not found in enum " + clazz.getSimpleName());
	}
}
