package steve6472.funnylib.entity.display;

import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.funnylib.item.builtin.IcoSphereCreator;
import steve6472.funnylib.util.Pair;

import java.util.*;

/**
 * Created by steve6472
 * Date: 1/27/2024
 * Project: StevesFunnyLibrary <br>
 */
public class SphereFrameDisplayEntity extends AdjustableDisplayEntity
{
	private float radius;
	private float width;
	private Quaternionf rotation = new Quaternionf();
	public int ticksAlive;

	public SphereFrameDisplayEntity(Location location, float radius)
	{
		super(Objects.requireNonNull(location.getWorld()).spawn(location, ItemDisplay.class));
		this.radius = radius;
		interpolationDuration = 3;
		create();
	}

	private void create()
	{
		IcoSphereCreator.MeshGeometry3D meshGeometry3D = new IcoSphereCreator().create(1);
		List<Pair<Vector3f, Vector3f>> lines = getPairs(meshGeometry3D);

		lines = deduplicateLines(lines);

		for (Pair<Vector3f, Vector3f> pair : lines)
		{
			createCenteredPart(FrameDisplayEntity.FrameType.AQUA_MARINE.createHead(), t ->
			{
				AdjustableDisplayEntity.transformSlabToLine(
					t,
					rotation.transform(new Vector3f(pair.a()).mul(radius)).add(0, 0.5f, 0),
					rotation.transform(new Vector3f(pair.b()).mul(radius)).add(0, 0.5f, 0),
					width);
			});
		}
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
		update();
	}

	public void setWidth(float width)
	{
		this.width = width;
		update();
	}

	public void setRotation(Quaternionf rotation)
	{
		this.rotation = rotation;
		update();
	}

	public Quaternionf getRotation()
	{
		return new Quaternionf(rotation);
	}

	@Override
	public void tick()
	{
		super.tick();
		ticksAlive++;
	}

	public void setInterpolationDuration(int interpolationDuration)
	{
		this.interpolationDuration = interpolationDuration;
	}




	@NotNull
	private static List<Pair<Vector3f, Vector3f>> getPairs(IcoSphereCreator.MeshGeometry3D meshGeometry3D)
	{
		List<Pair<Vector3f, Vector3f>> lines = new ArrayList<>();

		for (int i = 0; i < meshGeometry3D.triangleIndices.size() / 3; i++)
		{
			int i1 = meshGeometry3D.triangleIndices.get(i * 3);
			int i2 = meshGeometry3D.triangleIndices.get(i * 3 + 1);
			int i3 = meshGeometry3D.triangleIndices.get(i * 3 + 2);

			List<Vector3f> p = meshGeometry3D.positions;

			lines.add(new Pair<>(p.get(i1), p.get(i2)));
			lines.add(new Pair<>(p.get(i2), p.get(i3)));
			lines.add(new Pair<>(p.get(i3), p.get(i1)));
		}
		return lines;
	}

	public static List<Pair<Vector3f, Vector3f>> deduplicateLines(List<Pair<Vector3f, Vector3f>> lines) {
		Set<Pair<Vector3f, Vector3f>> uniqueLines = new HashSet<>();
		List<Pair<Vector3f, Vector3f>> deduplicatedLines = new ArrayList<>();

		for (Pair<Vector3f, Vector3f> line : lines) {
			// Sort the vectors within the pair to handle swapped arguments
			Pair<Vector3f, Vector3f> sortedLine = sortPair(line);

			// If the sorted line is not in the set, add it to both the set and the deduplicated list
			if (uniqueLines.add(sortedLine)) {
				deduplicatedLines.add(line);
			}
		}

		return deduplicatedLines;
	}

	public static Pair<Vector3f, Vector3f> sortPair(Pair<Vector3f, Vector3f> pair) {
		// Sort the vectors within the pair and return a new pair
		Vector3f first = pair.a();
		Vector3f second = pair.b();

		if (compareVectors(first, second) > 0) {
			return new Pair<>(second, first);
		} else {
			return new Pair<>(first, second);
		}
	}

	public static int compareVectors(Vector3f v1, Vector3f v2) {
		// Compare vectors based on their components
		int cmpX = Float.compare(v1.x, v2.x);
		int cmpY = Float.compare(v1.y, v2.y);
		int cmpZ = Float.compare(v1.z, v2.z);

		// Compare each component in order
		if (cmpX != 0) {
			return cmpX;
		} else if (cmpY != 0) {
			return cmpY;
		} else {
			return cmpZ;
		}
	}
}
