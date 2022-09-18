package steve6472.funnylib.blocks;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.blocks.stateengine.StateObject;

import java.util.List;

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

	public void onPlace(BlockContext context) {}
	public void onRemove(BlockContext context) {}
	public boolean canBreakByExplosion(BlockContext blockContext) { return true; }
	public boolean canPlayerBreak(PlayerContext context) { return true; }

	/*
	 * Drops
	 */

	public void getDrops(BlockContext blockContext, List<ItemStack> drops) {}

	public void getDrops(PlayerBlockContext context, List<ItemStack> drops)
	{
		getDrops(context.blockContext(), drops);
	}

	public void getExplodeDrops(BlockContext blockContext, List<ItemStack> drops)
	{
		getDrops(blockContext, drops);
	}

	public boolean vanillaBlockDrops() { return false; }
}
