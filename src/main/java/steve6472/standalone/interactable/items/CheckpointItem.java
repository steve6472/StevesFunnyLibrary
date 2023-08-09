package steve6472.standalone.interactable.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerEntityContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.standalone.interactable.blocks.data.CheckpointBlockData;

/**
 * Created by steve6472
 * Date: 5/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class CheckpointItem extends CustomItem
{
	@Override
	public String id()
	{
		return "checkpoint_returner";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.ENDER_PEARL).setName("Return To Checkpoint", ChatColor.DARK_AQUA).buildItemStack();
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		if (context.isCreative() && context.isPlayerSneaking() && useType.isRight())
		{
			CustomBlockData blockData = Blocks.getBlockData(context.getBlockLocation());
			if (blockData instanceof CheckpointBlockData data)
			{
				ItemNBT itemData = context.getItemData();
				itemData.setString("parkour_id", data.parkourId);
				itemData.setInt("reached_checkpoint", data.order);
				itemData.setBoolean("use_player_facing", data.usePlayerFacing);
				Location newLoc = context.getBlockLocation().clone().add(0.5, 0.01, 0.5);
				newLoc.setYaw(data.yaw);
				newLoc.setPitch(data.pitch);
				itemData.setLocation("loc", newLoc);
				context.getPlayer().sendMessage(ChatColor.GREEN + "Checkpoint item set to " + data.parkourId + " at " + data.order + ".");
				result.setCancelled(true);
				return;
			}
		}
		use(context.playerContext(), useType, result);
	}

	@Override
	public void useOnEntity(PlayerEntityContext context, CancellableResult result)
	{
		use(context.playerContext(), UseType.RIGHT, result);
	}

	@Override
	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result)
	{
		use(context, useType, result);
	}

	private void use(PlayerItemContext context, UseType useType, CancellableResult result)
	{
		result.setCancelled(true);
		if (useType.isLeft())
			return;

		if (context.isCreative() && context.isSneaking() && useType.isRight())
			return;

		ItemNBT itemData = context.getItemData();
		if (itemData.hasLocation("loc"))
		{
			Location loc = itemData.getLocation("loc");
			if (itemData.getBoolean("use_player_facing", false))
			{
				loc = loc.clone();
				loc.setYaw(context.getPlayer().getEyeLocation().getYaw());
				loc.setPitch(context.getPlayer().getEyeLocation().getPitch());
			}
			context.getPlayer().teleport(loc);
		}
	}
}
