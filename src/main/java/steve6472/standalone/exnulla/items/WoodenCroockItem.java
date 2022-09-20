package steve6472.standalone.exnulla.items;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.ItemBreakBlockEvent;
import steve6472.funnylib.util.Checks;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.RandomUtil;
import steve6472.standalone.exnulla.ExNulla;

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
	public boolean breakBlock(PlayerBlockContext context)
	{
		if (context.isCreative())
			return true;

		if (Checks.isLeavesMaterial(context.getBlock().getType()))
		{
			boolean proc = false;
			if (RandomUtil.randomDouble(0, 1) <= 0.1)
			{
				context.getBlock().setType(Material.AIR);
				context.getBlock().getWorld().dropItemNaturally(context.getBlockLocation(), ExNulla.SILKWORM.newItemStack());
				proc = true;
			}

			if (RandomUtil.randomDouble(0, 1) <= 0.2 && context.getBlock().getType() == Material.OAK_LEAVES)
			{
				context.getBlock().setType(Material.AIR);
				context.getBlock().getWorld().dropItemNaturally(context.getBlockLocation(), new ItemStack(Material.APPLE));
				proc = true;
			}

			return !proc;
		}

		return true;
	}
}
