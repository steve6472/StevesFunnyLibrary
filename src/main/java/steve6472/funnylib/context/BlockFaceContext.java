package steve6472.funnylib.context;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.stateengine.State;

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

	public BlockFaceContext(Location location, BlockFace face, State state)
	{
		super(location, state);
		if (face == null)
			face = BlockFace.SELF;
		this.face = face;
	}

	public BlockFaceContext(Location location, BlockFace face, State state, CustomBlockData blockData)
	{
		super(location, state, blockData);
		if (face == null)
			face = BlockFace.SELF;
		this.face = face;
	}

	public BlockFace getFace()
	{
		return face;
	}
}
