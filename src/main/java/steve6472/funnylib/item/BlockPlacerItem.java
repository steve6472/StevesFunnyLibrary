package steve6472.funnylib.item;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.item.events.ItemClickEvents;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MetaUtil;

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

		Location location = e.getClickedBlock().getLocation().add(e.getBlockFace().getDirection());
		if (location.getBlock().getType().isAir())
		{
			State stateForPlacement = block.getStateForPlacement(new PlayerBlockContext(new PlayerContext(e.getPlayer()), new BlockFaceContext(location, e.getBlockFace())));
			Blocks.setBlockState(location, stateForPlacement);
			if (e.getItem() != null && e.getPlayer().getGameMode() == GameMode.SURVIVAL)
			{
				e.getItem().setAmount(e.getItem().getAmount() - 1);
			}
		}
	}
}
