package steve6472.funnylib.workdistro;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by steve6472
 * Date: 1/20/2024
 * Project: StevesFunnyLibrary <br>
 */
public class WorkloadRunnable implements Runnable
{
	private static final double MAX_MILLIS_PER_TICK = 10;
	private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

	private final Deque<Workload> workloadDeque = new ArrayDeque<>();

	public void addWorkload(Workload workload)
	{
		workloadDeque.add(workload);
	}

	@Override
	public void run()
	{
		long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

		Workload nextLoad;

		while (System.nanoTime() <= stopTime && (nextLoad = workloadDeque.poll()) != null)
		{
			nextLoad.compute();
		}
	}
}
