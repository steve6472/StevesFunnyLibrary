package steve6472.funnylib.util;

import net.minecraft.world.item.Tier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
		return Tag.LEAVES.isTagged(material);
	}

	private static final Map<Material, FurnaceRecipe> COOK_TIME_MAP = new HashMap<>();

	public static FurnaceRecipe getFurnaceRecipeByInput(ItemStack itemStack)
	{
		return COOK_TIME_MAP.computeIfAbsent(itemStack.getType(), m ->
		{
			for (Iterator<Recipe> iterator = Bukkit.recipeIterator(); iterator.hasNext(); )
			{
				Recipe recipe = iterator.next();
				if (recipe instanceof FurnaceRecipe fr)
				{
					if (fr.getInputChoice().test(itemStack))
					{
						return fr;
					}
//					if (fr.getInput().getType() == m)
//					{
//						return fr;
//					}
				}
			}

			return null;
		});
	}

	public static int getCookTime(ItemStack itemStack)
	{
		FurnaceRecipe furnaceRecipe = getFurnaceRecipeByInput(itemStack);
		return furnaceRecipe == null ? 0 : furnaceRecipe.getCookingTime();
	}

	@Deprecated
	public static Tier getTier(Material material)
	{
		throw new RuntimeException("Not yet implemented");
	}

	/*
	 * NMS
	 */

	public static int getBurnTime(Material material)
	{
		return SafeNMS.nmsFunction(() -> NMS.getBurnTime(material), 0);
	}

	public static boolean isFuel(Material material)
	{
		return SafeNMS.nmsFunction(() -> NMS.isFuel(material), false);
	}
}
