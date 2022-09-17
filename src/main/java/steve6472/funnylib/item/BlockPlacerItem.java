package steve6472.funnylib.item;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.item.events.ItemClickEvents;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class BlockPlacerItem extends GenericItem implements ItemClickEvents
{
	private final CustomBlock block;

	public BlockPlacerItem(CustomBlock block, String id, Material material, String name, int customModelId)
	{
		super(id, material, name, customModelId);
		this.block = block;
	}

	@Override
	public void rightClickBlock(ItemStack item, PlayerInteractEvent e)
	{
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) return;

		State stateForPlacement = block.getStateForPlacement(e.getPlayer(), e.getClickedBlock(), e.getBlockFace());
		Location location = e.getClickedBlock().getLocation().add(e.getBlockFace().getDirection());
		Blocks.setBlockState(location, stateForPlacement);
	}
}
