package steve6472.funnylib.serialize;

import org.bukkit.Chunk;

/**
 * Created by steve6472
 * Date: 4/23/2023
 * Project: StevesFunnyLibrary <br>
 */
public class ChunkNBT extends NBT
{
	private Chunk chunk;

	public Chunk getChunk()
	{
		return chunk;
	}

	public static ChunkNBT create(Chunk chunk)
	{
		ChunkNBT chunkNBT = new ChunkNBT();
		chunkNBT.container = chunk.getPersistentDataContainer();

		return chunkNBT;
	}
}
