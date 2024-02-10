package steve6472.funnylib.item.builtin.worldtools.menu;

import org.bukkit.Material;
import org.bukkit.World;
import org.joml.Vector3i;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.funnylib.workdistro.impl.PlaceBlockWorkload;
import steve6472.funnylib.workdistro.impl.PlaceWithWeightedMaterialWorkload;
import steve6472.funnylib.workdistro.impl.ReplaceBlockWorkload;
import steve6472.funnylib.workdistro.impl.ReplaceWithWeightedMaterialWorkload;
import steve6472.funnylib.workdistro.util.WeightedRandomBag;

/**
 * Created by steve6472
 * Date: 2/9/2024
 * Project: StevesFunnyLibrary <br>
 */
public class FillFunctions
{
	private final FillerMenu menu;

	public FillFunctions(FillerMenu menu)
	{
		this.menu = menu;
	}

	public void applySphere(Click click)
	{
		click.player().sendMessage("Normal appply");
		ItemNBT data = menu.nbtFromPlayersHandOrStack(null);
		if (data == null) return;
		PdcNBT protectedData = data.protectedData();
		if (!protectedData.has3i("center")) return;

		int radius = protectedData.getInt("radius", 2);
		boolean matchAnyBlock = protectedData.getBoolean("match_any_block", true);

		Vector3i center = protectedData.get3i("center");

		Material match = Material.matchMaterial(protectedData.getString("match", Material.AIR.toString()));
		Material place = Material.matchMaterial(protectedData.getString("place", Material.AIR.toString()));

		World world = click.player().getWorld();
		for (int i = -radius; i <= radius; i++)
		{
			for (int j = -radius; j <= radius; j++)
			{
				for (int k = -radius; k < radius; k++)
				{
					if (Math.sqrt(i * i + j * j + k * k) > radius - FillerMenu.RADIUS_OFFSET) continue;

					if (matchAnyBlock)
					{
						FunnyLib
							.getWorkloadRunnable()
							.addWorkload(new PlaceBlockWorkload(world, center.x + i, center.y + j, center.z + k, place));
					} else
					{
						FunnyLib
							.getWorkloadRunnable()
							.addWorkload(new ReplaceBlockWorkload(world, center.x + i, center.y + j, center.z + k, match, place));
					}
				}
			}
		}
	}

	public void applyAdvancedSphere(Click click)
	{
		click.player().sendMessage("Advanced appply");
		ItemNBT data = menu.nbtFromPlayersHandOrStack(null);
		if (data == null) return;
		PdcNBT protectedData = data.protectedData();
		if (!protectedData.has3i("center")) return;

		int radius = protectedData.getInt("radius", 2);
		boolean matchAnyBlock = protectedData.getBoolean("match_any_block", true);

		Vector3i center = protectedData.get3i("center");

		Material match = Material.matchMaterial(protectedData.getString("match", Material.AIR.toString()));

		WeightedRandomBag<Material> bag = new WeightedRandomBag<>();

		protectedData.getOrCreateCompound("advanced").getKeys().stream().filter(p -> p.startsWith("slot_")).forEach(key -> {
			String string = protectedData.getOrCreateCompound("advanced").getString(key);
			Material material = Material.matchMaterial(string);
			if (material != null)
			{
				bag.addEntry(material, 1);
			}
		});

		int airCount = protectedData.getOrCreateCompound("advanced").getInt("air_count", 0);
		if (airCount > 0)
		{
			bag.addEntry(Material.AIR, airCount);
		}

		World world = click.player().getWorld();
		for (int i = -radius; i <= radius; i++)
		{
			for (int j = -radius; j <= radius; j++)
			{
				for (int k = -radius; k <= radius; k++)
				{
					if (Math.sqrt(i * i + j * j + k * k) > radius - FillerMenu.RADIUS_OFFSET) continue;

					if (matchAnyBlock)
					{
						FunnyLib
							.getWorkloadRunnable()
							.addWorkload(new PlaceWithWeightedMaterialWorkload(world, center.x + i, center.y + j, center.z + k, bag));
					} else
					{
						FunnyLib
							.getWorkloadRunnable()
							.addWorkload(new ReplaceWithWeightedMaterialWorkload(world, center.x + i, center.y + j, center.z + k, match, bag));
					}
				}
			}
		}
	}

	public void applyRectangle(Click click)
	{
		Vector3i pos1 = new Vector3i();
		Vector3i pos2 = new Vector3i();

		ItemNBT data = menu.nbtFromPlayersHandOrStack(null);
		if (data == null) return;
		PdcNBT protectedData = data.protectedData();

		if (!protectedData.has3i("pos1") || !protectedData.has3i("pos2"))
		{
			return;
		}

		protectedData.get3i("pos1", pos1);
		protectedData.get3i("pos2", pos2);

		Vector3i minPos = pos1.min(pos2, new Vector3i());
		Vector3i maxPos = pos1.max(pos2, new Vector3i()).add(1, 1, 1);

		boolean matchAnyBlock = protectedData.getBoolean("match_any_block", true);

		Material match = Material.matchMaterial(protectedData.getString("match", Material.AIR.toString()));
		Material place = Material.matchMaterial(protectedData.getString("place", Material.AIR.toString()));

		World world = click.player().getWorld();
		for (int i = minPos.x; i < maxPos.x; i++)
		{
			for (int j = minPos.y; j < maxPos.y; j++)
			{
				for (int k = minPos.z; k < maxPos.z; k++)
				{
					if (matchAnyBlock)
					{
						FunnyLib
							.getWorkloadRunnable()
							.addWorkload(new PlaceBlockWorkload(world, i, j, k, place));
					} else
					{
						FunnyLib
							.getWorkloadRunnable()
							.addWorkload(new ReplaceBlockWorkload(world, i, j, k, match, place));
					}
				}
			}
		}
	}

	public void applyAdvancedRectangle(Click click)
	{
		Vector3i pos1 = new Vector3i();
		Vector3i pos2 = new Vector3i();

		ItemNBT data = menu.nbtFromPlayersHandOrStack(null);
		if (data == null) return;
		PdcNBT protectedData = data.protectedData();

		if (!protectedData.has3i("pos1") || !protectedData.has3i("pos2"))
		{
			return;
		}

		protectedData.get3i("pos1", pos1);
		protectedData.get3i("pos2", pos2);

		Vector3i minPos = pos1.min(pos2, new Vector3i());
		Vector3i maxPos = pos1.max(pos2, new Vector3i()).add(1, 1, 1);

		boolean matchAnyBlock = protectedData.getBoolean("match_any_block", true);

		Material match = Material.matchMaterial(protectedData.getString("match", Material.AIR.toString()));
		WeightedRandomBag<Material> bag = new WeightedRandomBag<>();

		protectedData.getOrCreateCompound("advanced").getKeys().stream().filter(p -> p.startsWith("slot_")).forEach(key -> {
			String string = protectedData.getOrCreateCompound("advanced").getString(key);
			Material material = Material.matchMaterial(string);
			if (material != null)
			{
				bag.addEntry(material, 1);
			}
		});

		int airCount = protectedData.getOrCreateCompound("advanced").getInt("air_count", 0);
		if (airCount > 0)
		{
			bag.addEntry(Material.AIR, airCount);
		}

		World world = click.player().getWorld();
		for (int i = minPos.x; i < maxPos.x; i++)
		{
			for (int j = minPos.y; j < maxPos.y; j++)
			{
				for (int k = minPos.z; k < maxPos.z; k++)
				{
					if (matchAnyBlock)
					{
						FunnyLib
							.getWorkloadRunnable()
							.addWorkload(new PlaceWithWeightedMaterialWorkload(world, i, j, k, bag));
					} else
					{
						FunnyLib
							.getWorkloadRunnable()
							.addWorkload(new ReplaceWithWeightedMaterialWorkload(world, i, j, k, match, bag));
					}
				}
			}
		}
	}
}
