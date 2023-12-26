package steve6472.standalone.dimensionviewer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.joml.Vector3d;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.util.Preconditions;

import java.util.UUID;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 10/13/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ViewController
{
	public SnapshotView view;
	public int refreshRate;

	private final UUID viewer;

	public ViewController(Player player, int refreshRate)
	{
		this.viewer = player.getUniqueId();
		this.refreshRate = refreshRate;
		view = createView(player);
	}

	public void tick(boolean move)
	{
		view.tick(move);

		if (FunnyLib.getUptimeTicks() % Math.max(refreshRate, 5) == 0 && move)
		{
			Player player = Bukkit.getPlayer(viewer);
			Preconditions.checkNotNull(player, "Viewer not found");
			SnapshotView newView = createView(player);
			view.delete();
			view = newView;
		}
	}

	public void delete()
	{
		view.delete();
		view = null;
	}

	private SnapshotView createView(Player player)
	{
		Function<Vector3d, Vector3d> OVERWORLD_TO_NETHER = (vec) -> new Vector3d(vec.x / 8.0, vec.y, vec.z / 8.0);
		Function<Vector3d, Vector3d> NETHER_TO_OVERWORLD = (vec) -> new Vector3d(vec.x * 8d, vec.y, vec.z * 8d);

		World netherWorld = Bukkit
			.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.NETHER).findFirst().orElseThrow();
		World overWorld = Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.NORMAL).findFirst().orElseThrow();
		boolean inOverworld = player.getWorld() == overWorld;

		return new SnapshotView(
			7,
			inOverworld ? 1d / 8d : 8d,
			player,
			inOverworld ? netherWorld : overWorld,
			inOverworld ? OVERWORLD_TO_NETHER : NETHER_TO_OVERWORLD);
	}

	private SnapshotView createViewTest(Player player)
	{
		Function<Vector3d, Vector3d> PLAYER_TO_TEST = (vec) -> new Vector3d(vec.x - 20, vec.y, vec.z);
		Function<Vector3d, Vector3d> TEST_TO_PLAYER = (vec) -> new Vector3d(vec.x + 20, vec.y, vec.z);
//		Function<Vector3d, Vector3d> PLAYER_TO_TEST = (vec) -> new Vector3d(vec.x, vec.y, vec.z);
//		Function<Vector3d, Vector3d> TEST_TO_PLAYER = (vec) -> new Vector3d(vec.x, vec.y, vec.z);

		World overWorld = Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.NORMAL).findFirst().orElseThrow();

		return new SnapshotView(
			6,
			1d,
			player,
			overWorld,
			PLAYER_TO_TEST);
	}
}
