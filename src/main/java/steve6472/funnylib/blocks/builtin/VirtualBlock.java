package steve6472.funnylib.blocks.builtin;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.context.BlockContext;

/**
 * Created by steve6472
 * Date: 6/17/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class VirtualBlock extends CustomBlock implements IBlockData
{
	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return Material.AIR.createBlockData();
	}
}
