package steve6472.funnylib.workdistro.impl;

import org.bukkit.World;
import org.bukkit.block.Biome;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ChangeBiomeWorkload extends WorldWorkload
{
	private final int x, y, z;
	private final Biome biome;

	public ChangeBiomeWorkload(World world, int x, int y, int z, Biome biome)
	{
		super(world);
		this.x = x;
		this.y = y;
		this.z = z;
		this.biome = biome;
	}

	@Override
	public void compute()
	{
		World world = getWorld();
		if (world == null) return;
		world.setBiome(x, y, z, biome);
	}
}
