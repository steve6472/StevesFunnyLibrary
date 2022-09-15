package steve6472.funnylib.blocks;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.events.ServerTickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Blocks implements Listener
{
	public static final int CREATE_DATA = 1;

	public static final int DEFAULT_PLACE_FLAGS = CREATE_DATA;

	public static final Map<String, CustomBlock> BLOCKS = new HashMap<>();

	public static void registerBlock(CustomBlock customBlock)
	{
		BLOCKS.put(customBlock.id(), customBlock);
	}

	@EventHandler
	public void tick(ServerTickEvent e)
	{

	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e)
	{
		PersistentDataContainer chunkData = e.getBlock().getChunk().getPersistentDataContainer();
		Location location = e.getBlock().getLocation();

		NamespacedKey stateKey = createStateKey(location);
		State state = chunkData.get(stateKey, CustomBlockStateType.BLOCK_STATE);
		if (state != null)
		{
			chunkData.remove(stateKey);
			chunkData.remove(createDataKey(location));
		}
	}

	@EventHandler
	public void click(PlayerInteractEvent e)
	{
		Block block = e.getClickedBlock();
		if (block == null) return;

		State state = getBlockState(e.getClickedBlock().getLocation());
		if (state == null) return;

		CustomBlock customBlock = ((CustomBlock) state.getObject());

		if (customBlock instanceof BlockClickEvents bce)
		{
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				bce.rightClick(state, e.getItem(), e.getPlayer(), e);
			}
			if (e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				bce.leftClick(state, e.getItem(), e.getPlayer(), e);
			}
		}
	}

	/*
	 * Fancy stuff
	 */

	private static NamespacedKey createStateKey(Location location)
	{
		return new NamespacedKey(FunnyLib.getPlugin(), "block_state_%d_%d_%d".formatted(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}

	public static void setBlockState(Location location, State state)
	{
		setBlockState(location, state, DEFAULT_PLACE_FLAGS);
	}

	public static void setBlockState(Location location, State state, int flags)
	{
		if (!(state.getObject() instanceof CustomBlock cb))
		{
			throw new RuntimeException("State not of type CustomBlock");
		}

		Chunk chunk = location.getChunk();
		PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
		chunkData.set(createStateKey(location), CustomBlockStateType.BLOCK_STATE, state);

		if ((flags & CREATE_DATA) != 0 && cb instanceof IBlockData data)
		{
			BlockData blockData = data.createBlockData();
			blockData.setLogic(cb);
			blockData.setLocation(chunk.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
			setBlockData(location, blockData);
		}
	}

	public static State getBlockState(Location location)
	{
		Chunk chunk = location.getChunk();
		PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
		return chunkData.get(createStateKey(location), CustomBlockStateType.BLOCK_STATE);
	}

	private static NamespacedKey createDataKey(Location location)
	{
		return new NamespacedKey(FunnyLib.getPlugin(), "block_data_%d_%d_%d".formatted(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}

	public static BlockData getBlockData(Location location)
	{
		Chunk chunk = location.getChunk();
		PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
		return chunkData.get(createDataKey(location), CustomBlockDataType.BLOCK_DATA);
	}

	public static <T extends BlockData> T getBlockData(Location location, Class<T> expectedType)
	{
		Chunk chunk = location.getChunk();
		PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
		BlockData blockData = chunkData.get(createDataKey(location), CustomBlockDataType.BLOCK_DATA);
		if (blockData.getClass().isAssignableFrom(expectedType))
			return (T) blockData;
		throw new RuntimeException("Expected type " + expectedType.getName()+ ", got " + blockData.getClass().getName());
	}

	public static void setBlockData(Location location, BlockData data)
	{
		Chunk chunk = location.getChunk();
		PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
		chunkData.set(createDataKey(location), CustomBlockDataType.BLOCK_DATA, data);
	}

	public static CustomBlock getCustomBlockById(String id)
	{
		return BLOCKS.get(id);
	}
}
