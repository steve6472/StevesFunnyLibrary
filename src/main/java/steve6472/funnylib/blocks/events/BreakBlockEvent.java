package steve6472.funnylib.blocks.events;

import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerContext;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface BreakBlockEvent
{
	default void playerBreakBlock(PlayerContext playerContext, BlockFaceContext blockContext, BlockBreakResult result) {}
}
