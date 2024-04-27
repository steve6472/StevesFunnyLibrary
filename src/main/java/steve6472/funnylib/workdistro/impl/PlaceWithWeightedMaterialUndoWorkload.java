package steve6472.funnylib.workdistro.impl;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import steve6472.funnylib.workdistro.UndoManager;
import steve6472.funnylib.workdistro.Workload;
import steve6472.funnylib.workdistro.util.WeightedRandomBag;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class PlaceWithWeightedMaterialUndoWorkload extends WorldUndoWorkload
{
	private final int x, y, z;
	private final WeightedRandomBag<Material> place;

	public PlaceWithWeightedMaterialUndoWorkload(World world, Player undoOwner, UndoManager.UndoType undoType, int x, int y, int z, WeightedRandomBag<Material> place)
	{
		super(world, undoOwner, undoType);
		this.x = x;
		this.y = y;
		this.z = z;
		this.place = place;
	}

	@Override
	public void compute()
	{
		placeOrFallBlock(x, y, z, place.getRandom().createBlockData());
	}

	@Override
	public Workload createUndo()
	{
		World world = getWorld();
		if (world == null) return null;
		return new PlaceBlockStateWorkload(world, x, y, z, world.getBlockState(x, y, z), true);
	}
}
