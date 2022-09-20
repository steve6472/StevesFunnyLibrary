package steve6472.funnylib.item.builtin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.builtin.AdminInterface;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.ItemClickEvents;
import steve6472.funnylib.util.ItemStackBuilder;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class AdminWrenchItem extends CustomItem implements ItemClickEvents
{
	@Override
	public String id()
	{
		return "admin_wrench";
	}

	@Override
	public void leftClickBlock(ItemStack item, PlayerInteractEvent e)
	{
		e.setCancelled(true);

		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) return;

		State blockState = Blocks.getBlockState(clickedBlock.getLocation());
		if (blockState == null) return;

		CustomBlock object = (CustomBlock) blockState.getObject();
		//noinspection rawtypes
		if (object instanceof AdminInterface af)
		{
			CustomBlockData blockData = Blocks.getBlockData(clickedBlock.getLocation());
			//noinspection unchecked
			af.showInterface(blockData, new PlayerBlockContext(new PlayerContext(e.getPlayer()), new BlockFaceContext(clickedBlock.getLocation(), e.getBlockFace(), blockState, blockData)));
		}
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.STICK).setName("Admin Wrench", ChatColor.DARK_AQUA).glow().buildItemStack();
	}
}
