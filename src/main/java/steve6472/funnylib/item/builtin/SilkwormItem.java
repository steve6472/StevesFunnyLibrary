package steve6472.funnylib.item.builtin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.ItemBreakBlockEvent;
import steve6472.funnylib.item.events.ItemClickEvents;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.RandomUtil;

/**
 * Created by steve6472
 * Date: 9/16/2022
 * Project: StevesFunnyLibrary <br>
 */
public class SilkwormItem extends CustomItem implements ItemClickEvents
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
	public void rightClickBlock(ItemStack item, PlayerInteractEvent e)
	{
		if (e.getClickedBlock().getType().name().endsWith("_LEAVES"))
		{
			Blocks.setBlockState(e.getClickedBlock().getLocation(), FunnyLib.SILK_LEAVES.getDefaultState());
		}
		e.setCancelled(true);
	}
}
