package steve6472.funnylib.data;

import org.bukkit.block.data.BlockData;
import org.joml.Vector3i;

public record BlockInfo(BlockData data, Vector3i position)
{
	@Override
	public String toString()
	{
		return "BlockInfo{" + "data=" + data + ", position=" + position + '}';
	}
}