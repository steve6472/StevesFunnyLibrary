package steve6472.funnylib.item;

import org.bukkit.Location;
import org.bukkit.Material;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.UseType;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class BlockPlacerItem extends GenericItem
{
	private final CustomBlock block;

	public BlockPlacerItem(CustomBlock block, String id, Material material, String name, int customModelId)
	{
		super(id, material, name, customModelId);
		this.block = block;
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		if (useType != UseType.RIGHT)
			return;

		Location location = context.getBlockLocation().clone().add(context.getFace().getDirection());
		if (location.getBlock().getType().isAir())
		{
			State stateForPlacement = Items.callWithItemContextR(context.getPlayer(), context.getHand(), context.getHandItem(), ic -> block.getStateForPlacement(new PlayerBlockContext(ic, new BlockFaceContext(location, context.getFace()))));
			Blocks.setBlockState(location, stateForPlacement);
			if (context.getHandItem() != null && !context.isCreative())
			{
				context.reduceItemAmount(1);
			}
		}
	}
}
