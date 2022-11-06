package steve6472.funnylib.item.builtin;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.ParticleUtil;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class MarkerItem extends CustomItem implements TickInHandEvent
{
	public static final String ID = "location_marker";
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.WHITE, 0.75f);

	@Override
	public String id()
	{
		return ID;
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		if (!useType.isLeft())
			return;

		Block clickedBlock = context.getBlock();
		if (clickedBlock == null) return;

		ItemStackBuilder.edit(context.getHandItem())
			.customTagInt("x", clickedBlock.getX())
			.customTagInt("y", clickedBlock.getY())
			.customTagInt("z", clickedBlock.getZ())
			.removeLore()
			.addLore(ChatColor.DARK_GRAY + "Location: " + ChatColor.RED + clickedBlock.getX() + ChatColor.WHITE + "/" + ChatColor.GREEN + clickedBlock.getY() + ChatColor.WHITE + "/" + ChatColor.BLUE + clickedBlock.getZ())
			.buildItemStack();
		result.setCancelled(true);
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (FunnyLib.getUptimeTicks() % 3 != 0) return;

		ItemStackBuilder edit = ItemStackBuilder.edit(context.getHandItem());
		int x = edit.getCustomTagInt("x");
		int y = edit.getCustomTagInt("y");
		int z = edit.getCustomTagInt("z");

		ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, x, y, z, x + 1, y + 1, z + 1, 0, 0.2, OPTIONS);
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.PAPER).setName("Location Marker", ChatColor.DARK_AQUA).buildItemStack();
	}
}
