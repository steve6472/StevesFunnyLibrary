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
import steve6472.funnylib.blocks.builtin.AdminInterface;
import steve6472.funnylib.blocks.events.BlockBreakResult;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.blocks.events.BreakBlockEvent;
import steve6472.funnylib.blocks.stateengine.properties.BooleanProperty;
import steve6472.funnylib.blocks.stateengine.properties.IProperty;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.util.generated.BlockGen;
import steve6472.standalone.interactable.ex.CodeExecutor;
import steve6472.standalone.interactable.ex.ExpContext;
import steve6472.standalone.interactable.ex.event.ExpressionEventData;
import steve6472.standalone.interactable.ex.event.ExpressionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/22/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CodeBlock extends CustomBlock implements IBlockData, Activable, BlockClickEvents, BlockTick, BreakBlockEvent, AdminInterface<CodeBlockData>
{
	private static final boolean DEBUG = false;
	public static final BooleanProperty EXECUTING = BooleanProperty.create("executing");

	@Override
	public String id()
	{
		return "code_block";
	}

	@Override
	public void rightClick(PlayerBlockContext context, PlayerInteractEvent e)
	{
		if (context.getHandItem().getType().isAir())
		{
			context.getPlayer().sendMessage(ChatColor.GREEN + "Executing code");
			start(context.blockContext());
			e.setCancelled(true);
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
		CodeBlockData blockData = context.getBlockData(CodeBlockData.class);
		CodeExecutor executor = blockData.executor;
		if (executor != null)
		{
			executor.start();
		}
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
			if (DEBUG)
			{
				Bukkit.broadcastMessage(ChatColor.RED + "Can not execute code from block at " + context.getLocation().getBlockX() + "/" + context.getLocation().getBlockY() + "/" + context.getLocation().getBlockZ());
				Bukkit.broadcastMessage(ChatColor.RED + "Executor has not been initialized");
			}
			return;
		}

		if (executor.executeTick())
		{
			Blocks.changeBlockState(context.getLocation(), context.getState().with(EXECUTING, false));
			if (DEBUG)
			{
				Bukkit.broadcastMessage(ChatColor.GREEN + "Block at " + context.getLocation().getBlockX() + "/" + context.getLocation().getBlockY() + "/" + context.getLocation().getBlockZ() + " finished executing");
			}
		}
	}

	@Override
	public void tick(BlockContext context)
	{
		CodeBlockData blockData = context.getBlockData(CodeBlockData.class);
		if (blockData.repeating && !context.getState().get(EXECUTING))
		{
			start(context);
		}

		List<ExpressionEventData> eventData = new ArrayList<>();
		// Poll events
		for (ExpressionEvent event : blockData.events)
		{
			event.createEvents(new ExpContext(context.getLocation()), eventData);
			for (ExpressionEventData eventDatum : eventData)
			{
				ExpContext eventContext = new ExpContext(context.getLocation());
				eventContext.setEventData(eventDatum);
				CodeExecutor eventExecutor = new CodeExecutor(event.cloneExpression(), eventContext);
				eventExecutor.start();

				blockData.executingEvents.add(eventExecutor);
			}
			eventData.clear();
		}

		/*
		 * Iterate over all current events, if any of them finish after ticking, remove them
		 */
		blockData.executingEvents.removeIf(CodeExecutor::executeTick);

		execute(context);
	}

	@Override
	public void playerBreakBlock(PlayerItemContext playerContext, BlockFaceContext blockContext, BlockBreakResult result)
	{
		if (playerContext.holdsCustomItem(FunnyLib.ADMIN_WRENCH))
		{
			result.cancel();
		}
	}
/*
	MenuBuilder MENU = MenuBuilder.create(3, "Code Block")
		.slot(4, 1, SlotBuilder.buttonSlot(ItemStackBuilder.quick(Material.COMMAND_BLOCK, "Ticking Code"), (c, m) -> {
			ArbitraryData data = m.getPassedData();
			CodeBlockData blockData = data.getData("data", CodeBlockData.class);
			Location location = data.getData("location", Location.class);

			if (blockData.executor == null)
			{
				blockData.executor = new CodeExecutor(CodeBlockExp.body(null), new ExpContext(location));
			}

			ExpressionMenu.showMenuToPlayer(c.player(), (CodeBlockExp) blockData.executor.expression);
		}))
		.slot(1, 1, SlotBuilder.toggleSlot("Repeating", d -> d.getData("data", CodeBlockData.class).repeating, (d, flag) -> d.getData("data", CodeBlockData.class).repeating = flag))
		.slot(7, 1, SlotBuilder.buttonSlotResponse(Material.COMMAND_BLOCK, "Events", (c, m) -> {
			return Response.redirect(EventGui.getEventsMenu(), new ArbitraryData().copyFrom(m.getPassedData()));
		}));
//		.slot(7, 2, SlotBuilder.create(ItemStackBuilder.quick(Material.COMMAND_BLOCK, "Test Event")).onClick((c, m) -> {
//			ArbitraryData data = m.getPassedData();
//			CodeBlockData blockData = data.getData("data", CodeBlockData.class);
//			ItemStack item = c.itemOnCursor();
//			if (!Items.isCustomItem(item)) return Response.cancel();
//			CustomItem customItem = Items.getCustomItem(item);
//			if (customItem != FunnyLib.AREA_LOCATION_MARKER) return Response.cancel();
//			AreaSelection areaSelection = AreaSelection.fromItem(item);
//
//			PlayerAreaExpEvent e = new PlayerAreaExpEvent();
//			e.setExpression(CodeBlockExp.body(null, new DebugHereExp(0)));
//			e.area = areaSelection;
//			blockData.events.add(e);
//			return Response.cancel();
//		}).allow(ClickType.values()).allow(InventoryAction.values())).allowPlayerInventory();
*/
	@Override
	public void showInterface(CodeBlockData data, PlayerBlockContext context)
	{/*
		MENU.setData("data", data);
		MENU.setData("location", context.getBlockLocation());
		MENU.build().showToPlayers(context.getPlayer());*/
	}
}
