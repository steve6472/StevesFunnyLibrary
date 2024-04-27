package steve6472.funnylib.workdistro.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
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

	protected void placeOrFallBlock(int x, int y, int z, BlockData data)
	{
		World world = getWorld();
		if (world == null) return;

		if (data.getMaterial().hasGravity() && world.getBlockAt(x, y - 1, z).getType().isAir())
		{
			world.spawnFallingBlock(new Location(world, x, y, z), data);
		} else
		{
			world.setBlockData(x, y, z, data);
		}
	}
}
