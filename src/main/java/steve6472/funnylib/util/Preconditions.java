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
}
