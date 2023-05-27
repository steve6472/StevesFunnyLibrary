package steve6472.funnylib.item;

import org.bukkit.Location;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.UseType;

/**
 * Created by steve6472
 * Date: 5/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class PlacerHeadItem extends GenericHeadItem
{
	private final CustomBlock block;

	public PlacerHeadItem(CustomBlock block, String headUrl, String id, String name)
	{
		super(headUrl, id, name);
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
