package steve6472.funnylib.coroutine;

import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 11/9/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CoroutineExecutor
{
	private final Coroutine<?, ?> coroutine;
	private int waitingTicks;

	public CoroutineExecutor(Coroutine<?, ?> coroutine)
	{
		this.coroutine = coroutine;
	}

	public void waitFor(int waitForTicks)
	{
		this.waitingTicks = Math.max(waitForTicks, 0);
	}

	/**
	 *
	 * @return true if coroutine is waiting
	 */
	public boolean run()
	{
		if (waitingTicks > 0)
		{
			waitingTicks--;
			return true;
		}
		return coroutine.execute(this);
	}

	public boolean ended()
	{
		return coroutine.lines.ended;
	}

	public void debug()
	{
		DepthTest test = new DepthTest();
		coroutine.debug(test);
		test.print();
	}
}
