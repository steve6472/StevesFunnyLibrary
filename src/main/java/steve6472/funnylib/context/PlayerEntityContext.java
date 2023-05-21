package steve6472.funnylib.context;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public record PlayerEntityContext(PlayerItemContext playerContext, EntityContext entityContext)
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

	public boolean isSneaking()
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
	 * Entity context
	 */

	public Entity getEntity()
	{
		return entityContext.getEntity();
	}

	public World getEntityWorld()
	{
		return entityContext.getWorld();
	}

	public Chunk getEntityChunk()
	{
		return entityContext.getEntityChunk();
	}

	public Location getEntityLocation()
	{
		return entityContext.getLocation();
	}
}
