package steve6472.funnylib.menu.slots;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.components.Disable;
import steve6472.funnylib.menu.components.DisableComponent;
import steve6472.funnylib.util.ItemStackBuilder;

/**
 * Created by steve6472
 * Date: 6/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class IconSlot extends ItemSlot implements Disable<IconSlot>
{
	private final DisableComponent disabled = new DisableComponent(this, ItemStackBuilder.quick(Material.GRAY_DYE, "Disabled Icon", ChatColor.DARK_GRAY));

	public IconSlot(ItemStack icon, boolean isSticky)
	{
		super(icon, isSticky);
	}

	@Override
	public boolean canBeInteractedWith(Click click)
	{
		return false;
	}

	@Override
	public Response onClick(Click click)
	{
		return Response.cancel();
	}

	@Override
	public ItemStack getIcon()
	{
		if (isDisabled()) return getDisabledIcon();
		return super.getIcon();
	}

	@Override
	public DisableComponent getDisableComponent()
	{
		return disabled;
	}

	@Override
	public IconSlot getThis()
	{
		return this;
	}
}
