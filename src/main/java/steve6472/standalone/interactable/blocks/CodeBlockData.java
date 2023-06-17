package steve6472.standalone.interactable.blocks;

import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.serialize.NBT;
import steve6472.standalone.interactable.ex.CodeExecutor;

/**
 * Created by steve6472
 * Date: 10/22/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CodeBlockData extends CustomBlockData
{
	CodeExecutor executor;

	@Override
	public void toNBT(NBT compound)
	{
		if (executor != null)
		{
			NBT executorCompound = compound.createCompound();
			executor.toNBT(executorCompound);
			compound.setCompound("executor", executorCompound);
		}
	}

	@Override
	public void fromNBT(NBT compound)
	{
		if (compound.hasCompound("executor"))
		{
			executor = new CodeExecutor();
			executor.fromNBT(compound.getCompound("executor"));
		}
	}
}
