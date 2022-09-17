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

	@Deprecated
	public static Tier getTier(Material material)
	{
		throw new RuntimeException("Not yet implemented");
	}
}
