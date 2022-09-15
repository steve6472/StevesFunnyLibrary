package steve6472.funnylib.blocks;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class BlockData
{
	private CustomBlock block;

	public BlockData()
	{

	}

	public void setLogic(CustomBlock block)
	{
		this.block = block;
	}

	public CustomBlock getBlock()
	{
		return block;
	}
}
