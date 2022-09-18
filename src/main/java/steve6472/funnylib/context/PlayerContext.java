package steve6472.funnylib.context;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.MarkerItem;
import steve6472.funnylib.util.MiscUtil;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public class PlayerContext
{
	private final Player player;
	private final ItemStack handItem;
	private final EquipmentSlot hand;

	private CustomItem customItem;

	public PlayerContext(Player player)
	{
		this(player, EquipmentSlot.HAND);
	}

	public PlayerContext(Player player, EquipmentSlot hand)
	{
		this.player = player;
		ItemStack item = player.getInventory().getItem(hand);
		if (item == null)
		{
			handItem = MiscUtil.AIR;
		} else
		{
			this.handItem = item;
		}
		this.hand = hand;
	}

	public Player getPlayer()
	{
		return player;
	}

	public ItemStack getHandItem()
	{
		return handItem;
	}

	public EquipmentSlot getHand()
	{
		return hand;
	}

	public World getWorld()
	{
		return player.getWorld();
	}

	/*
	 * Fancy helpers
	 */

	public CustomItem getCustomItem()
	{
		if (customItem == null)
		{
			customItem = Items.getCustomItem(handItem);
		}

		return customItem;
	}

	public boolean holdsCustomItem(CustomItem item)
	{
		return getCustomItem() == item;
	}

	public boolean isCreative()
	{
		return getPlayer().getGameMode() == GameMode.CREATIVE;
	}

	public boolean isSurvival()
	{
		return getPlayer().getGameMode() == GameMode.SURVIVAL;
	}

	public boolean isSneaking()
	{
		return getPlayer().isSneaking();
	}
}
