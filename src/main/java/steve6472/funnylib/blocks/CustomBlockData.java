package steve6472.funnylib.blocks;

import org.bukkit.Location;
import org.json.JSONObject;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.json.IJsonConfig;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class CustomBlockData
{
	private CustomBlock block;
	protected Location pos;

	public CustomBlockData()
	{

	}

	public final void setPos(Location pos)
	{
		this.pos = pos;
	}

	public void onPlace(BlockContext context) {}
	public void onRemove(BlockContext context) {}
	public void save(JSONObject json, boolean unloading) {}
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
