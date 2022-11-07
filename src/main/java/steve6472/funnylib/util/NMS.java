package steve6472.funnylib.util;

import net.minecraft.nbt.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 11/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class NMS
{
	private static final Map<Item, Integer> BURN_TIME = AbstractFurnaceBlockEntity.getFuel();

	public static int getBurnTime(Material material)
	{
		Item item = CraftMagicNumbers.getItem(material);
		return BURN_TIME.getOrDefault(item, 0);
	}

	public static boolean isFuel(Material material)
	{
		Item item = CraftMagicNumbers.getItem(material);
		return BURN_TIME.containsKey(item);
	}

	public static void addLore(ItemStack bukkitStack, String json)
	{
		try
		{
			boolean copy = false;
			CraftItemStack craftStack;
			if (bukkitStack instanceof CraftItemStack)
			{
				craftStack = (CraftItemStack) bukkitStack;
			} else
			{
				craftStack = CraftItemStack.asCraftCopy(bukkitStack);
				copy = true;
			}

			Field handle = craftStack.getClass().getDeclaredField("handle");
			handle.setAccessible(true);
			var nmsStack = (net.minecraft.world.item.ItemStack) handle.get(craftStack);

			// get Lore tag
			CompoundTag tag = nmsStack.getOrCreateTag();
			CompoundTag display = tag.contains("display", Tag.TAG_COMPOUND) ? tag.getCompound("display") : new CompoundTag();
			ListTag lore = display.getList("Lore", Tag.TAG_STRING);

			// add lore line
			lore.add(StringTag.valueOf(json));

			// put edited data back to itemstack
			display.put("Lore", lore);
			tag.put("display", display);

			if (copy)
			{
				bukkitStack.setItemMeta(craftStack.getItemMeta());
			}


			// debug
//			System.out.println("TAG: -> " + tag.getAsString());
		} catch (ReflectiveOperationException exception)
		{
			throw new RuntimeException(exception);
		}
	}

	public static void addLore(ItemStack bukkitStack, JSONMessage message)
	{
		try
		{
			boolean copy = false;
			CraftItemStack craftStack;
			if (bukkitStack instanceof CraftItemStack)
			{
				craftStack = (CraftItemStack) bukkitStack;
			} else
			{
				craftStack = CraftItemStack.asCraftCopy(bukkitStack);
				copy = true;
			}

			Field handle = craftStack.getClass().getDeclaredField("handle");
			handle.setAccessible(true);
			var nmsStack = (net.minecraft.world.item.ItemStack) handle.get(craftStack);

			// get Lore tag
			CompoundTag tag = nmsStack.getOrCreateTag();
			CompoundTag display = tag.contains("display", Tag.TAG_COMPOUND) ? tag.getCompound("display") : new CompoundTag();
			ListTag lore = display.getList("Lore", Tag.TAG_STRING);

			// add lore line
			String var0 = message.toJSON().toString();
			lore.add(StringTag.valueOf(var0));

			// put edited data back to itemstack
			display.put("Lore", lore);
			tag.put("display", display);

			if (copy)
			{
				bukkitStack.setItemMeta(craftStack.getItemMeta());
			}

			// debug
//			System.out.println(tag.getAsString());
		} catch (ReflectiveOperationException exception)
		{
			throw new RuntimeException(exception);
		}
	}
}