package steve6472.funnylib.context;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.Preconditions;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CustomItemContext extends ItemContext implements Context
{
	private static final String NOT_CUSTOM_ITEM = "ItemStack is not a Custom Item";

	protected final CustomItem customItem;
	protected final ItemNBT data;

	/**
	 * Constructs {@code CustomItemContext} with item from {@code player} main hand
	 *
	 * @param player - used to retrieve the item in main hand
	 */
	public CustomItemContext(Player player)
	{
		this(EquipmentSlot.HAND, player.getInventory().getItem(EquipmentSlot.HAND));
	}

	/**
	 * Constructs {@code CustomItemContext} with item from {@code player} {@code hand}
	 *
	 * @param player used to retrieve the item in specified {@code hand}
	 * @param hand   used to retrieve item from {@code player}
	 */
	public CustomItemContext(Player player, EquipmentSlot hand)
	{
		this(hand, player.getInventory().getItem(hand));
	}

	public CustomItemContext(EquipmentSlot hand, ItemStack item)
	{
		super(hand, item);
		this.customItem = Items.getCustomItem(item);
		data = customItem != null ? ItemNBT.create(item) : null;
	}

	public CustomItemContext(ItemContext parent)
	{
		this(parent.getHand(), parent.getItemStack());
	}

	public boolean isCustomItem()
	{
		return customItem != null;
	}

	/**
	 * Saved automatically after the method returns
	 */
	public void saveData()
	{
		Preconditions.checkTrue(isCustomItem(), NOT_CUSTOM_ITEM);
		data.save();
	}

	public CustomItem getCustomItem()
	{
		Preconditions.checkTrue(isCustomItem(), NOT_CUSTOM_ITEM);
		return customItem;
	}

	public boolean holdsCustomItem(CustomItem item)
	{
		return customItem == item;
	}

	public ItemNBT getData()
	{
		return data;
	}
}
