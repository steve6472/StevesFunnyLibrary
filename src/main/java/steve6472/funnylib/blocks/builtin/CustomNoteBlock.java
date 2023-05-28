package steve6472.funnylib.blocks.builtin;

import org.bukkit.*;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.util.generated.BlockGen;

import java.util.List;

/**
 * Created by steve6472
 * Date: 5/28/2023
 * Project: StevesFunnyLibrary <br>
 */
public class CustomNoteBlock extends CustomBlock implements IBlockData, BlockTick
{
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.RED, 0.8f);

	@Override
	public String id()
	{
		return "noteblock";
	}

	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return BlockGen.NoteBlock(Instrument.PIANO, new Note(0), false);
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new CustomNoteBlockData();
	}

	@Override
	public void tick(BlockContext context)
	{
		CustomNoteBlockData blockData = context.getBlockData(CustomNoteBlockData.class);
		if (blockData.powered)
		{
			context.getWorld().spawnParticle(Particle.REDSTONE, context.getLocation().getX() + 0.5, context.getLocation().getY() + 1.1, context.getLocation().getZ() + 0.5, 0, OPTIONS);
		}
	}

	@Override
	public void getDrops(BlockContext blockContext, List<ItemStack> drops)
	{
		drops.add(new ItemStack(Material.NOTE_BLOCK));
	}

	@Override
	public void getDrops(PlayerBlockContext context, List<ItemStack> drops)
	{
		drops.add(new ItemStack(Material.NOTE_BLOCK));
	}

	@Override
	public PistonMoveReaction pistonReaction()
	{
		return PistonMoveReaction.MOVE;
	}
}
