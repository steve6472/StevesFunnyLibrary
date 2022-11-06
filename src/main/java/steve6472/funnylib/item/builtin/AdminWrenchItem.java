package steve6472.funnylib.item.builtin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.blocks.builtin.AdminInterface;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.util.ItemStackBuilder;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class AdminWrenchItem extends CustomItem
{
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

		result.cancel();

		Block clickedBlock = context.getBlock();

		State blockState = Blocks.getBlockState(clickedBlock.getLocation());
		if (blockState == null) return;

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
}
