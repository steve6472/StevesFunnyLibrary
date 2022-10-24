package steve6472.funnylib.util;

import java.util.Collection;

/**
 * Created by steve6472
 * Date: 9/11/2022
 * Project: StevesFunnyLibrary
 */
public class Preconditions
{
	public static <T> T checkNotNull(final T o)
	{
		if (o == null)
			throw new NullPointerException();
		return o;
	}

	public static <T> T checkNotNull(final T o, String messageIfNull)
	{
		if (o == null)
			throw new NullPointerException(messageIfNull);
		return o;
	}

	public static void checkRange(Collection<?> collection, int index)
	{
		if (index < 0 || index > collection.size())
			throw new ArrayIndexOutOfBoundsException(index);
	}

	public static void checkNotEmpty(Collection<?> collection)
	{
		if (collection.isEmpty())
			throw new RuntimeException("Collection is empty");
	}

	public static void checkPositive(int number)
	{
		if (number < 0)
			throw new RuntimeException("Number is negative");
	}

	public static void checkPositive(long number)
	{
		if (number < 0)
			throw new RuntimeException("Number is negative");
	}

	public static void checkAboveZero(int number)
	{
		if (number < 0)
			throw new RuntimeException("Number is below zero");
	}

	public static void checkAboveZero(long number)
	{
		if (number < 0)
			throw new RuntimeException("Number is below zero");
	}
}
