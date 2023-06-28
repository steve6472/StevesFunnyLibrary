package steve6472.funnylib.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.util.Transformation;
import org.joml.*;
import steve6472.funnylib.data.BlockInfo;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.serialize.PdcNBT;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 6/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BlockStructureEntity
{
	WeakReference<Entity> rootEntity;

	/**
	 * Transformation for the whole "structure"
	 */
	private final Transformation transformation = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1), new Quaternionf());

	private BlockStructureEntity()
	{

	}

	public static BlockStructureEntity createFromStructure(Location location, GameStructure structure)
	{
		BlockInfo[] blocks = structure.getBlocks();

		BlockStructureEntity entity = new BlockStructureEntity();

		final Vector3i size = structure.getSize().add(1, 1, 1);
		final Vector3d center = new Vector3d(size.x * 0.5, size.y * 0.5, size.z * 0.5);
		final Vector3d centerOffset = new Vector3d(-center.x, -center.y, -center.z);
		final World world = location.getWorld();
		assert world != null;

		entity.rootEntity = new WeakReference<>(world.spawn(location, BlockDisplay.class, root ->
		{
			BlockInfo rootInfo = blocks[0];
			blocks[0] = null;

			root.setBlock(rootInfo.data());
			root.setTransformation(
				new Transformation(
					new Vector3f((float) centerOffset.x, (float) centerOffset.y, (float) centerOffset.z),
					new Quaternionf(),
					new Vector3f(1, 1, 1),
					new Quaternionf()));
			NBT nbt = PdcNBT.fromPDC(root.getPersistentDataContainer());
			nbt.set3f("original_translation", root.getTransformation().getTranslation());

			for (BlockInfo block : blocks)
			{
				if (block == null) continue;
				if (block.data().getMaterial().isAir()) continue;

				BlockDisplay passenger = world.spawn(location, BlockDisplay.class, e ->
				{
					Vector3i position = block.position();
					e.setBlock(block.data());
					e.setTransformation(
						new Transformation(
							new Vector3f((float) centerOffset.x + position.x, (float) centerOffset.y + position.y, (float) centerOffset.z + position.z),
							new Quaternionf(),
							new Vector3f(1, 1, 1),
							new Quaternionf()));

					NBT nbt_ = PdcNBT.fromPDC(e.getPersistentDataContainer());
					nbt_.set3f("original_translation", e.getTransformation().getTranslation());
				});
				root.addPassenger(passenger);
			}
		}));

		return entity;
	}

	public void iterateAll(Consumer<BlockDisplay> function)
	{
		Entity root = rootEntity.get();
		if (root == null) return;

		if (root instanceof BlockDisplay rootDisplay)
			function.accept(rootDisplay);

		for (Entity passenger : root.getPassengers())
		{
			if (!(passenger instanceof BlockDisplay display)) continue;
			function.accept(display);
		}
	}

	public void scale(double newScale, int delay, int duration)
	{
		iterateAll(d ->
		{
			NBT nbt = PdcNBT.fromPDC(d.getPersistentDataContainer());
			Vector3f originalTranslation = nbt.get3f("original_translation", new Vector3f());
			transformation.getLeftRotation().transform(originalTranslation);

			Transformation transformation = d.getTransformation();
			transformation.getScale().set(newScale);
			transformation.getTranslation().set(originalTranslation.mul((float) newScale));
			d.setTransformation(transformation);
			d.setInterpolationDelay(delay);
			d.setInterpolationDuration(duration);
		});

		transformation.getScale().set(newScale);
	}

	public void rotate(Quaternionf quat, int delay, int duration)
	{
		iterateAll(d ->
		{
			Transformation transformation = d.getTransformation();
			transformation.getLeftRotation().set(quat);
			Vector3f originalTranslation = PdcNBT.fromPDC(d.getPersistentDataContainer()).get3f("original_translation", new Vector3f());
			originalTranslation.mul(transformation.getScale());
			transformation.getLeftRotation().transform(originalTranslation);
			transformation.getTranslation().set(originalTranslation);
			d.setTransformation(transformation);
			d.setInterpolationDelay(delay);
			d.setInterpolationDuration(duration);
		});

		transformation.getLeftRotation().set(quat);
	}
}
