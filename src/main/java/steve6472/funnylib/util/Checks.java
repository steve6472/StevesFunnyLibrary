package steve6472.funnylib.util;

import net.minecraft.world.item.Tier;
import org.bukkit.Material;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Checks
{
	public static boolean isSwordMaterial(Material material)
	{
		return
			material == Material.WOODEN_SWORD ||
			material == Material.STONE_SWORD ||
			material == Material.IRON_SWORD ||
			material == Material.GOLDEN_SWORD ||
			material == Material.DIAMOND_SWORD ||
			material == Material.NETHERITE_SWORD;
	}

	public static boolean isPickaxeMaterial(Material material)
	{
		return switch (material)
			{
				case DIAMOND_PICKAXE,
					GOLDEN_PICKAXE,
					IRON_PICKAXE,
					NETHERITE_PICKAXE,
					STONE_PICKAXE,
					WOODEN_PICKAXE -> true;
				default -> false;
			};
	}

	public static boolean isLeavesMaterial(Material material)
	{
		return switch (material)
			{
				case ACACIA_LEAVES,
					AZALEA_LEAVES,
					BIRCH_LEAVES,
					DARK_OAK_LEAVES,
					FLOWERING_AZALEA_LEAVES,
					JUNGLE_LEAVES,
					MANGROVE_LEAVES,
					OAK_LEAVES,
					SPRUCE_LEAVES -> true;
				default -> false;
			};
	}

	@Deprecated
	public static Tier getTier(Material material)
	{
		throw new RuntimeException("Not yet implemented");
	}
}
