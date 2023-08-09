package steve6472.funnylib.menu.slots;

import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;

/**
 * Created by steve6472
 * Date: 6/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class IconSlot extends ItemSlot
{
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
}
