package steve6472.funnylib.workdistro.impl;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ReplaceBlockWorkload extends WorldWorkload
{
	private final int x, y, z;
	private final Material place;
	private final Material match;

	public ReplaceBlockWorkload(World world, int x, int y, int z, Material match, Material place)
	{
		super(world);
		this.x = x;
		this.y = y;
		this.z = z;
		this.match = match;
		this.place = place;
	}

	@Override
	public void compute()
	{
		World world = getWorld();
		if (world == null) return;
		Block blockAt = world.getBlockAt(x, y, z);
		if (blockAt.getType().equals(match))
			blockAt.setType(place);
	}
}
