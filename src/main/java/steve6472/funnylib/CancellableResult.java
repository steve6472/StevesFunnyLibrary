package steve6472.funnylib;

/**
 * Created by steve6472
 * Date: 11/5/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CancellableResult
{
	public CancellableResult()
	{

	}

	protected boolean cancel;

	public void cancel()
	{
		this.cancel = true;
	}

	public boolean isCancelled()
	{
		return cancel;
	}

	public void setCancelled(boolean flag)
	{
		cancel = flag;
	}

	/*
	 * Singleton
	 */

	public static final CancellableResult INSTANCE = new CancellableResult();

	public static void reset()
	{
		INSTANCE.cancel = false;
	}

	public static CancellableResult getInstance()
	{
		return INSTANCE;
	}
}
