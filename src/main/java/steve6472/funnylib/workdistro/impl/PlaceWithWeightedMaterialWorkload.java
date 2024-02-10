package steve6472.funnylib.workdistro.impl;

import org.bukkit.Material;
import org.bukkit.World;
import steve6472.funnylib.workdistro.util.WeightedRandomBag;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class PlaceWithWeightedMaterialWorkload extends WorldWorkload
{
	private final int x, y, z;
	private final WeightedRandomBag<Material> place;

	public PlaceWithWeightedMaterialWorkload(World world, int x, int y, int z, WeightedRandomBag<Material> place)
	{
		super(world);
		this.x = x;
		this.y = y;
		this.z = z;
		this.place = place;
	}

	@Override
	public void compute()
	{
		World world = getWorld();
		if (world == null) return;
		world.setBlockData(x, y, z, place.getRandom().createBlockData());
	}
}
