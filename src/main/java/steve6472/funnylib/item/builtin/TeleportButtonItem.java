package steve6472.funnylib.item.builtin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.BlockData;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.builtin.TeleportButtonBlock;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.ItemClickEvents;
import steve6472.funnylib.util.ItemStackBuilder;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class TeleportButtonItem extends BlockPlacerItem
{
	public TeleportButtonItem()
	{
		super(FunnyLib.TELEPORT_BUTTON_BLOCK);
	}

	@Override
	public String id()
	{
		return "teleport_button";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.STONE_BUTTON).setName("Teleport Button", ChatColor.DARK_AQUA).buildItemStack();
	}
}
