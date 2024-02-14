package steve6472.funnylib.entity.ecs.components;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by steve6472
 * Date: 2/11/2024
 * Project: StevesFunnyLibrary <br>
 */
public class LocationComp
{
	private final Location location;
	private boolean moved;

	public LocationComp(Location location)
	{
		this.location = location;
		moved = true;
	}

	public void teleport(double x, double y, double z)
	{
		location.setX(x);
		location.setY(y);
		location.setZ(z);
		moved = true;
	}

	public World getWorld()
	{
		return location.getWorld();
	}

	public boolean hasMoved()
	{
		return moved;
	}

	public void unmove()
	{
		moved = false;
	}

	public Location getLocation()
	{
		return location.clone();
	}
}
