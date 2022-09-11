package steve6472.funnylib.item.builtin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.ClickEvents;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.ParticleUtil;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class MarkerItem extends CustomItem implements ClickEvents, TickInHandEvent
{
	private static final NamespacedKey X = new NamespacedKey(FunnyLib.getPlugin(), "x");
	private static final NamespacedKey Y = new NamespacedKey(FunnyLib.getPlugin(), "y");
	private static final NamespacedKey Z = new NamespacedKey(FunnyLib.getPlugin(), "z");
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.WHITE, 0.75f);

	@Override
	public String id()
	{
		return "location_marker";
	}

	@Override
	public void leftClickBlock(ItemStack item, PlayerInteractEvent e)
	{
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) return;

		ItemStackBuilder.edit(item)
			.customTagInt(X, clickedBlock.getX())
			.customTagInt(Y, clickedBlock.getY())
			.customTagInt(Z, clickedBlock.getZ())
			.removeLore()
			.addLore(ChatColor.DARK_GRAY + "Location: " + ChatColor.RED + clickedBlock.getX() + ChatColor.WHITE + "/" + ChatColor.GREEN + clickedBlock.getY() + ChatColor.WHITE + "/" + ChatColor.BLUE + clickedBlock.getZ())
			.buildItemStack();
		e.setCancelled(true);
	}

	@Override
	public void tickInHand(Player player, ItemStack itemStack, EquipmentSlot hand)
	{
		ItemStackBuilder edit = ItemStackBuilder.edit(itemStack);
		int x = edit.getCustomTagInt(X);
		int y = edit.getCustomTagInt(Y);
		int z = edit.getCustomTagInt(Z);

		ParticleUtil.boxAbsolute(player, Particle.REDSTONE, x, y, z, x + 1, y + 1, z + 1, 0, 0.1, OPTIONS);
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.PAPER).setName("Location Marker", ChatColor.DARK_AQUA).buildItemStack();
	}
}
