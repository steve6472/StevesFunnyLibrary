package steve6472.funnylib.menu.slots.buttons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 7/7/2023
 * Project: StevesFunnyLibrary <br>
 */
public class MoveButtonSlot extends ButtonSlot
{
	public MoveButtonSlot(ItemStack icon, int moveX, int moveY, boolean isSticky)
	{
		super(icon, isSticky);
		setClick(c -> {
			c.menu().move(moveX, moveY);
			return Response.cancel();
		});
	}

	public MoveButtonSlot(JSONMessage label, Material material, int moveX, int moveY, boolean isSticky)
	{
		super(label, material, isSticky);
		setClick(c -> {
			c.menu().move(moveX, moveY);
			return Response.cancel();
		});
	}

	public MoveButtonSlot(JSONMessage label, Material material, int moveX, int moveY)
	{
		super(label, material);
		setClick(c -> {
			c.menu().move(moveX, moveY);
			return Response.cancel();
		});
	}
}
