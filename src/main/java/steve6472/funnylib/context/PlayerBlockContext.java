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
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public record PlayerBlockContext(PlayerItemContext playerContext, BlockFaceContext blockContext) implements Context
{
	/*
	 * Player Context
	 */
	public Player getPlayer()
	{
		return playerContext.getPlayer();
	}

	public Location getPlayerLocation()
	{
		return playerContext.getLocation();
	}

	public Chunk getPlayerChunk()
	{
		return playerContext.getPlayerChunk();
	}

	public boolean isCreative()
	{
		return playerContext.isCreative();
	}

	public boolean isSurvival()
	{
		return playerContext.isSurvival();
	}

	public boolean isPlayerSneaking()
	{
		return playerContext.isSneaking();
	}

	/*
	 * Item Context
	 */

	public ItemStack getHandItem()
	{
		return playerContext.getHandItem();
	}

	public EquipmentSlot getHand()
	{
		return playerContext.getHand();
	}

	public void reduceItemAmount(int byAmount)
	{
		playerContext.reduceItemAmount(byAmount);
	}

	/*
	 * Custom Item Context
	 */

	public ItemContext itemContext()
	{
		return playerContext.itemContext();
	}

	public CustomItemContext customItemContext()
	{
		return playerContext.customItemContext();
	}

	public boolean isCustomItem()
	{
		return playerContext.isCustomItem();
	}

	public void saveItemData()
	{
		playerContext.saveItemData();
	}

	public CustomItem getCustomItem()
	{
		return playerContext.getCustomItem();
	}

	public boolean holdsCustomItem(CustomItem item)
	{
		return playerContext.holdsCustomItem(item);
	}

	public ItemNBT getItemData()
	{
		return playerContext.getItemData();
	}

	/*
	 * Block context
	 */

	public BlockFace getFace()
	{
		return blockContext.getFace();
	}

	public Location getBlockLocation()
	{
		return blockContext.getLocation();
	}

	public World getWorld()
	{
		return blockContext.getWorld();
	}

	public Chunk getChunk()
	{
		return blockContext.getChunk();
	}

	public Block getBlock()
	{
		return blockContext.getBlock();
	}

	public State getState()
	{
		return blockContext.getState();
	}

	public CustomBlockData getBlockData()
	{
		return blockContext.getBlockData();
	}

	public <T extends CustomBlockData> T getBlockData(Class<T> expectedType)
	{
		return blockContext.getBlockData(expectedType);
	}

	public boolean testDataType(Class<? extends CustomBlockData> expectedType)
	{
		return blockContext.testDataType(expectedType);
	}
}
