package steve6472.funnylib.workdistro.impl;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import steve6472.funnylib.workdistro.util.WeightedRandomBag;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ReplaceWithWeightedMaterialWorkload extends WorldWorkload
{
	private final int x, y, z;
	private final Material match;
	private final WeightedRandomBag<Material> place;

	public ReplaceWithWeightedMaterialWorkload(World world, int x, int y, int z, Material match, WeightedRandomBag<Material> place)
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
			blockAt.setType(place.getRandom());
	}
}
