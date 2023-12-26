package steve6472.funnylib.minigame.builtin.phase;

import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import steve6472.funnylib.data.BlockInfo;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.Pair;
import steve6472.funnylib.util.Preconditions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 8/5/2023
 * Project: StevesFunnyLibrary <br>
 */
public class DeleteStructure extends AbstractGamePhase
{
	private final UUID worldUUID;
	private final int x, y, z;
	private final GameStructure structure;

	public DeleteStructure(Game game, World worldUUID, GameStructure structure, int x, int y, int z)
	{
		super(game);
		this.worldUUID = worldUUID.getUID();
		this.structure = structure;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void start()
	{
		World world = Bukkit.getWorld(worldUUID);
		structure.unplaceCentered(world, x, y, z);
		endPhase();
	}

	@Override
	public void end()
	{

	}
}
