package steve6472.funnylib.item.builtin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.BlockData;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.builtin.TeleportButtonBlock;
import steve6472.funnylib.blocks.builtin.TeleportButtonData;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.ItemClickEvents;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.ParticleUtil;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class TeleportButtonItem extends CustomItem implements ItemClickEvents
{
	@Override
	public String id()
	{
		return "teleport_button";
	}

	@Override
	public void rightClickBlock(ItemStack item, PlayerInteractEvent e)
	{
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) return;

		TeleportButtonBlock teleportBlock = (TeleportButtonBlock) Blocks.getCustomBlockById(id());
		BlockData blockData = teleportBlock.createBlockData();
		blockData.setLogic(teleportBlock);
		Blocks.setBlockData(clickedBlock, blockData);
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.STONE_BUTTON).setName("Teleport Button", ChatColor.DARK_AQUA).buildItemStack();
	}
}
