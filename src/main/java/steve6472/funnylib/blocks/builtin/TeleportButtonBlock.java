package steve6472.funnylib.blocks.builtin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.BlockData;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.events.BlockClickEvents;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class TeleportButtonBlock extends CustomBlock implements IBlockData, BlockClickEvents
{
	@Override
	public String id()
	{
		return "teleport_button";
	}

	@Override
	public BlockData createBlockData()
	{
		return new TeleportButtonData();
	}

	@Override
	public void rightClick(ItemStack itemInHand, Player player, PlayerInteractEvent e)
	{
		TeleportButtonData blockData = Blocks.getBlockData(e.getClickedBlock(), TeleportButtonData.class);
		player.teleport(new Location(player.getWorld(), blockData.x + 0.5, blockData.y, blockData.z + 0.5));
	}
}
