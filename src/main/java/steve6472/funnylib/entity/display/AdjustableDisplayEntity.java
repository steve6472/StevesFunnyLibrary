package steve6472.funnylib.entity.display;

import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class AdjustableDisplayEntity extends MultiDisplayEntity
{
	protected final Map<UUID, Consumer<Transformation>> partBehaviour = new HashMap<>();
	protected int interpolationDuration;

	public AdjustableDisplayEntity(Entity root)
	{
		super(root);
	}

	public void update()
	{
		iteratePassengers(ent ->
		{
			Transformation transformation = ent.getTransformation();
			Consumer<Transformation> transformationConsumer = partBehaviour.get(ent.getUniqueId());
			if (transformationConsumer != null)
			{
				transformationConsumer.accept(transformation);
				ent.setInterpolationDelay(0);
				ent.setInterpolationDuration(interpolationDuration);
				ent.setTransformation(transformation);
			}
		});
	}

	public void createCenteredPart(ItemStack itemStack, Consumer<Transformation> partBehavior)
	{
		addDisplay(ItemDisplay.class, entity ->
		{
			entity.setItemStack(itemStack);
			Transformation transformation = entity.getTransformation();
			partBehavior.accept(transformation);
			entity.setTransformation(transformation);
			entity.setBrightness(new Display.Brightness(15, 15));
			this.partBehaviour.put(entity.getUniqueId(), partBehavior);
		});
	}

	public void addPart(Display display, Consumer<Transformation> partBehavior)
	{
		this.partBehaviour.put(display.getUniqueId(), partBehavior);
	}

	public static void transformBlockToLine(Transformation transformation, Vector3f point1, Vector3f point2, float width)
	{
		// Calculate the direction vector between the two points
		Vector3f direction = new Vector3f(point2).sub(point1).normalize();

		// Calculate the rotation quaternion to align the cube with the direction vector
		Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(0, 1, 0), direction);

		// Calculate the scale factor to stretch the cube into a line
		float scale = point1.distance(point2);

		// Calculate the translation vector to move the center of the cube to the midpoint of the line
		Vector3f translation = new Vector3f(point1).add(point2).mul(0.5f);

		transformation.getTranslation().set(translation);
		transformation.getTranslation().add(0, -0.5f, 0);
		transformation.getLeftRotation().set(rotation);
		transformation.getScale().set(width, scale, width);
	}

	// Skulls are basically "slabs"
	public static void transformSlabToLine(Transformation transformation, Vector3f point1, Vector3f point2, float width)
	{
		// Calculate the direction vector between the two points
		Vector3f direction = new Vector3f(point2).sub(point1).normalize();

		// Calculate the rotation quaternion to align the cube with the direction vector
		Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(0, 1, 0), direction);

		// Calculate the scale factor to stretch the cube into a line
		float scale = point1.distance(point2);

		transformation.getTranslation().set(point2);
		transformation.getTranslation().add(0, -0.5f, 0);
		transformation.getLeftRotation().set(rotation);
		transformation.getScale().set(width, scale * 2f, width);
	}
}
