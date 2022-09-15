package steve6472.funnylib.blocks;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.FaceAttachable;
import steve6472.funnylib.blocks.stateengine.properties.BooleanProperty;
import steve6472.funnylib.blocks.stateengine.properties.EnumProperty;

public class States
{
	public static final BooleanProperty STABILIZED = BooleanProperty.create("stabilized");
	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
	public static final EnumProperty<BlockFace> FACING_HORIZONTAL = EnumProperty.create("facing", BlockFace.class, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
	public static final EnumProperty<BlockFace> FACING = EnumProperty.create("facing", BlockFace.class, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
	public static final EnumProperty<FaceAttachable.AttachedFace> ATTACHED = EnumProperty.create("face", FaceAttachable.AttachedFace.class, FaceAttachable.AttachedFace.values());
}
