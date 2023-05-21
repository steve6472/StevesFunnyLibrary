package steve6472.funnylib.item.builtin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.*;

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
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		NBT data = context.getItemData();
		Block clickedBlock = context.getBlock();
		if (useType == UseType.LEFT)
		{
			data.set3i("start", clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ());
		} else
		{
			data.set3i("end", clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ());
		}

		// Fix coordinates so start is always smaller then end
		fixCoordinates(data, "start", "end");

		updateLore(context.customItemContext().getData());
		result.setCancelled(true);
	}

	/**
	 *
	 * @param data NBT compound containing start and end keys
	 * @param startKey start key
	 * @param endKey end key
	 * @return true if both start and end keys exist and coordinates were fixed
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static boolean fixCoordinates(NBT data, String startKey, String endKey)
	{
		if (data.has3i(startKey) && data.has3i(endKey))
		{
			Vector3i start = data.get3i(startKey);
			Vector3i end = data.get3i(endKey);

			int startX = Math.min(start.x, end.x);
			int startY = Math.min(start.y, end.y);
			int startZ = Math.min(start.z, end.z);

			int endX = Math.max(start.x, end.x);
			int endY = Math.max(start.y, end.y);
			int endZ = Math.max(start.z, end.z);

			data.set3i(startKey, startX, startY, startZ);
			data.set3i(endKey, endX, endY, endZ);

			return true;
		}
		return false;
	}

	public static void updateLore(ItemNBT data)
	{
		ItemStackBuilder builder = ItemStackBuilder.edit(data).removeLore();

		if (data.hasCompound("start"))
		{
			Vector3i start = data.get3i("start");
			builder.addLore(Messages.createLocationMessage("Start: ", start.x, start.y, start.z));
		}
		if (data.hasCompound("end"))
		{
			Vector3i end = data.get3i("end");
			builder.addLore(Messages.createLocationMessage("End: ", end.x, end.y, end.z));
		}
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (FunnyLib.getUptimeTicks() % 3 != 0) return;

		NBT data = context.getItemData();
		if (!data.hasCompound("start") || !data.hasCompound("end"))
			return;

		Vector3i start = data.get3i("start");
		Vector3i end = data.get3i("end");

		int x0 = start.x;
		int y0 = start.y;
		int z0 = start.z;
		int x1 = end.x + 1;
		int y1 = end.y + 1;
		int z1 = end.z + 1;

		ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, x0, y0, z0, x1, y1, z1, 0, 0.5, OPTIONS);
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.PAPER).setName("Area Location Marker", ChatColor.DARK_AQUA).buildItemStack();
	}
}
