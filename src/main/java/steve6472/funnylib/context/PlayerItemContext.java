package steve6472.funnylib.context;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.util.NBT;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public record PlayerItemContext(PlayerContext playerContext, CustomItemContext customItemContext)
{
	/**
	 * @deprecated use {@code Items.callWithItemContext}
	 */
	@Deprecated
	public PlayerItemContext(Player player)
	{
		this(player, EquipmentSlot.HAND);
	}

	/**
	 * @deprecated use {@code Items.callWithItemContext}
	 */
	@Deprecated
	public PlayerItemContext(Player player, CustomItemContext customItemContext)
	{
		this(new PlayerContext(player, customItemContext.getHand()), customItemContext);
	}

	/**
	 * @deprecated use {@code Items.callWithItemContext}
	 */
	@Deprecated
	public PlayerItemContext(Player player, EquipmentSlot hand)
	{
		this(new PlayerContext(player, hand), new CustomItemContext(player, hand));
	}

	/**
	 * @deprecated use {@code Items.callWithItemContext}
	 */
	@Deprecated
	public PlayerItemContext(Player player, EquipmentSlot hand, ItemStack itemStack)
	{
		this(new PlayerContext(player, hand), new CustomItemContext(hand, itemStack));
	}

	public Player getPlayer()
	{
		return playerContext.getPlayer();
	}

	public EquipmentSlot getHand()
	{
		return playerContext.getHand();
	}

	public World getWorld()
	{
		return playerContext.getWorld();
	}

	public Location getLocation()
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

	public ItemStack getHandItem()
	{
		return customItemContext.getItemStack();
	}

	public ItemStack getItemStack()
	{
		return customItemContext.getItemStack();
	}

	public ItemContext itemContext()
	{
		return customItemContext;
	}

	/*
	 * Custom Item Context
	 */

	public void reduceItemAmount(int byAmount)
	{
		customItemContext.reduceAmount(byAmount);
	}

	public boolean isCustomItem()
	{
		return customItemContext.isCustomItem();
	}

	public void saveItemData()
	{
		customItemContext.saveData();
	}

	public CustomItem getCustomItem()
	{
		return customItemContext.getCustomItem();
	}

	public boolean holdsCustomItem(CustomItem item)
	{
		return customItemContext.holdsCustomItem(item);
	}

	public NBT getItemData()
	{
		return customItemContext.getData();
	}
}
