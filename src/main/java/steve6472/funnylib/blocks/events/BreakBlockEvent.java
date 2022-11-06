package steve6472.funnylib.blocks.events;

import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerItemContext;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface BreakBlockEvent
{
	// TODO: replace with PlayerBlockContext ???
	void playerBreakBlock(PlayerItemContext playerContext, BlockFaceContext blockContext, BlockBreakResult result);
}
