package steve6472.funnylib.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.blocks.stateengine.StateObject;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public abstract class CustomBlock extends StateObject
{
	public CustomBlock()
	{
		super();
	}

	public abstract String id();

	public State getStateForPlacement(Player player, Block clickedBlock, BlockFace clickedFace)
	{
		return getDefaultState();
	}

	public abstract BlockData getVanillaState(State state);

	public void onPlace(Location location, State state, CustomBlockData data) {}
	public void onRemove(Location location, State state, CustomBlockData data) {}
}
