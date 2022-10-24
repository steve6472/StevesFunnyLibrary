package steve6472.funnylib.context;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.stateengine.State;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public class BlockContext
{
	private final Location location;

	/*
	 * Lazy
	 */
	private Block block;
	private State customState;
	private CustomBlockData customBlockData;

	public BlockContext(Location location)
	{
		this.location = location;
	}

	public BlockContext(Location location, State state)
	{
		this.location = location;
		this.customState = state;
	}

	public BlockContext(Location location, State state, CustomBlockData data)
	{
		this.location = location;
		this.customState = state;
		this.customBlockData = data;
	}

	public Location getLocation()
	{
		return location;
	}

	public World getWorld()
	{
		return location.getWorld();
	}

	public Chunk getChunk()
	{
		return location.getChunk();
	}

	/*
	 * Lazy
	 */
	public Block getBlock()
	{
		if (block == null)
		{
			updateBlock();
		}

		return block;
	}

	public void updateBlock()
	{
		block = location.getBlock();
	}

	public State getState()
	{
		if (customState == null)
		{
			updateState();
		}

		return customState;
	}

	public void updateState()
	{
		customState = Blocks.getBlockState(location);
	}

	public CustomBlockData getBlockData()
	{
		if (customBlockData == null)
		{
			updateBlockData();
		}

		return customBlockData;
	}

	public void updateBlockData()
	{
		customBlockData = Blocks.getBlockData(location);
	}

	public void updateLazy()
	{
		if (block != null) updateBlock();
		if (customState != null) updateState();
		if (customBlockData != null) updateBlockData();
	}

	public <T extends CustomBlockData> T getBlockData(Class<T> expectedType)
	{
		CustomBlockData data = getBlockData();

		if (data != null && data.getClass().isAssignableFrom(expectedType))
			//noinspection unchecked
			return (T) data;

		throw new RuntimeException("Expected type " + expectedType.getName() + ", got " + (data == null ? "null" : data.getClass().getName()));
	}

	public boolean testDataType(Class<? extends CustomBlockData> expectedType)
	{
		CustomBlockData data = getBlockData();
		if (data == null)
			return false;
		return data.getClass().isAssignableFrom(expectedType);
	}
}
