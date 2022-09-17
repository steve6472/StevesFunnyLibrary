package steve6472.funnylib.blocks.events;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.stateengine.State;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public class BlockBreakResult
{
	private boolean cancel;
	private BlockData resultBlock = Material.AIR.createBlockData();
	private CustomBlock resultCustomBlock;
	private State resultState;
	private boolean changedResult;
	private boolean dropItems = true;

	/**
	 * Cancel vanilla block break result
	 */
	public void cancel()
	{
		this.cancel = true;
	}

	public boolean isCancelled()
	{
		return cancel;
	}

	public boolean isResultChanged()
	{
		return changedResult;
	}

	public BlockData getResultBlock()
	{
		return resultBlock;
	}

	public CustomBlock getResultCustomBlock()
	{
		return resultCustomBlock;
	}

	public boolean dropsItems()
	{
		return dropItems;
	}

	public void setDropItems(boolean dropItems)
	{
		this.dropItems = dropItems;
	}

	public State getResultState()
	{
		return resultState;
	}

	public Material getResultMaterial()
	{
		return resultBlock.getMaterial();
	}

	public void setResultBlock(BlockData resultBlock)
	{
		this.resultBlock = resultBlock;
		this.changedResult = true;
	}

	public void setResultBlock(Material material)
	{
		this.resultBlock = material.createBlockData();
		this.changedResult = true;
	}

	public void setResultBlock(CustomBlock block)
	{
		this.resultCustomBlock = block;
		this.changedResult = true;
	}

	public void setResultState(State state)
	{
		this.resultState = state;
		this.changedResult = true;
	}
}
