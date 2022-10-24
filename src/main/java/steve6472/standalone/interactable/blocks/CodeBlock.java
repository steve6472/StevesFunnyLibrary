package steve6472.standalone.interactable.blocks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.player.PlayerInteractEvent;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.IBlockData;
import steve6472.funnylib.blocks.events.BlockBreakResult;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.blocks.events.BreakBlockEvent;
import steve6472.funnylib.blocks.stateengine.properties.BooleanProperty;
import steve6472.funnylib.blocks.stateengine.properties.IProperty;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.util.BlockGen;
import steve6472.funnylib.util.MetaUtil;
import steve6472.funnylib.util.MiscUtil;
import steve6472.standalone.interactable.ex.*;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/22/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CodeBlock extends CustomBlock implements IBlockData, Activable, BlockClickEvents, BlockTick, BreakBlockEvent
{
	public static final BooleanProperty EXECUTING = BooleanProperty.create("executing");

	@Override
	public String id()
	{
		return "code_block";
	}

	@Override
	public void rightClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		if (context.getCustomItem() == FunnyLib.ADMIN_WRENCH)
		{
			if (e.getPlayer().isSneaking())
			{
				context.getPlayer().sendMessage(ChatColor.GREEN + "Executing code");
				start(context.blockContext());
				e.setCancelled(true);
			} else
			{
				CodeBlockData blockData = context.getBlockData(CodeBlockData.class);
				Expression expression = MetaUtil.getValue(context.getPlayer(), CodeBlockExp.class, "code_block");
				blockData.executor = new CodeExecutor(expression, new ExpContext(context.getBlockLocation().clone()));
				context.getPlayer().sendMessage(ChatColor.GREEN + "Successfully bound block to " + context.getPlayer().getName());
			}
		}

		e.setCancelled(true);
		e.getPlayer().closeInventory();
	}

	@Override
	public void fillStates(List<IProperty<?>> properties)
	{
		properties.add(EXECUTING);
	}

	@Override
	public BlockData getVanillaState(BlockContext context)
	{
		return BlockGen.CommandBlock(context.getState().get(EXECUTING), BlockFace.DOWN);
	}

	@Override
	public CustomBlockData createBlockData()
	{
		return new CodeBlockData();
	}

	@Override
	public void activate(PlayerBlockContext context)
	{
		start(context.blockContext());
	}

	private void start(BlockContext context)
	{
		Blocks.changeBlockState(context.getLocation(), context.getState().with(EXECUTING, true));
		context.updateLazy();
		execute(context);
	}

	private void execute(BlockContext context)
	{
		if (!context.getState().get(EXECUTING))
			return;

		CodeBlockData blockData = context.getBlockData(CodeBlockData.class);
		CodeExecutor executor = blockData.executor;
		if (executor == null)
		{
			Blocks.changeBlockState(context.getLocation(), context.getState().with(EXECUTING, false));
			Bukkit.broadcastMessage(ChatColor.RED + "Can not execute code from block at " + context.getLocation().getBlockX() + "/" + context.getLocation().getBlockY() + "/" + context.getLocation().getBlockZ());
			Bukkit.broadcastMessage(ChatColor.RED + "Executor has not been initialized");
			return;
		}

		if (executor.executeTick())
		{
			Blocks.changeBlockState(context.getLocation(), context.getState().with(EXECUTING, false));
			Bukkit.broadcastMessage(ChatColor.GREEN + "Block at " + context.getLocation().getBlockX() + "/" + context.getLocation().getBlockY() + "/" + context.getLocation().getBlockZ() + " finished executing");
		}
	}

	@Override
	public void tick(BlockContext context)
	{
		execute(context);
	}

	@Override
	public void playerBreakBlock(PlayerContext playerContext, BlockFaceContext blockContext, BlockBreakResult result)
	{
		if (playerContext.getCustomItem() == FunnyLib.ADMIN_WRENCH)
		{
			result.cancel();
		}
	}
}