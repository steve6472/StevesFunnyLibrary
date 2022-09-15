package steve6472.funnylib.blocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class BlockData
{
	private CustomBlock block;
	int x, y, z;
	String worldName;

	public BlockData()
	{

	}

	public final void save()
	{
		Blocks.setBlockData(new Location(Bukkit.getWorld(worldName), x, y, z), this);
	}

	public final void setLocation(World world, int x, int y, int z)
	{
		this.worldName = world.getName();
		this.x = x;
		this.y = y;
		this.z = z;
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
