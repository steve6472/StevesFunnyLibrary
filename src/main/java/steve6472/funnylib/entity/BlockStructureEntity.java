package steve6472.funnylib.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Transformation;
import org.joml.*;
import steve6472.funnylib.data.BlockInfo;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.serialize.PdcNBT;

/**
 * Created by steve6472
 * Date: 6/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BlockStructureEntity extends MultiDisplayEntity
{
	public BlockStructureEntity(Entity root)
	{
		super(root);
	}

	public static BlockStructureEntity createFromStructure(Location location, GameStructure structure)
	{
//		BlockInfo[] blocks = structure.getBlocks();
//
//		for (int i = 0; i < blocks.length; i++)
//		{
//			blocks[i] = new BlockInfo(blocks[i].data().clone(), new Vector3i(blocks[i].position()));
//		}

		final Vector3i size = structure.getSize().add(1, 1, 1);
		final Vector3d center = new Vector3d(size.x * 0.5, size.y * 0.5, size.z * 0.5);
		final Vector3d centerOffset = new Vector3d(-center.x, -center.y, -center.z);
		final World world = location.getWorld();
		assert world != null;

		BlockStructureEntity blockStructure = new BlockStructureEntity(world.spawn(location, ItemDisplay.class));

		for (int i = 0; i < structure.getBlocks().length; i++)
		{
			BlockInfo block = structure.getBlocks()[i];
			if (block.data().getMaterial().isAir()) continue;

			blockStructure.addDisplay(BlockDisplay.class, entity ->
			{
				Vector3i position = block.position();
				entity.setBlock(block.data().clone());
				entity.setTransformation(
					new Transformation(
						new Vector3f((float) centerOffset.x + position.x, (float) centerOffset.y + position.y, (float) centerOffset.z + position.z),
						new Quaternionf(),
						new Vector3f(1, 1, 1),
						new Quaternionf()));
			});
		}

//		Entity rootEntity = world.spawn(location, BlockDisplay.class, root ->
//		{
//			BlockInfo rootInfo = blocks[0];
//			blocks[0] = null;
//
//			root.setBlock(rootInfo.data());
//			root.setTransformation(
//				new Transformation(
//					new Vector3f((float) centerOffset.x, (float) centerOffset.y, (float) centerOffset.z),
//					new Quaternionf(),
//					new Vector3f(1, 1, 1),
//					new Quaternionf()));
//			NBT nbt = PdcNBT.fromPDC(root.getPersistentDataContainer());
//			nbt.set3f("original_translation", root.getTransformation().getTranslation());
//
//			for (BlockInfo block : blocks)
//			{
//				if (block == null) continue;
//				if (block.data().getMaterial().isAir()) continue;
//
//				BlockDisplay passenger = world.spawn(location, BlockDisplay.class, e ->
//				{
//					Vector3i position = block.position();
//					e.setBlock(block.data());
//					e.setTransformation(
//						new Transformation(
//							new Vector3f((float) centerOffset.x + position.x, (float) centerOffset.y + position.y, (float) centerOffset.z + position.z),
//							new Quaternionf(),
//							new Vector3f(1, 1, 1),
//							new Quaternionf()));
//
//					NBT nbt_ = PdcNBT.fromPDC(e.getPersistentDataContainer());
//					nbt_.set3f("original_translation", e.getTransformation().getTranslation());
//				});
//				root.addPassenger(passenger);
//			}
//		});

		return blockStructure;
	}
}
