package steve6472.funnylib.entity.display;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockSupport;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Transformation;
import org.joml.*;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.data.BlockInfo;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.workdistro.impl.OnEntityWorkload;

import java.util.*;

/**
 * Created by steve6472
 * Date: 6/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BlockStructureEntity extends AdjustableDisplayEntity
{
	public BlockStructureEntity(Entity root)
	{
		super(root);
	}

	public static BlockStructureEntity createFromStructure(Location location, GameStructure structure)
	{
		final Vector3i size = structure.getSize().add(1, 1, 1);
		final Vector3d center = new Vector3d(size.x * 0.5, size.y * 0.5, size.z * 0.5);
		final Vector3d centerOffset = new Vector3d(-center.x, -center.y, -center.z);
		final World world = location.getWorld();
		assert world != null;

		BlockStructureEntity blockStructure = new BlockStructureEntity(world.spawn(location, ItemDisplay.class));

		BlockInfo[] blocks = structure.getBlocks();
		List<BlockInfo> infoList = new ArrayList<>(blocks.length);

		int width = structure.getSize().x + 1;
		int height = structure.getSize().y + 1;
		int depth = structure.getSize().z + 1;

		Map<Vector3i, BlockInfo> dataMap = new HashMap<>();

		for (BlockInfo block : blocks)
		{
			dataMap.put(block.position(), block);
		}

		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				for (int k = 0; k < depth; k++)
				{
					// If not on edge
					if (i != 0 && j != 0 && k != 0 && i != width - 1 && j != height - 1 && k != depth - 1)
					{
						if (!shouldBeVisible(dataMap, i, j, k)) continue;
					}

					infoList.add(dataMap.get(new Vector3i(i, j, k)));
				}
			}
		}

		Collections.shuffle(infoList);

		for (BlockInfo block : infoList)
		{
			if (block.data().getMaterial().isAir()) continue;
			if (block.data().getMaterial() == Material.BARRIER) continue;

			FunnyLib.getWorkloadRunnable().addWorkload(new OnEntityWorkload<>(blockStructure.getRootEntity().get(), ent ->
			{
				blockStructure.addDisplay(BlockDisplay.class, entity ->
				{
					Vector3i position = block.position();
					entity.setBlock(block.data().clone());
					entity.setBrightness(new Display.Brightness(15, 15));
					entity.setTransformation(
						new Transformation(
							new Vector3f((float) centerOffset.x + position.x, (float) centerOffset.y + position.y, (float) centerOffset.z + position.z),
							new Quaternionf(),
							new Vector3f(1, 1, 1),
							new Quaternionf()));
				});
			}));
		}
		return blockStructure;
	}

	private static boolean shouldBeVisible(Map<Vector3i, BlockInfo> data, int x, int y, int z)
	{
		BlockData blockData = data.get(new Vector3i(x, y, z)).data();

		if (blockData.getMaterial().isAir()) return false;

		return !(
			(blockData.isFaceSturdy(BlockFace.UP, BlockSupport.FULL)    && data.get(new Vector3i(x, y + 1, z)).data().isFaceSturdy(BlockFace.DOWN, BlockSupport.FULL))  &&
			(blockData.isFaceSturdy(BlockFace.DOWN, BlockSupport.FULL)  && data.get(new Vector3i(x, y - 1, z)).data().isFaceSturdy(BlockFace.UP, BlockSupport.FULL))    &&
			(blockData.isFaceSturdy(BlockFace.EAST, BlockSupport.FULL)  && data.get(new Vector3i(x + 1, y, z)).data().isFaceSturdy(BlockFace.WEST, BlockSupport.FULL))  &&
			(blockData.isFaceSturdy(BlockFace.WEST, BlockSupport.FULL)  && data.get(new Vector3i(x - 1, y, z)).data().isFaceSturdy(BlockFace.EAST, BlockSupport.FULL))  &&
			(blockData.isFaceSturdy(BlockFace.NORTH, BlockSupport.FULL) && data.get(new Vector3i(x, y, z - 1)).data().isFaceSturdy(BlockFace.SOUTH, BlockSupport.FULL)) &&
			(blockData.isFaceSturdy(BlockFace.SOUTH, BlockSupport.FULL) && data.get(new Vector3i(x, y, z + 1)).data().isFaceSturdy(BlockFace.NORTH, BlockSupport.FULL))
		);
	}
}
