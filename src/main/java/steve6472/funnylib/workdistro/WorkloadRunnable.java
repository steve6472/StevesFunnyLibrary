package steve6472.funnylib.workdistro;

import steve6472.funnylib.util.NMS;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 1/20/2024
 * Project: StevesFunnyLibrary <br>
 */
public class WorkloadRunnable implements Runnable
{
	private static final double MAX_MILLIS_PER_TICK = 20;
	private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

	private final Deque<Workload> workloadDeque = new ArrayDeque<>();
	private final UndoManager undoManager = new UndoManager(64);

	public UndoManager undoManager()
	{
		return undoManager;
	}

	public void addWorkload(Workload workload)
	{
		workloadDeque.add(workload);
	}

	private final long[] lastTickState = new long[100];

	int recoveryTime = 0;

	@Override
	public void run()
	{
		long change = 0;

		for (int i = 0; i < 100; i++)
		{
			long l = lastTickState[i] - NMS.getTickTimeNanos()[i];
			if (l != 0)
                change = Math.max(l, Math.abs(l));

			lastTickState[i] = NMS.getTickTimeNanos()[i];
		}

		double mspt = change * 1e-6;

		if (mspt >= 40)
        {
			recoveryTime = 10;
            return;
        }

		if (recoveryTime > 0)
		{
			recoveryTime--;
			return;
		}

		long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

		Workload nextLoad;

		while (System.nanoTime() <= stopTime && (nextLoad = workloadDeque.poll()) != null)
		{
			if (nextLoad instanceof UndoWorkload undoWorkload)
            {
	            Workload undo = undoWorkload.createUndo();
				undoManager.addWorkload(undoWorkload.uuid(), undoWorkload.type(), undo);
            }

			nextLoad.compute();
		}
	}

	public void removeWorkloadIf(Predicate<Workload> predicate)
	{
		workloadDeque.removeIf(predicate);
	}

	public List<Workload> getWorkloadsCopy()
	{
		List<Workload> copy = new ArrayList<>(workloadDeque.size());
        copy.addAll(workloadDeque);
		return copy;
	}
}
