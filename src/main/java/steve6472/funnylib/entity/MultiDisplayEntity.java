package steve6472.funnylib.entity;

import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.standalone.interactable.ReflectionHacker;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 12/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class MultiDisplayEntity
{
	WeakReference<Entity> rootEntity;

	public MultiDisplayEntity(Entity root)
	{
		rootEntity = new WeakReference<>(root);
	}

	/**
	 * Transformation for the whole "structure"
	 */
	private final Transformation transformation = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1), new Quaternionf());

	public WeakReference<Entity> getRootEntity()
	{
		return rootEntity;
	}

	public void tick()
	{

	}

	public void move(double x, double y, double z)
	{
		Entity root = rootEntity.get();
		if (root == null) return;
		ReflectionHacker.callEntityMoveTo(root, x, y, z, 0, 0);
	}

	public <T extends Display> void addDisplay(Class<T> clazz, Consumer<T> function)
	{
		Entity root = rootEntity.get();
		if (root == null) return;
		T spawn = root.getWorld().spawn(root.getLocation(), clazz, function::accept);
		NBT nbt = PdcNBT.fromPDC(root.getPersistentDataContainer());
		nbt.set3f("original_translation", spawn.getTransformation().getTranslation());
		root.addPassenger(spawn);
	}

	public void remove()
	{
		Entity root = rootEntity.get();
		if (root == null) return;

		iteratePassengers(Entity::remove);
		root.remove();
	}

	public void iteratePassengers(Consumer<Display> function)
	{
		Entity root = rootEntity.get();
		if (root == null) return;

		for (Entity passenger : root.getPassengers())
		{
			if (!(passenger instanceof Display display)) continue;
			function.accept(display);
		}
	}

	public void iterateAll(Consumer<Display> function)
	{
		Entity root = rootEntity.get();
		if (root == null) return;

		if (root instanceof BlockDisplay rootDisplay)
			function.accept(rootDisplay);

		for (Entity passenger : root.getPassengers())
		{
			if (!(passenger instanceof Display display)) continue;
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
