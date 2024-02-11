package steve6472.funnylib.entity.display;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.data.BlockInfo;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.util.Preconditions;
import steve6472.funnylib.workdistro.impl.RunnableWorkload;

import java.util.*;

/**
 * Created by steve6472
 * Date: 12/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class StructureHighlightEntity extends AdjustableDisplayEntity
{
	private static final int ALPHA = 63;

	private static final HashMap<BlockFace, Vector3f> TRANSLATIONS = new HashMap<>(6);
	private static final HashMap<BlockFace, Vector3f> SCALES = new HashMap<>(6);
	private static final HashMap<BlockFace, Quaternionf> ROTATIONS = new HashMap<>(6);
	private static final HashMap<BlockFace, Color> COLORS = new HashMap<>(6);
	private static final HashMap<BlockFace, Float> SHADING = new HashMap<>(6);
	private static final HashMap<BlockFace, Offsets> OFFSETS = new HashMap<>(6);

	static
	{
		Color shieldColor = Color.fromRGB(100, 233, 233);

		TRANSLATIONS.put(BlockFace.UP, new Vector3f(-0.5f, 0.5f, 0.5f));
		SCALES      .put(BlockFace.UP, new Vector3f(40, 40f, 1));
		ROTATIONS   .put(BlockFace.UP, new Quaternionf().rotateAxis((float) (Math.PI * -0.5f), 1, 0, 0));
		COLORS      .put(BlockFace.UP, shieldColor);
		SHADING     .put(BlockFace.UP, 1f);
		OFFSETS     .put(BlockFace.UP, (blockOffset, expUp, expRight, expDown, expLeft, translation, scale) -> {
			if (expUp) scale.add(0, blockOffset * 40f, 0);
			if (expRight) scale.add(blockOffset * 40f, 0, 0);
			if (expLeft) translation.add(-blockOffset, 0, 0);
			if (expLeft) scale.add(blockOffset * 40f, 0, 0);
			if (expDown) translation.add(0, 0, blockOffset);
			if (expDown) scale.add(0, blockOffset * 40f, 0);
		});

		TRANSLATIONS.put(BlockFace.DOWN, new Vector3f(-0.5f, -0.5f, -0.5f));
		SCALES      .put(BlockFace.DOWN, new Vector3f(40, 40f, 1));
		ROTATIONS   .put(BlockFace.DOWN, new Quaternionf().rotateAxis((float) (Math.PI * 0.5f), 1, 0, 0));
		COLORS      .put(BlockFace.DOWN, shieldColor);
		SHADING     .put(BlockFace.DOWN, 0.5f);
		OFFSETS     .put(BlockFace.DOWN, (blockOffset, expUp, expRight, expDown, expLeft, translation, scale) -> {
			if (expUp) scale.add(0, blockOffset * 40f, 0);
			if (expRight) scale.add(blockOffset * 40f, 0, 0);
			if (expLeft) translation.add(-blockOffset, 0, 0);
			if (expLeft) scale.add(blockOffset * 40f, 0, 0);
			if (expDown) translation.add(0, 0, -blockOffset);
			if (expDown) scale.add(0, blockOffset * 40f, 0);
		});

		TRANSLATIONS.put(BlockFace.NORTH, new Vector3f(0.5f, -0.5f, -0.5f));
		SCALES      .put(BlockFace.NORTH, new Vector3f(40, 40f, 1));
		ROTATIONS   .put(BlockFace.NORTH, new Quaternionf().rotateAxis((float) Math.PI, 0, 1, 0));
		COLORS      .put(BlockFace.NORTH, shieldColor);
		SHADING     .put(BlockFace.NORTH, 0.8f);
		OFFSETS     .put(BlockFace.NORTH, (blockOffset, expUp, expRight, expDown, expLeft, translation, scale) -> {
			if (expUp) scale.add(0, blockOffset * 40f, 0);
			if (expRight) scale.add(blockOffset * 40f, 0, 0);
			if (expLeft) translation.add(blockOffset, 0, 0);
			if (expLeft) scale.add(blockOffset * 40f, 0, 0);
			if (expDown) translation.add(0, -blockOffset, 0);
			if (expDown) scale.add(0, blockOffset * 40f, 0);
		});

		TRANSLATIONS.put(BlockFace.EAST, new Vector3f(0.5f, -0.5f, 0.5f));
		SCALES      .put(BlockFace.EAST, new Vector3f(40, 40f, 1));
		ROTATIONS   .put(BlockFace.EAST, new Quaternionf().rotateAxis((float) (Math.PI * 0.5f), 0, 1, 0));
		COLORS      .put(BlockFace.EAST, shieldColor);
		SHADING     .put(BlockFace.EAST, 0.6f);
		OFFSETS     .put(BlockFace.EAST, (blockOffset, expUp, expRight, expDown, expLeft, translation, scale) -> {
			if (expUp) scale.add(0, blockOffset * 40f, 0);
			if (expRight) scale.add(blockOffset * 40f, 0, 0);
			if (expLeft) translation.add(0, 0, blockOffset);
			if (expLeft) scale.add(blockOffset * 40f, 0, 0);
			if (expDown) translation.add(0, -blockOffset, 0);
			if (expDown) scale.add(0, blockOffset * 40f, 0);
		});

		TRANSLATIONS.put(BlockFace.SOUTH, new Vector3f(-0.5f, -0.5f, 0.5f));
		SCALES      .put(BlockFace.SOUTH, new Vector3f(40, 40f, 1));
		ROTATIONS   .put(BlockFace.SOUTH, new Quaternionf());
		COLORS      .put(BlockFace.SOUTH, shieldColor);
		SHADING     .put(BlockFace.SOUTH, 0.8f);
		OFFSETS     .put(BlockFace.SOUTH, (blockOffset, expUp, expRight, expDown, expLeft, translation, scale) -> {
			if (expUp) scale.add(0, blockOffset * 40f, 0);
			if (expRight) scale.add(blockOffset * 40f, 0, 0);
			if (expLeft) translation.add(-blockOffset, 0, 0);
			if (expLeft) scale.add(blockOffset * 40f, 0, 0);
			if (expDown) translation.add(0, -blockOffset, 0);
			if (expDown) scale.add(0, blockOffset * 40f, 0);
		});

		TRANSLATIONS.put(BlockFace.WEST, new Vector3f(-0.5f, -0.5f, -0.5f));
		SCALES      .put(BlockFace.WEST, new Vector3f(40, 40f, 1));
		ROTATIONS   .put(BlockFace.WEST, new Quaternionf().rotateAxis((float) (Math.PI * -0.5f), 0, 1, 0));
		COLORS      .put(BlockFace.WEST, shieldColor);
		SHADING     .put(BlockFace.WEST, 0.6f);
		OFFSETS     .put(BlockFace.WEST, (blockOffset, expUp, expRight, expDown, expLeft, translation, scale) -> {
			if (expUp) scale.add(0, blockOffset * 40f, 0);
			if (expRight) scale.add(blockOffset * 40f, 0, 0);
			if (expLeft) translation.add(0, 0, -blockOffset);
			if (expLeft) scale.add(blockOffset * 40f, 0, 0);
			if (expDown) translation.add(0, -blockOffset, 0);
			if (expDown) scale.add(0, blockOffset * 40f, 0);
		});

		// Debug colors
/*
		COLORS      .put(BlockFace.UP, Color.fromRGB(0, 255, 0));
		COLORS      .put(BlockFace.DOWN, Color.fromRGB(0, 255, 0));
		COLORS      .put(BlockFace.NORTH, Color.fromRGB(0, 0, 255));
		COLORS      .put(BlockFace.EAST, Color.fromRGB(255, 0, 0));
		COLORS      .put(BlockFace.SOUTH, Color.fromRGB(0, 0, 255));
		COLORS      .put(BlockFace.WEST, Color.fromRGB(255, 0, 0));*/
	}

	private float blockOffset;

	public StructureHighlightEntity(@NotNull Player owner, Location location, GameStructure structure, float blockOffset)
	{
		super(Objects.requireNonNull(location.getWorld()).spawn(location, ItemDisplay.class));
		Preconditions.checkNotNull(owner);
		interpolationDuration = 3;
		this.blockOffset = blockOffset;
		create(structure);
	}

	protected void create(GameStructure structure)
	{
		final Vector3i size = structure.getSize().add(1, 1, 1);
		final Vector3d center = new Vector3d(size.x * 0.5, size.y * 0.5, size.z * 0.5);
		final Vector3d centerOffset = new Vector3d(-center.x, -center.y, -center.z);
		BlockInfo[] blocks = structure.getBlocks();

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
			int I = i;
			FunnyLib.getWorkloadRunnable().addWorkload(new RunnableWorkload(() -> {
			for (int j = 0; j < height; j++)
			{
				int J = j;
				FunnyLib.getWorkloadRunnable().addWorkload(new RunnableWorkload(() -> {
				for (int k = 0; k < depth; k++)
				{
					Vector3i curr = new Vector3i(I, J, k);

					BlockInfo info = dataMap.get(new Vector3i(I, J, k));

					if (info.data().getMaterial().isAir()) continue;
					if (info.data().getMaterial() == Material.BARRIER) continue;

					boolean spawnUp = J == height - 1 || shouldSpawnFace(dataMap, curr.add(0, 1, 0, new Vector3i()));
					boolean spawnDown = J == 0 || shouldSpawnFace(dataMap, curr.add(0, -1, 0, new Vector3i()));
					boolean spawnNorth = k == 0 || shouldSpawnFace(dataMap, curr.add(0, 0, -1, new Vector3i()));
					boolean spawnEast = I == width - 1 || shouldSpawnFace(dataMap, curr.add(1, 0, 0, new Vector3i()));
					boolean spawnSouth = k == depth - 1 || shouldSpawnFace(dataMap, curr.add(0, 0, 1, new Vector3i()));
					boolean spawnWest = I == 0 || shouldSpawnFace(dataMap, curr.add(-1, 0, 0, new Vector3i()));

					FunnyLib.getWorkloadRunnable().addWorkload(new RunnableWorkload(() ->
					{
						Vector3i position = info.position();

						if (spawnUp) createDisplay(centerOffset, position, BlockFace.UP, spawnNorth, spawnEast, spawnSouth, spawnWest);
						if (spawnDown) createDisplay(centerOffset, position, BlockFace.DOWN, spawnSouth, spawnEast, spawnNorth, spawnWest);
						if (spawnNorth) createDisplay(centerOffset, position, BlockFace.NORTH, spawnUp, spawnWest, spawnDown, spawnEast);
						if (spawnEast) createDisplay(centerOffset, position, BlockFace.EAST, spawnUp, spawnNorth, spawnDown, spawnSouth);
						if (spawnSouth) createDisplay(centerOffset, position, BlockFace.SOUTH, spawnUp, spawnEast, spawnDown, spawnWest);
						if (spawnWest) createDisplay(centerOffset, position, BlockFace.WEST, spawnUp, spawnSouth, spawnDown, spawnNorth);
					}));
				}
				}));
			}
			}));
		}
	}

	private boolean shouldSpawnFace(Map<Vector3i, BlockInfo> dataMap, Vector3i pos)
	{
		BlockInfo blockInfo = dataMap.get(pos);
		return blockInfo == null || blockInfo.data().getMaterial().isAir();
	}

	public void setBlockOffset(float offset)
	{
		this.blockOffset = offset;
		update();
	}

	private void createDisplay(Vector3d centerOffset, Vector3i position, BlockFace face, boolean expUp, boolean expRight, boolean expDown, boolean expLeft)
	{
		addDisplay(TextDisplay.class, entity -> {
			Color color = COLORS.get(face);
			float shading = SHADING.get(face);

			entity.setText("");
			entity.setDefaultBackground(false);
			entity.setBackgroundColor(Color.fromARGB(ALPHA, ((int) (color.getRed() * shading)), ((int) (color.getGreen() * shading)), ((int) (color.getBlue() * shading))));
//			entity.setBackgroundColor(Color.fromARGB(modifyColorWithRandomOffsets(color, shading, ALPHA, 200)));
			entity.setBrightness(new Display.Brightness(15, 15));
//			entity.setSeeThrough(true); // TODO: sphere setting ?

			Vector3f startTranslate = new Vector3f((float) centerOffset.x + position.x, (float) centerOffset.y + position.y, (float) centerOffset.z + position.z);
			startTranslate.add(TRANSLATIONS.get(face));
			startTranslate.add(face.getDirection().toVector3f().mul(blockOffset));

			Vector3f startScale = new Vector3f(SCALES.get(face));

			OFFSETS.get(face).apply(blockOffset, expUp, expRight, expDown, expLeft, startTranslate, startScale);

			entity.setTransformation(
				new Transformation(
					startTranslate,
					new Quaternionf(ROTATIONS.get(face)),
					startScale,
					new Quaternionf()));

			addPart(entity, t -> {
				Vector3f translation = t.getTranslation();
				Vector3f scale = t.getScale();

				// Reset back to default
				translation.set((float) centerOffset.x + position.x, (float) centerOffset.y + position.y, (float) centerOffset.z + position.z);
				translation.add(TRANSLATIONS.get(face));
				scale.set(SCALES.get(face));

				// Basic movement in offset direction
				translation.add(face.getDirection().toVector3f().mul(blockOffset));

				OFFSETS.get(face).apply(blockOffset, expUp, expRight, expDown, expLeft, translation, scale);

				t.getTranslation().set(translation);
				t.getScale().set(scale);
			});
		});
	}

	public static int modifyColorWithRandomOffsets(Color color, double shading, int alpha, int offsetRange) {
		// Define a random number generator
		Random random = new Random();

		// Original color values
		int red = (int) (color.getRed() * shading);
		int green = (int) (color.getGreen() * shading);
		int blue = (int) (color.getBlue() * shading);

		// Introduce small random offsets
		int randomRedOffset = random.nextInt(2 * offsetRange + 1) - offsetRange;
		int randomGreenOffset = random.nextInt(2 * offsetRange + 1) - offsetRange;
		int randomBlueOffset = random.nextInt(2 * offsetRange + 1) - offsetRange;

		// Apply offsets to the color components
		int finalRed = clampColor(red + randomRedOffset);
		int finalGreen = clampColor(green + randomGreenOffset);
		int finalBlue = clampColor(blue + randomBlueOffset);

		// Combine the components into an ARGB color
		return Color.fromARGB(alpha, finalRed, finalGreen, finalBlue).asARGB();
	}

	private static int clampColor(int value) {
		return Math.max(0, Math.min(value, 255));
	}

	private interface Offsets
	{
		void apply(float blockOffset, boolean expUp, boolean expRight, boolean expDown, boolean expLeft, Vector3f translation, Vector3f scale);
	}
}
