package steve6472.funnylib.command;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import steve6472.funnylib.json.JsonPrettify;
import steve6472.funnylib.util.*;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/7/2022
 * Project: StevesFunnyLibrary <br>
 */
public class DebugCommands
{
	@Command
	@Description("Tests serialization")
	@Usage("/testSer")
	public static boolean testSer(@NotNull Player player, @NotNull String[] args)
	{
		JSONObject json = MiscUtil.serializeItemStack(player.getInventory().getItemInMainHand());
		System.out.println(JsonPrettify.prettify(json));
		ItemStack deserialize = MiscUtil.deserializeItemStack(json);
		player.getInventory().addItem(deserialize);
		return true;
	}


	@Command
	@Description("Prints item into console")
	@Usage("/printItem")
	@Usage("[-p] -> send message to player as well")
	public static boolean printItem(@NotNull Player player, @NotNull String[] args)
	{
		System.out.println(player.getInventory().getItemInMainHand());
		if (hasFlag("-p", args))
			player.sendMessage(player.getInventory().getItemInMainHand().toString());
		return true;
	}


	@Command
	@Description("Prints item lore into console")
	@Usage("/printItemLore")
	@Usage("[-p] -> send message to player as well")
	public static boolean printItemLore(@NotNull Player player, @NotNull String[] args)
	{
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null)
		{
			System.out.println("null meta");
			if (hasFlag("-p", args))
				player.sendMessage(ChatColor.RED + "null meta");
		} else
		{
			List<String> lore = itemMeta.getLore();
			System.out.println("Lore: " + lore);
			if (hasFlag("-p", args))
				player.sendMessage("Lore: " + lore);
		}
		return true;
	}


	@Command
	@Description("Prints item nms tag")
	@Usage("/printVanillaTag")
	@Usage("[-p] -> send message to player as well")
	public static boolean printVanillaTag(@NotNull Player player, @NotNull String[] args)
	{
		ItemStack itemStack = player.getInventory().getItemInMainHand();

		try
		{
			CraftItemStack craftStack = (CraftItemStack) itemStack;
			Field handle = craftStack.getClass().getDeclaredField("handle");
			handle.setAccessible(true);
			var nmsStack = (net.minecraft.world.item.ItemStack) handle.get(craftStack);
			CompoundTag tag = nmsStack.getOrCreateTag();
			System.out.println(tag.getAsString());
			if (hasFlag("-p", args))
				player.sendMessage(tag.getAsString());
		} catch (ReflectiveOperationException exception)
		{
			throw new RuntimeException(exception);
		}

		return true;
	}


	@Command
	@Usage("/test1")
	public static boolean test1(@NotNull Player player, @NotNull String[] args)
	{
		JSONMessage color = JSONMessage.create("").then(JSONMessage.create("a").then("b")).color(ChatColor.RED);
		player.sendMessage(color.toJSON().toString());
		color.send(player);

		return true;
	}


	public static boolean hasFlag(String flag, String[] args)
	{
		for (String arg : args)
		{
			if (arg.equals(flag))
			{
				return true;
			}
		}

		return false;
	}
}
