package steve6472.standalone.interactable.ex;

import org.bukkit.Location;
import org.bukkit.World;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveLong;
import steve6472.funnylib.json.codec.codecs.LocationCodec;
import steve6472.funnylib.json.codec.codecs.WorldCodec;
import steve6472.funnylib.util.Preconditions;

/**
 * Created by steve6472
 * Date: 10/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExpContext
{
	@Save(WorldCodec.class)     private final World world;
	@Save(LocationCodec.class)  private final Location executionLocation;
	@SaveLong                   long delayTicks;

	public ExpContext()
	{
		world = null;
		executionLocation = null;
	}

	public ExpContext(Location location)
	{
		this.world = location.getWorld();
		this.executionLocation = location;
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
		return world;
	}
}
