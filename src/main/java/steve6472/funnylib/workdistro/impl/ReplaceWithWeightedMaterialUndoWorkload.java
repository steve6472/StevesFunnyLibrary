package steve6472.funnylib.workdistro.impl;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import steve6472.funnylib.workdistro.UndoManager;
import steve6472.funnylib.workdistro.Workload;
import steve6472.funnylib.workdistro.util.WeightedRandomBag;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class ReplaceWithWeightedMaterialUndoWorkload extends WorldUndoWorkload
{
	private final int x, y, z;
	private final Material match;
	private final WeightedRandomBag<Material> place;

	public ReplaceWithWeightedMaterialUndoWorkload(World world, Player undoOwner, UndoManager.UndoType undoType, int x, int y, int z, Material match, WeightedRandomBag<Material> place)
	{
		super(world, undoOwner, undoType);
		this.x = x;
		this.y = y;
		this.z = z;
		this.match = match;
		this.place = place;
	}

	@Override
	public void compute()
	{
		if (place.isEmpty()) return;
		World world = getWorld();
		if (world == null) return;
		Block blockAt = world.getBlockAt(x, y, z);
		if (blockAt.getType().equals(match))
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
