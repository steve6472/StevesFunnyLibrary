package steve6472.funnylib.blocks.builtin;

import org.bukkit.util.Vector;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.context.BlockContext;

/**
 * Created by steve6472
 * Date: 9/25/2022
 * Project: StevesFunnyLibrary <br>
 */
public interface IMultiBlock
{
	Vector multiblockSize();

	default void breakMultiblock(BlockContext context)
	{
		Vector vector = multiblockSize();
		for (int i = 0; i < vector.getBlockX(); i++)
		{
			for (int j = 0; j < vector.getBlockY(); j++)
			{
				for (int k = 0; k < vector.getBlockZ(); k++)
				{
					if (i == 0 && j == 0 && k == 0)
						continue;

					Blocks.setBlockState(context.getLocation().clone().add(i, j, k), null);
				}
			}
		}
	}
}
