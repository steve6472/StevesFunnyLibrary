package steve6472.funnylib.blocks;

import org.bukkit.Chunk;
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
		BlockData blockData = e
			.getBlock()
			.getChunk()
			.getPersistentDataContainer()
			.get(createKey(e.getBlock()), CustomBlockDataType.BLOCK_DATA);


	}

	@EventHandler
	public void click(PlayerInteractEvent e)
	{
		Block block = e.getClickedBlock();
		if (block == null) return;

		BlockData blockData = getBlockData(block);
		if (blockData == null) return;

		CustomBlock customBlock = blockData.getBlock();

		if (customBlock instanceof BlockClickEvents bce)
		{
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				bce.rightClick(e.getItem(), e.getPlayer(), e);
			}
			if (e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				bce.leftClick(e.getItem(), e.getPlayer(), e);
			}
		}
	}

	/*
	 * Fancy stuff
	 */

	private static NamespacedKey createKey(Block block)
	{
		return new NamespacedKey(FunnyLib.getPlugin(), "BLOCK_%d_%d_%d".formatted(block.getX(), block.getY(), block.getZ()));
	}

	public static BlockData getBlockData(Block block)
	{
		Chunk chunk = block.getChunk();
		PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
		return chunkData.get(createKey(block), CustomBlockDataType.BLOCK_DATA);
	}

	public static <T extends BlockData> T getBlockData(Block block, Class<T> expectedType)
	{
		Chunk chunk = block.getChunk();
		PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
		BlockData blockData = chunkData.get(createKey(block), CustomBlockDataType.BLOCK_DATA);
		if (blockData.getClass().isAssignableFrom(expectedType))
			return (T) blockData;
		throw new RuntimeException("Expected type " + expectedType.getName()+ ", got " + blockData.getClass().getName());
	}

	public static void setBlockData(Block block, BlockData data)
	{
		Chunk chunk = block.getChunk();
		PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
		chunkData.set(createKey(block), CustomBlockDataType.BLOCK_DATA, data);
	}

	public static CustomBlock getCustomBlockById(String id)
	{
		return BLOCKS.get(id);
	}
}
