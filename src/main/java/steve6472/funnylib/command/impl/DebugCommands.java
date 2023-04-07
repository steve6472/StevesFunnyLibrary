package steve6472.funnylib.command.impl;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.command.Command;
import steve6472.funnylib.command.Description;
import steve6472.funnylib.command.Usage;
import steve6472.funnylib.coroutine.Coroutine;
import steve6472.funnylib.coroutine.CoroutineExecutor;
import steve6472.funnylib.json.JsonPrettify;
import steve6472.funnylib.util.*;
import steve6472.funnylib.util.generated.BlockGen;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

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


	@Command
	@Usage("/coroutineTest")
	public static boolean coroutineTest(@NotNull Player player, @NotNull String[] args)
	{
		new RepeatingTask((JavaPlugin) FunnyLib.getPlugin(), 0, 0)
		{
			@Override
			public void run()
			{
				for (Player onlinePlayer : Bukkit.getOnlinePlayers())
				{
					{
						String id = onlinePlayer.getName() + "_test1";
						CoroutineExecutor executor = Coroutine.getExecutor(id);
						if (executor != null)
						{
							executor.run();
							if (executor.ended())
								Coroutine.remove(id);
						}
					}
					{
						String id = onlinePlayer.getName() + "_test2";
						CoroutineExecutor executor = Coroutine.getExecutor(id);
						if (executor != null)
						{
							executor.run();
						}
					}
				}
			}
		}.sendStopMessage(player);
		return true;
	}



	@Command
	@Usage("/coroutineTest1")
	public static boolean coroutineTest1(@NotNull Player player, @NotNull String[] args)
	{
		Coroutine.create(player.getName() + "_test1")
			.WaitUntilTrue(player::isSneaking)
			.Loop(0, i -> i < 32, 1, l -> l
				.Loop(0, i -> i < 180, 1, l_ -> l_
					.Do(() -> player.getWorld().spawnParticle(Particle.COMPOSTER, player.getLocation().getX() + Math.sin(Math.toRadians(l_.i() * 2)), player.getLocation().getY() + l.i() / 16f, player.getLocation().getZ() + Math.cos(Math.toRadians(l_.i() * 2)), 1))
				)
				.Wait(1)
			)
			.finish();

		return true;
	}


	@Command
	@Usage("/coroutineTest2")
	public static boolean coroutineTest2(@NotNull Player player, @NotNull String[] args)
	{
		if (Coroutine.getExecutor(player.getName() + "_test2") != null)
		{
			Coroutine.remove(player.getName() + "_test2");
			return true;
		}

		Coroutine.create(player.getName() + "_test2")
			.If(() -> player.isSneaking() && player.isOnGround())
			.Loop(0, i -> i < 180, 1, l -> l
				.Do(() -> player.getWorld().spawnParticle(Particle.COMPOSTER, player.getLocation().getX() + Math.sin(Math.toRadians(l.i() * 2)), player.getLocation().getY(), player.getLocation().getZ() + Math.cos(Math.toRadians(l.i() * 2)), 1))
			)
			.Do(() -> player.setVelocity(player.getEyeLocation().getDirection()))
			.End()
			.finish();

		return true;
	}


	@Command
	@Usage("/coroutineTest3")
	public static boolean coroutineTest3(@NotNull Player player, @NotNull String[] args)
	{
		BoundingBox bb = new BoundingBox(-96, 111, -778, -95, 113, -776);
		World world = player.getLocation().getWorld();
		if (world == null)
			return false;
		Location loc = new Location(world, -94, 112, -779);
		Coroutine.create(player.getName() + "_test1")
			.WaitUntilTrue(20, () -> Bukkit.getOnlinePlayers().stream().anyMatch(p -> p.getBoundingBox().overlaps(bb)))
			.Loop(0, i -> i < 4, 1, l -> l
				.Do(() -> world.setBlockData(loc.clone().add(l.i() * 2, 0, 0), BlockGen.Lantern(false, false)))
				.Do(() -> world.setBlockData(loc.clone().add(l.i() * 2, 0, 3), BlockGen.Lantern(false, false)))
				.Wait(10)
			)
			.WaitUntilTrue(20, () -> Bukkit.getOnlinePlayers().stream().noneMatch(p -> p.getBoundingBox().overlaps(bb)))
			.Loop(0, i -> i < 4, 1, l -> l
				.Do(() -> world.setBlockData(loc.clone().add(l.i() * 2, 0, 0), Material.AIR.createBlockData()))
				.Do(() -> world.setBlockData(loc.clone().add(l.i() * 2, 0, 3), Material.AIR.createBlockData()))
			)
			.finish();

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