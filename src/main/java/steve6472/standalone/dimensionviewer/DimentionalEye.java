package steve6472.standalone.dimensionviewer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.context.*;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/13/2023
 * Project: StevesFunnyLibrary <br>
 */
public class DimentionalEye extends CustomItem implements TickInHandEvent
{
	public DimensionViewer viewer;

	public DimentionalEye(DimensionViewer viewer)
	{
		this.viewer = viewer;
	}

	@Override
	public String id()
	{
		return "dimentional_eye";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.ENDER_EYE).setName("Dimensional Eye", ChatColor.GOLD).glow().buildItemStack();
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		UUID player = context.getPlayer().getUniqueId();
		if (context.getHand() != EquipmentSlot.OFF_HAND)
		{
			ViewController controller = viewer.controllers.remove(player);
			if (controller != null)
				controller.delete();
			return;
		}

		ViewController controller = viewer.controllers.get(player);
		if (controller == null)
		{
			viewer.controllers.put(player, controller = new ViewController(context.getPlayer(), context.getWorld().getEnvironment() == World.Environment.NORMAL ? 600 : 10));
		}
		controller.tick(!context.getItemData().getBoolean("locked", false));
	}

	private void use(PlayerItemContext context)
	{
		boolean locked = !context.getItemData().getBoolean("locked", false);
		context.getItemData().setBoolean("locked", locked);
		context.getPlayer().sendMessage(locked ? ChatColor.RED + "Locked" : ChatColor.GREEN + "Unlocked");
	}

	// region Disable Actions
	@Override
	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result)
	{
		use(context);
		result.setCancelled(true);
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		use(context.playerContext());
		result.setCancelled(true);
	}

	@Override
	public void useOnEntity(PlayerEntityContext context, CancellableResult result)
	{
		use(context.playerContext());
		result.setCancelled(true);
	}
	// endregion disable_actions
}
