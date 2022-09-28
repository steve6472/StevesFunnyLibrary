package steve6472.funnylib.blocks;

import org.bukkit.World;
import org.json.JSONObject;
import steve6472.funnylib.context.BlockContext;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class CustomBlockData
{
	private CustomBlock block;
	protected int x, y, z;
	protected String worldName;

	public CustomBlockData()
	{

	}

	public final void setLocation(World world, int x, int y, int z)
	{
		this.worldName = world.getName();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void onPlace(BlockContext context) {}
	public void onRemove(BlockContext context) {}
	public void save(JSONObject json) {}
	public void load(JSONObject json) {}

	public void setLogic(CustomBlock block)
	{
		this.block = block;
	}

	public CustomBlock getBlock()
	{
		return block;
	}
}
