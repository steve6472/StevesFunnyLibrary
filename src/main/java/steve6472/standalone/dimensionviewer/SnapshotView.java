package steve6472.standalone.dimensionviewer;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockSupport;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Vector3d;
import org.joml.Vector3f;
import steve6472.funnylib.util.Pair;
import steve6472.funnylib.util.ParticleUtil;
import steve6472.funnylib.util.Preconditions;
import steve6472.standalone.interactable.ReflectionHacker;

import java.util.*;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 10/13/2023
 * Project: StevesFunnyLibrary <br>
 */
public class SnapshotView
{
	private final UUID viewerWorld, viewedWorld;
	private final UUID viewer;
	private final Vector3d origin;
	private final Function<Vector3d, Vector3d> viewerToViewed;
	private final int size;
	private final double ratio;
	private final List<Pair<Vector3d, Vector3d>> portalLinks = new ArrayList<>();
	private final Vector3d lastViewLocation = new Vector3d();

	private Entity anchor;

	public SnapshotView(int size, double ratio, Player viewer, World viewedWorld, Function<Vector3d, Vector3d> viewerToViewed)
	{
		this.size = size;
		this.viewerWorld = viewer.getWorld().getUID();
		this.viewedWorld = viewedWorld.getUID();
		this.viewer = viewer.getUniqueId();
		this.viewerToViewed = viewerToViewed;
		this.ratio = ratio;

		Location location = viewer.getLocation();
		this.origin = new Vector3d(location.getX(), location.getY(), location.getZ());

		setupView();
	}

	public void tick(boolean move)
	{
		Player viewer = Bukkit.getPlayer(this.viewer);
		Preconditions.checkNotNull(viewer, "Viewer not found");

		Vector3d viewerLocation = new Vector3d();

		if (move)
		{
			viewerLocation.set(viewer.getLocation().getX(), viewer.getLocation().getY(), viewer.getLocation().getZ());
			viewerLocation.sub(origin);
			viewerLocation.mul(1 - ratio);

			ReflectionHacker.callEntityMoveTo(anchor, origin.x + viewerLocation.x, origin.y, origin.z + viewerLocation.z, 0, 0);
			lastViewLocation.set(viewerLocation);
		} else
		{
			viewerLocation.set(lastViewLocation);
		}

		for (Pair<Vector3d, Vector3d> portalLink : portalLinks)
		{
			Vector3d viewedWorld = portalLink.a();
			Vector3d viewerWorld = portalLink.b();

			double X = origin.x + viewerLocation.x + (viewedWorld.x - origin.x * ratio);
			double Z = origin.z + viewerLocation.z + (viewedWorld.z - origin.z * ratio);

			ParticleUtil.line(
				viewer.getWorld(),
				Particle.REDSTONE,

				X + 0.5,
				viewedWorld.y + 0.5,
				Z + 0.5,

				viewerWorld.x,
				viewerWorld.y,
				viewerWorld.z,

				0, 1, new Particle.DustOptions(Color.FUCHSIA, 0.5f));
//			Bukkit.broadcastMessage("X: " + X + ", Y: " + viewedWorld.y + ", Z: " + Z);
		}
	}

	private void setupView()
	{
		List<Location> portalBlocks = new ArrayList<>();

		anchor = createAnchor();
		copyWorld(portalBlocks);

		List<List<Vector3d>> lists = groupPortals(portalBlocks);

		for (List<Vector3d> list : lists)
		{
			linkPortals(list);
		}
	}

	private List<List<Vector3d>> groupPortals(List<Location> portals)
	{
		List<Vector3d> voxels = portals.stream().map(loc -> new Vector3d(loc.getX(), loc.getY(), loc.getZ())).toList();
		List<List<Vector3d>> voxelGroups = groupVoxels(voxels);

		List<List<Vector3d>> editedCoordinates = new ArrayList<>();

		for (List<Vector3d> voxelGroup : voxelGroups)
		{
//			Vector3d minCoordinate = voxelGroup.stream()
//				.min(Comparator.comparing((Vector3d v) -> v.x)
//					.thenComparing(v -> v.y)
//					.thenComparing(v -> v.z))
//				.orElseThrow();
//
//			voxelGroup.remove(minCoordinate);
//
//			List<Vector3d> newCoords = new ArrayList<>();
//			newCoords.add(minCoordinate);
//
//			newCoords.addAll(voxelGroup);

			editedCoordinates.add(voxelGroup);
		}

		return editedCoordinates;
	}

	private static Vector3d calculateCenter(List<Vector3d> voxelGroup)
	{
		double sumX = 0, sumY = 0, sumZ = 0;
		int size = voxelGroup.size();

		for (Vector3d vector3d : voxelGroup) {
			sumX += vector3d.x;
			sumY += vector3d.y;
			sumZ += vector3d.z;
		}

		return new Vector3d(sumX / size, sumY / size, sumZ / size);
	}

	private static boolean areNeighbors(Vector3d v1, Vector3d v2)
	{
		return Math.abs(v1.x - v2.x) <= 1 && Math.abs(v1.y - v2.y) <= 1 && Math.abs(v1.z - v2.z) <= 1;
	}

	private static List<List<Vector3d>> groupVoxels(List<Vector3d> voxels)
	{
		List<List<Vector3d>> groups = new ArrayList<>();
		for (Vector3d voxel : voxels)
		{
			boolean added = false;
			for (List<Vector3d> group : groups)
			{
				for (Vector3d groupVoxel : group)
				{
					if (areNeighbors(voxel, groupVoxel))
					{
						group.add(voxel);
						added = true;
						break;
					}
				}
				if (added)
					break;
			}
			if (!added)
			{
				List<Vector3d> newGroup = new ArrayList<>();
				newGroup.add(voxel);
				groups.add(newGroup);
			}
		}
		return groups;
	}

	private void linkPortals(List<Vector3d> portalBlocks)
	{
		World viewerWorld = Bukkit.getWorld(this.viewerWorld);
		Preconditions.checkNotNull(viewerWorld, "Viewer not found");

		World viewedWorld = Bukkit.getWorld(this.viewedWorld);
		Preconditions.checkNotNull(viewedWorld, "Viewed not found");

		for (Vector3d viewedWorldBlock : portalBlocks)
		{
			Location location = new Location(viewedWorld, viewedWorldBlock.x * (1d / ratio), viewedWorldBlock.y, viewedWorldBlock.z * (1d / ratio));
			Location viewerWorldBlock = PortalLinkFinder.findPortal(location, viewerWorld);
			if (viewerWorldBlock != null)
			{
//				Bukkit.broadcastMessage("Link found: %s, %s, %s <-> %s, %s, %s".formatted(viewedWorldBlock.getX(), viewedWorldBlock.getY(), viewedWorldBlock.getZ(), viewerWorldBlock.getX(), viewerWorldBlock.getY(), viewerWorldBlock.getZ()));
				portalLinks.add(new Pair<>(
					new Vector3d(viewedWorldBlock),
					new Vector3d(viewerWorldBlock.getX() + 0.5, viewerWorldBlock.getY() + 0.5, viewerWorldBlock.getZ() + 0.5)
				));
			}
		}
	}

	private void copyWorld(List<Location> portalBlocks)
	{
		World viewedWorld = Bukkit.getWorld(this.viewedWorld);
		Preconditions.checkNotNull(viewedWorld, "Viewed not found");

		World viewerWorld = Bukkit.getWorld(this.viewerWorld);
		Preconditions.checkNotNull(viewerWorld, "Viewer not found");

		Vector3d center = viewerToViewed.apply(origin).floor();
		Location originLocation = new Location(viewerWorld, origin.x, origin.y, origin.z);

		Set<Pair<Chunk, Boolean>> chunks = new HashSet<>();

		for (int i = ((int) center.x - size - 1) >> 4; i < ((int) center.x + size + 1) >> 4; i++)
		{
			for (int j = ((int) center.z - size - 1) >> 4; j < ((int) center.z + size + 1) >> 4; j++)
			{
				Chunk chunkAt = viewedWorld.getChunkAt(i, j);
				chunkAt.load();
				boolean lastForceState = chunkAt.isForceLoaded();
				chunkAt.setForceLoaded(true);
				chunks.add(new Pair<>(chunkAt, lastForceState));
			}
		}

		BlockData[][][] data = new BlockData[size * 2 + 1][size * 2 + 1][size * 2 + 1];

		int maxSize = size * 2 + 1;

		for (int i = 0; i < maxSize; i++)
		{
			for (int j = 0; j < maxSize; j++)
			{
				for (int k = 0; k < maxSize; k++)
				{
					int x = ((int) center.x) + i - size;
					int y = ((int) center.y) + j - size;
					int z = ((int) center.z) + k - size;
					Block blockAt = viewedWorld.getBlockAt(x, y, z);

					data[i][j][k] = blockAt.getBlockData();
				}
			}
		}

		chunks.forEach(c -> c.a().setForceLoaded(c.b()));


		for (int i = 0; i < maxSize; i++)
		{
			for (int j = 0; j < maxSize; j++)
			{
				for (int k = 0; k < maxSize; k++)
				{
					BlockData blockData = data[i][j][k];

					// If not on edge
					if (i != 0 && j != 0 && k != 0 && i != maxSize - 1 && j != maxSize - 1 && k != maxSize - 1)
					{
						if (!shouldBeVisible(data, i, j, k)) continue;
					}

					if (blockData.getMaterial() == Material.NETHER_PORTAL)
					{
						portalBlocks.add(new Location(viewedWorld, ((int) center.x) + i - size, ((int) center.y) + j - size, ((int) center.z) + k - size));
					}

					int finalI = i, finalJ = j, finalK = k;
					BlockDisplay display = viewerWorld.spawn(originLocation, BlockDisplay.class, e ->
					{
						e.setBlock(blockData);
						e.setDisplayHeight(size);
						e.setDisplayWidth(size);

						Transformation transformation = e.getTransformation();
						Vector3f translation = transformation.getTranslation();

						translation.set(
							finalI - size - fract(origin.x * ratio),
							finalJ - size - fract(origin.y),
							finalK - size - fract(origin.z * ratio)
						);

						e.setTransformation(new Transformation(translation, transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation()));
					});
					anchor.addPassenger(display);
				}
			}
		}
	}

	private static boolean shouldBeVisible(BlockData[][][] data, int x, int y, int z)
	{
		BlockData blockData = data[x][y][z];

		if (blockData.getMaterial().isAir()) return false;

		return !(
			(blockData.isFaceSturdy(BlockFace.UP, BlockSupport.FULL)    && data[x][y + 1][z].isFaceSturdy(BlockFace.DOWN, BlockSupport.FULL))  &&
			(blockData.isFaceSturdy(BlockFace.DOWN, BlockSupport.FULL)  && data[x][y - 1][z].isFaceSturdy(BlockFace.UP, BlockSupport.FULL))    &&
			(blockData.isFaceSturdy(BlockFace.EAST, BlockSupport.FULL)  && data[x + 1][y][z].isFaceSturdy(BlockFace.WEST, BlockSupport.FULL))  &&
			(blockData.isFaceSturdy(BlockFace.WEST, BlockSupport.FULL)  && data[x - 1][y][z].isFaceSturdy(BlockFace.EAST, BlockSupport.FULL))  &&
			(blockData.isFaceSturdy(BlockFace.NORTH, BlockSupport.FULL) && data[x][y][z - 1].isFaceSturdy(BlockFace.SOUTH, BlockSupport.FULL)) &&
			(blockData.isFaceSturdy(BlockFace.SOUTH, BlockSupport.FULL) && data[x][y][z + 1].isFaceSturdy(BlockFace.NORTH, BlockSupport.FULL))
		);
	}

	private static double fract(double num)
	{
		long iPart;
		double fPart;

		iPart = (long) num;
		fPart = num - iPart;

		if (!(num > 0))
			fPart = fPart + 1.0;

		return fPart;
	}

	private Entity createAnchor()
	{
		World world = Bukkit.getWorld(this.viewerWorld);
		Preconditions.checkNotNull(world, "Viewer not found");

		return world.spawn(new Location(world, origin.x, origin.y, origin.z), ArmorStand.class, e ->
		{
			e.setMarker(true);
			e.setSmall(true);
			e.setInvisible(true);
		});
	}

	public void delete()
	{
		anchor.getPassengers().forEach(Entity::remove);
		anchor.remove();
	}
}
