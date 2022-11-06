package steve6472.standalone.exnulla.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.util.Checks;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.standalone.exnulla.ExNulla;

/**
 * Created by steve6472
 * Date: 9/16/2022
 * Project: StevesFunnyLibrary <br>
 */
public class SilkwormItem extends CustomItem
{
	@Override
	public String id()
	{
		return "silkworm";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder
			.create(Material.COMMAND_BLOCK)
			.setCustomModelData(1)
			.setName("Silkworm", ChatColor.DARK_AQUA)
			.buildItemStack();
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		if (!useType.isRight())
			return;

		if (Checks.isLeavesMaterial(context.getBlock().getType()))
		{
			Blocks.setBlockState(context.getBlockLocation(), ExNulla.SILK_LEAVES.getDefaultState());
			if (!context.isCreative())
			{
				context.reduceItemAmount(1);
			}
		}
		result.cancel();
	}
}
