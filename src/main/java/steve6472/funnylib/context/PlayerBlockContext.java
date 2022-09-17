package steve6472.funnylib.context;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.item.CustomItem;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public record PlayerBlockContext(PlayerContext playerContext, BlockFaceContext blockClickContext)
{
	public Player getPlayer()
	{
		return playerContext.getPlayer();
	}

	public ItemStack getHandItem()
	{
		return playerContext.getHandItem();
	}

	public EquipmentSlot getHand()
	{
		return playerContext.getHand();
	}

	public CustomItem getCustomItem()
	{
		return playerContext.getCustomItem();
	}

	public Location getPlayerLocation()
	{
		return getPlayer().getLocation();
	}

	/*
	 * Block context
	 */

	public BlockFace getFace()
	{
		return blockClickContext.getFace();
	}

	public Location getBlockLocation()
	{
		return blockClickContext.getLocation();
	}

	public World getWorld()
	{
		return blockClickContext.getWorld();
	}

	public Chunk getChunk()
	{
		return blockClickContext.getChunk();
	}

	public Block getBlock()
	{
		return blockClickContext.getBlock();
	}

	public State getState()
	{
		return blockClickContext.getState();
	}

	public CustomBlockData getBlockData()
	{
		return blockClickContext.getBlockData();
	}

	public <T extends CustomBlockData> T getBlockData(Class<T> expectedType)
	{
		return blockClickContext.getBlockData(expectedType);
	}
}
