package steve6472.funnylib.item.builtin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.ItemClickEvents;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.ParticleUtil;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class AreaMarkerItem extends CustomItem implements ItemClickEvents, TickInHandEvent
{
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.WHITE, 0.75f);

	@Override
	public String id()
	{
		return "area_location_marker";
	}

	@Override
	public void leftClickBlock(ItemStack item, PlayerInteractEvent e)
	{
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) return;

		ItemStackBuilder.edit(item)
			.customTagInt("x0", clickedBlock.getX())
			.customTagInt("y0", clickedBlock.getY())
			.customTagInt("z0", clickedBlock.getZ())
			.buildItemStack();
		updateLore(item);
		e.setCancelled(true);
	}

	@Override
	public void rightClickBlock(ItemStack item, PlayerInteractEvent e)
	{
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) return;

		ItemStackBuilder.edit(item)
			.customTagInt("x1", clickedBlock.getX())
			.customTagInt("y1", clickedBlock.getY())
			.customTagInt("z1", clickedBlock.getZ())
			.buildItemStack();
		updateLore(item);
		e.setCancelled(true);
	}

	private void updateLore(ItemStack item)
	{
		ItemStackBuilder builder = ItemStackBuilder.edit(item).removeLore();
		builder.addLore(JSONMessage
			.create("Start: ").color(ChatColor.GRAY)
			.then("" + builder.getCustomTagInt("x0")).color(ChatColor.RED)
			.then("/").color(ChatColor.WHITE)
			.then("" + builder.getCustomTagInt("y0")).color(ChatColor.GREEN)
			.then("/").color(ChatColor.WHITE)
			.then("" + builder.getCustomTagInt("z0")).color(ChatColor.BLUE));
		builder.addLore(JSONMessage
			.create("End: ").color(ChatColor.GRAY)
			.then("" + builder.getCustomTagInt("x1")).color(ChatColor.RED)
			.then("/").color(ChatColor.WHITE)
			.then("" + builder.getCustomTagInt("y1")).color(ChatColor.GREEN)
			.then("/").color(ChatColor.WHITE)
			.then("" + builder.getCustomTagInt("z1")).color(ChatColor.BLUE));
		builder.buildItemStack();
	}

	@Override
	public void tickInHand(Player player, ItemStack itemStack, EquipmentSlot hand)
	{
		if (FunnyLib.getUptimeTicks() % 3 != 0) return;

		ItemStackBuilder edit = ItemStackBuilder.edit(itemStack);
		int x0 = edit.getCustomTagInt("x0");
		int y0 = edit.getCustomTagInt("y0");
		int z0 = edit.getCustomTagInt("z0");
		int x1 = edit.getCustomTagInt("x1");
		int y1 = edit.getCustomTagInt("y1");
		int z1 = edit.getCustomTagInt("z1");

		ParticleUtil.boxAbsolute(player, Particle.REDSTONE, Math.min(x0, x1 + 1), Math.min(y0, y1 + 1), Math.min(z0, z1 + 1), Math.max(x0, x1 + 1), Math.max(y0, y1 + 1), Math.max(z0, z1 + 1), 0, 0.5, OPTIONS);
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.PAPER).setName("Area Location Marker", ChatColor.DARK_AQUA).buildItemStack();
	}
}
