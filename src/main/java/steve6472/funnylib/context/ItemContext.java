package steve6472.funnylib.context;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.util.MiscUtil;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ItemContext
{
	protected final ItemStack itemStack;
	// FIXME: remove hand from ItemContex and have it only in PlayerContext
	protected final EquipmentSlot hand;

	/**
	 * Constructs ItemContext with item from {@code player} main hand
	 * @param player used to retrieve the item in main hand
	 */
	public ItemContext(Player player)
	{
		this(player, EquipmentSlot.HAND);
	}

	/**
	 * Constructs ItemContext with item from {@code player} {@code hand}
	 * @param player used to retrieve the item in specified {@code hand}
	 * @param hand used to retrieve item from {@code player}
	 */
	public ItemContext(Player player, EquipmentSlot hand)
	{
		this(hand, player.getInventory().getItem(hand));
	}

	public ItemContext(EquipmentSlot hand, ItemStack item)
	{
		if (item == null)
		{
			itemStack = MiscUtil.AIR;
		} else
		{
			this.itemStack = item;
		}
		this.hand = hand;
	}

	public ItemStack getItemStack()
	{
		return itemStack;
	}

	public EquipmentSlot getHand()
	{
		return hand;
	}

	public void reduceAmount(int byAmount)
	{
		itemStack.setAmount(itemStack.getAmount() - byAmount);
	}

	public CustomItemContext toCustomItemContext()
	{
		return new CustomItemContext(this);
	}
}
