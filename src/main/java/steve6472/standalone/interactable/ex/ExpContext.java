package steve6472.standalone.interactable.ex;

import org.bukkit.World;
import steve6472.funnylib.util.Preconditions;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExpContext
{
	private final World world;
	long delayTicks;

	public ExpContext(World world)
	{
		this.world = world;
	}

	public void delay(long ticks)
	{
		Preconditions.checkAboveZero(ticks);
		delayTicks += ticks;
	}

	public long getDelay()
	{
		return delayTicks;
	}

	public World getWorld()
	{
		return world;
	}
}
