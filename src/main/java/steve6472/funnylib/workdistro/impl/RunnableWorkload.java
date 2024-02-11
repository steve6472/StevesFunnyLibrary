package steve6472.funnylib.workdistro.impl;

import steve6472.funnylib.workdistro.Workload;

/**
 * Created by steve6472
 * Date: 2/11/2024
 * Project: StevesFunnyLibrary <br>
 */
public class RunnableWorkload implements Workload
{
	private final Runnable runnable;

	public RunnableWorkload(Runnable runnable)
	{
		this.runnable = runnable;
	}

	@Override
	public void compute()
	{
		runnable.run();
	}
}
