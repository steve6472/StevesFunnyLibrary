package steve6472.standalone.interactable.ex;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import steve6472.funnylib.json.INBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.Preconditions;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExpContext implements INBT
{
	private Location executionLocation;
	long delayTicks;

	public ExpContext()
	{
		executionLocation = null;
	}

	public ExpContext(Location location)
	{
		this.executionLocation = location;
	}

	@Override
	public void toNBT(NBT compound)
	{
		compound.setLong("delay_ticks", delayTicks);
		if (executionLocation != null)
			compound.setLocation("execution_location", executionLocation);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		delayTicks = compound.getLong("delay_ticks");
		if (compound.hasLocation("execution_location"))
			executionLocation = compound.getLocation("execution_location");
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

	public void passDelay()
	{
		delayTicks = Math.max(0, delayTicks - 1);
	}

	public World getWorld()
	{
		return executionLocation.getWorld();
	}
}
