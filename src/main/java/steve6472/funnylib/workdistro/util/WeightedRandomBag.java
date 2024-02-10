package steve6472.funnylib.workdistro.util;

import java.util.*;

/**
 * @author <a href="https://gamedev.stackexchange.com/a/162987">StackOverflow User</a>
 * @param <T>
 */
public class WeightedRandomBag<T>
{
	private final List<Entry<T>> entries = new ArrayList<>();
	private final Random rand = new Random();
	private double accumulatedWeight;

	public void addEntry(T object, double weight)
	{
		accumulatedWeight += weight;
		Entry<T> e = new Entry<>();
		e.object = object;
		e.accumulatedWeight = accumulatedWeight;
		entries.add(e);
	}

	public T getRandom()
	{
		double r = rand.nextDouble() * accumulatedWeight;

		for (Entry<T> entry : entries)
		{
			if (entry.accumulatedWeight >= r)
			{
				return entry.object;
			}
		}
		return null; //should only happen when there are no entries
	}

	private static class Entry<T>
	{
		double accumulatedWeight;
		T object;
	}
}