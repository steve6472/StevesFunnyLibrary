package steve6472.funnylib.item.builtin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.builtin.AdminInterface;
import steve6472.funnylib.blocks.builtin.VirtualBlock;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class AdminWrenchItem extends CustomItem implements TickInHandEvent
{
	private final Set<Location> virtualBlocks = new HashSet<>();
	private long lastTickClear = 0;

	@Override
	public String id()
	{
		return "admin_wrench";
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		if (!useType.isLeft())
			return;

		Block clickedBlock = context.getBlock();

		State blockState = Blocks.getBlockState(clickedBlock.getLocation());

		if (blockState == null)
		{
			result.cancel();
			return;
		}

		if (blockState.getObject() instanceof VirtualBlock)
		{
			return;
		}

		result.cancel();

		CustomBlock object = (CustomBlock) blockState.getObject();
		//noinspection rawtypes
		if (object instanceof AdminInterface af)
		{
			CustomBlockData blockData = Blocks.getBlockData(clickedBlock.getLocation());
			//noinspection unchecked
			Items.callWithItemContext(context.getPlayer(), context.getHand(), context.getHandItem(), ic -> af.showInterface(blockData, new PlayerBlockContext(ic, new BlockFaceContext(clickedBlock.getLocation(), context.getFace(), blockState, blockData))));
		}
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.STICK).setName("Admin Wrench", ChatColor.DARK_AQUA).glow().buildItemStack();
	}

	// TODO: probably some item unhanded event so I can make sure to clear all the virtual blocks
	// TODO: maybe even playerDisconnectedWithItemEvent or changedDimensionEvent

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (FunnyLib.getUptimeTicks() % 10 != 0)
			return;

		Location location = context.playerContext().getLocation();
		Location loc = location.clone();

		// Make sure the blocks are cleared only once (multiple players can hold this item)
		if (FunnyLib.getUptimeTicks() % 20 == 0 && lastTickClear != FunnyLib.getUptimeTicks())
		{
			virtualBlocks.forEach(vb -> vb.getBlock().setBlockData(Material.AIR.createBlockData(), false));
			virtualBlocks.clear();
			lastTickClear = FunnyLib.getUptimeTicks();
		}

		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				for (int k = 0; k < 9; k++)
				{
					int x = i - 5 + location.getBlockX();
					int y = j - 5 + location.getBlockY();
					int z = k - 5 + location.getBlockZ();

					loc.setX(x);
					loc.setY(y);
					loc.setZ(z);
					CustomBlockData blockData = Blocks.getBlockData(loc);
					if (blockData == null) continue;
					if (!(blockData.getBlock() instanceof VirtualBlock vb)) continue;

					virtualBlocks.add(loc.clone());
					loc.getBlock().setBlockData(Material.BARRIER.createBlockData(), false);
				}
			}
		}
	}
}
