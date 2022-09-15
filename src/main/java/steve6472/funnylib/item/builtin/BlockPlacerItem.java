package steve6472.funnylib.item.builtin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockStateType;
import steve6472.funnylib.blocks.builtin.TeleportButtonBlock;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.ItemClickEvents;
import steve6472.funnylib.util.ItemStackBuilder;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public abstract class BlockPlacerItem extends CustomItem implements ItemClickEvents
{
	private final CustomBlock block;

	public BlockPlacerItem(CustomBlock block)
	{
		this.block = block;
	}

	@Override
	public void rightClickBlock(ItemStack item, PlayerInteractEvent e)
	{
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) return;

		State stateForPlacement = block.getStateForPlacement(e.getPlayer(), e.getClickedBlock(), e.getBlockFace());
		Location location = e.getClickedBlock().getLocation().add(e.getBlockFace().getDirection());
		BlockData vanillaState = block.getVanillaState(stateForPlacement);
		location.getBlock().setBlockData(vanillaState);
		Blocks.setBlockState(location, stateForPlacement);
	}
}
