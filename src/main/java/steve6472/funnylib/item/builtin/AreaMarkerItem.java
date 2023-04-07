package steve6472.funnylib.item.builtin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.CustomItemContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.ItemData;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.ParticleUtil;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class AreaMarkerItem extends CustomItem implements TickInHandEvent
{
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.WHITE, 0.75f);

	@Override
	public String id()
	{
		return "area_location_marker";
	}

	@Override
	public ItemData createData()
	{
		return new MarkerData();
	}

	// FIXME: make this not create particles unless both location have been set at least once
	public static class MarkerData extends ItemData
	{
		public int x0, y0, z0, x1, y1, z1;

		@Override
		public String toString()
		{
			return "MarkerData{" + "x0=" + x0 + ", y0=" + y0 + ", z0=" + z0 + ", x1=" + x1 + ", y1=" + y1 + ", z1=" + z1 + '}';
		}
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		MarkerData itemData = context.getItemData();
		Block clickedBlock = context.getBlock();
		if (useType == UseType.LEFT)
		{
			itemData.x0 = clickedBlock.getX();
			itemData.y0 = clickedBlock.getY();
			itemData.z0 = clickedBlock.getZ();

		} else
		{
			itemData.x1 = clickedBlock.getX();
			itemData.y1 = clickedBlock.getY();
			itemData.z1 = clickedBlock.getZ();
		}
		updateLore(context.customItemContext());
		result.setCancelled(true);
	}

	private void updateLore(CustomItemContext itemContext)
	{
		MarkerData data = itemContext.getData();
		ItemStackBuilder builder = ItemStackBuilder.edit(itemContext.getItemStack()).removeLore();
		builder.addLore(JSONMessage
			.create("Start: ").color(ChatColor.GRAY)
			.then("" + data.x0).color(ChatColor.RED)
			.then("/").color(ChatColor.WHITE)
			.then("" + data.y0).color(ChatColor.GREEN)
			.then("/").color(ChatColor.WHITE)
			.then("" + data.z0).color(ChatColor.BLUE));
		builder.addLore(JSONMessage
			.create("End: ").color(ChatColor.GRAY)
			.then("" + data.x1).color(ChatColor.RED)
			.then("/").color(ChatColor.WHITE)
			.then("" + data.y1).color(ChatColor.GREEN)
			.then("/").color(ChatColor.WHITE)
			.then("" + data.z1).color(ChatColor.BLUE));
		builder.buildItemStack();
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (FunnyLib.getUptimeTicks() % 3 != 0) return;

		MarkerData data = context.getItemData();
		int x0 = data.x0;
		int y0 = data.y0;
		int z0 = data.z0;
		int x1 = data.x1;
		int y1 = data.y1;
		int z1 = data.z1;

		ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, Math.min(x0, x1 + 1), Math.min(y0, y1 + 1), Math.min(z0, z1 + 1), Math.max(x0, x1 + 1), Math.max(y0, y1 + 1), Math.max(z0, z1 + 1), 0, 0.5, OPTIONS);
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.PAPER).setName("Area Location Marker", ChatColor.DARK_AQUA).buildItemStack();
	}
}
