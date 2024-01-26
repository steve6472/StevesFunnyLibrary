package steve6472.funnylib.workdistro.impl;

import org.bukkit.Bukkit;
import org.bukkit.World;
import steve6472.funnylib.workdistro.Workload;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 1/26/2024
 * Project: StevesFunnyLibrary <br>
 */
public abstract class WorldWorkload implements Workload
{
	private final UUID uuid;

	public WorldWorkload(World world)
	{
		this.uuid = world.getUID();
	}

	protected World getWorld()
	{
		return Bukkit.getWorld(uuid);
	}
}
