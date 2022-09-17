package steve6472.funnylib.context;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public class BlockFaceContext extends BlockContext
{
	private final BlockFace face;

	public BlockFaceContext(Location location, BlockFace face)
	{
		super(location);
		if (face == null)
			face = BlockFace.SELF;
		this.face = face;
	}

	public BlockFace getFace()
	{
		return face;
	}
}
