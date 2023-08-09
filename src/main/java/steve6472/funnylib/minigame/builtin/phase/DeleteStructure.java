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
		Preconditions.checkNotNull(world);

		Set<Pair<Chunk, Boolean>> chunks = new HashSet<>();

		int offsetX = -structure.getSize().x / 2;
		int offsetY = -structure.getSize().y / 2;
		int offsetZ = -structure.getSize().z / 2;

		for (int i = (x + offsetX) >> 4; i < (x + offsetX + structure.getSize().x) >> 4; i++)
		{
			for (int j = (z + offsetZ) >> 4; j < (z + offsetZ + structure.getSize().z) >> 4; j++)
			{
				Chunk chunkAt = world.getChunkAt(i, j);
				chunkAt.load();
				boolean lastForceState = chunkAt.isForceLoaded();
				chunkAt.setForceLoaded(true);
				chunks.add(new Pair<>(chunkAt, lastForceState));
			}
		}

		for (BlockInfo block : structure.getBlocks())
		{
			world.getBlockAt(x + block.position().x + offsetX, y + block.position().y + offsetY, z + block.position().z + offsetZ).setType(Material.AIR);
		}

		chunks.forEach(c -> c.a().setForceLoaded(c.b()));
		endPhase();
	}

	@Override
	public void end()
	{

	}
}
