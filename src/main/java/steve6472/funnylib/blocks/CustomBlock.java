package steve6472.funnylib.blocks;

import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
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

	public State getStateForPlacement(PlayerBlockContext context)
	{
		return getDefaultState();
	}

	public abstract BlockData getVanillaState(BlockContext context);

	public void onPlace(BlockContext context) {}
	public void onRemove(BlockContext context) {}

	/**
	 * Called only when canBurn() returns true<br>
	 * and the vanilla state can burn
	 * @param context context
	 */
	public void onBurn(BlockContext context) {}
	public boolean canBreakByExplosion(BlockContext blockContext) { return true; }
	public boolean canPlayerBreak(PlayerContext context) { return true; }

	/**
	 * Has only effect on vanilla-burnable blocks
	 * @return true or false
	 */
	public boolean canBurn() { return false; }

	/*
	 * Drops
	 */

	public void getDrops(BlockContext blockContext, List<ItemStack> drops) {}

	public void getDrops(PlayerBlockContext context, List<ItemStack> drops)
	{
		getDrops(context.blockContext(), drops);
	}

	public boolean vanillaBlockDrops() { return false; }

	/**
	 * I don't wanna replace vanilla PistonBaseBlock just to make this one stupid fucking feature
	 * <br><br>
	 * IF and only IF I decided to do this: <br>
	 *  - Unfreeze Block registry <br>
	 *  - Replace vanilla PistonBaseBlock with custom one <br>
	 *  - Freeze Block registry <br>
	 * <br>
	 * @return Does not actually matter. It blocks both push & retract
	 * @deprecated Spigot does not have API for this and I do not want to implement it
	 */
	@Deprecated
	public PistonMoveReaction pistonReaction()
	{
		return PistonMoveReaction.BLOCK;
	}
}
