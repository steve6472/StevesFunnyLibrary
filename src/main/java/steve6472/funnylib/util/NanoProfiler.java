package steve6472.funnylib.util;

import java.util.ArrayList;
import java.util.List;

public class NanoProfiler
{
	private final List<Pair<Long, String>> fractions = new ArrayList<>();
	private long totalStart;

	public void start()
	{
		fractions.clear();
		fraction("START");
		totalStart = System.nanoTime();
	}

	public void fraction(String name)
	{
		fractions.add(new Pair<>(System.nanoTime(), name));
	}

	public void printEnd()
	{
		fraction("END");
		long now = System.nanoTime();

		for (int i = 1; i < fractions.size(); i++)
		{
			Pair<Long, String> lastFraction = fractions.get(i - 1);
			Pair<Long, String> fraction = fractions.get(i);
			double time = (fraction.a() - lastFraction.a()) / 1_000_000.0;
			System.out.println(lastFraction.b() + " -> " + fraction.b() + " took " + time + "ms");
		}

		System.out.println("Took total: " + ((now - totalStart) / 1_000_000.0) + "ms");
	}

	public float endTotalTime()
	{
		fraction("END");
		long now = System.nanoTime();

		return (now - totalStart) / 1_000_000.0f;
	}
}
