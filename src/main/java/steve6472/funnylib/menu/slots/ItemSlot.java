package steve6472.funnylib.menu.slots;

import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Slot;

/**
 * Created by steve6472
 * Date: 6/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class ItemSlot extends Slot
{
	private ItemStack icon;

	public ItemSlot(ItemStack icon, boolean isSticky)
	{
		super(isSticky);
		setIcon(icon);
	}

	public void setIcon(ItemStack icon)
	{
		this.icon = icon;
		updateSlot();
	}

	@Override
	public ItemStack getIcon()
	{
		return icon;
	}
}
