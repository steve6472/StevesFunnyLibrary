package steve6472.funnylib.minigame.builtin.phase;

import org.bukkit.Bukkit;
import org.bukkit.World;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class PlaceStructure extends AbstractGamePhase
{
	private final GameStructure structure;
	private final UUID world;
	private final int x, y, z;

	public PlaceStructure(Game game, World world, GameStructure structure, int x, int y, int z)
	{
		super(game);
		this.world = world.getUID();
		this.structure = structure;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void start()
	{
		structure.placeCentered(Bukkit.getWorld(world), x, y, z);
		endPhase();
	}

	@Override
	public void end()
	{

	}
}
