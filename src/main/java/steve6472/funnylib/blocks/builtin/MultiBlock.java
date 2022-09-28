package steve6472.funnylib.blocks.builtin;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerContext;

/**
 * Created by steve6472
 * Date: 9/25/2022
 * Project: StevesFunnyLibrary <br>
 */
public class MultiBlock extends CustomBlock
{
	@Override
	public String id()
	{
		return "multiblock";
	}

	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return Material.BARRIER.createBlockData();
	}

	@Override
	public boolean canPlayerBreak(PlayerContext context)
	{
		return false;
	}

	@Override
	public boolean canBreakByExplosion(BlockContext blockContext)
	{
		return false;
	}
}
