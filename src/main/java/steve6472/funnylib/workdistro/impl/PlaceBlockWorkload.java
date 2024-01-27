package steve6472.funnylib.workdistro.impl;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class PlaceBlockWorkload extends WorldWorkload
{
	private final int x, y, z;
	private final Material block;

	public PlaceBlockWorkload(World world, int x, int y, int z, Material block)
	{
		super(world);
		this.x = x;
		this.y = y;
		this.z = z;
		this.block = block;
	}

	@Override
	public void compute()
	{
		World world = getWorld();
		if (world == null) return;
		world.setBlockData(x, y, z, block.createBlockData());
	}
}
