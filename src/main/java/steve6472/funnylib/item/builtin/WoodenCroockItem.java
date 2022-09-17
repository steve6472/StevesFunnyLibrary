package steve6472.funnylib.item.builtin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.ItemBreakBlockEvent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.RandomUtil;

/**
 * Created by steve6472
 * Date: 9/16/2022
 * Project: StevesFunnyLibrary <br>
 */
public class WoodenCroockItem extends CustomItem implements ItemBreakBlockEvent
{
	@Override
	public String id()
	{
		return "wooden_crook";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder
			.create(Material.WOODEN_SWORD)
			.setCustomModelData(1)
			.setName("Wooden Crook", ChatColor.DARK_AQUA)
			.addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
			.addAttribute(Attribute.GENERIC_ATTACK_SPEED, -0.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
			.setHideFlags(ItemFlag.HIDE_ATTRIBUTES)
			.buildItemStack();
	}

	@Override
	public boolean breakBlock(Player player, ItemStack itemStack, Block block)
	{
		if (player.getGameMode() == GameMode.CREATIVE)
			return true;

		if (block.getType().name().endsWith("_LEAVES"))
		{
			boolean proc = false;
			if (RandomUtil.randomDouble(0, 1) <= 0.1)
			{
				block.setType(Material.AIR);
				block.getWorld().dropItemNaturally(block.getLocation(), FunnyLib.SILKWORM.newItemStack());
				proc = true;
			}

			if (RandomUtil.randomDouble(0, 1) <= 0.2 && block.getType() == Material.OAK_LEAVES)
			{
				block.setType(Material.AIR);
				block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.APPLE));
				proc = true;
			}

			return !proc;
		}

		return true;
	}
}
