package steve6472.funnylib;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.builtin.MultiBlock;
import steve6472.funnylib.command.impl.DebugCommands;
import steve6472.funnylib.json.IJsonConfig;
import steve6472.funnylib.json.JsonConfig;
import steve6472.funnylib.json.JsonPrettify;
import steve6472.funnylib.json.codec.codecs.*;
import steve6472.funnylib.util.GlowingUtil;
import steve6472.funnylib.blocks.builtin.TeleportButtonBlock;
import steve6472.funnylib.command.AnnotationCommand;
import steve6472.funnylib.command.impl.BuiltInCommands;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.item.BlockPlacerItem;
import steve6472.funnylib.item.builtin.*;
import steve6472.funnylib.json.codec.Codec;
import steve6472.funnylib.menu.MenuListener;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.events.ArmorEventListener;
import steve6472.funnylib.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by steve6472
 * Date: 9/8/2022
 * Project: StevesFunnyLibrary
 */
public class FunnyLib
{
	private static final ServerTickEvent SERVER_TICK_EVENT = new ServerTickEvent();

	public static boolean NMS_FAILED = false;

	private static Plugin PLUGIN;
	private static long uptimeTicks;
	private static ArmorEventListener armorEventListener;
	private static MenuListener menuListener;
	private static Blocks blocks;
	private static JsonConfig config;

	private static Set<IJsonConfig> configurations;

	private FunnyLib()
	{
		new MavenSux();
	}

	public static void init(Plugin plugin, LibSettings settings)
	{
		new MavenSux();

		configurations = new HashSet<>();
		config = new JsonConfig(plugin);

//		if (FunnyLib.PLUGIN != null)
//			throw new RuntimeException("Plugin %s tried to initialize FunnyLib again. This is not allowed!".formatted(plugin.getName()));

		FunnyLib.PLUGIN = plugin;

		Log.init(plugin);
		GlowingUtil.init();

		// TODO: remove
		AnnotationCommand.registerCommands(MenuTest.class);

		AnnotationCommand.registerCommands(BuiltInCommands.class);
		AnnotationCommand.registerCommands(DebugCommands.class);
		Bukkit.getPluginManager().registerEvents(armorEventListener = new ArmorEventListener(), plugin);
		Bukkit.getPluginManager().registerEvents(menuListener = new MenuListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new CustomCommandRunner(), plugin);
		Bukkit.getPluginManager().registerEvents(new Items(), plugin);
		Bukkit.getPluginManager().registerEvents(blocks = new Blocks(), plugin);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
		{
			uptimeTicks++;
			armorEventListener.tick();
			SERVER_TICK_EVENT.setUptimeTick(uptimeTicks);
			try
			{
				Bukkit.getPluginManager().callEvent(SERVER_TICK_EVENT);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			Items.tick();

		}, 0, 0);

		initBuiltin();
	}

	public static void onUnload()
	{
		for (World world : Bukkit.getWorlds())
		{
			Blocks.saveWorld(world, true);
		}
	}

	public static Plugin getPlugin()
	{
		return PLUGIN;
	}

	public static long getUptimeTicks()
	{
		return uptimeTicks;
	}

	public static MenuListener getMenuListener()
	{
		return menuListener;
	}

	public static Blocks getBlocks()
	{
		return blocks;
	}

	public static JsonConfig getConfig()
	{
		return config;
	}

	public static void registerConfig(IJsonConfig config)
	{
		configurations.add(config);
	}

	public static void save()
	{
		configurations.forEach(c -> config.save(c::save));
	}

	public static void load()
	{
		configurations.forEach(c -> config.load(c::load));
	}

	private static class CustomCommandRunner implements Listener
	{
		@EventHandler
		public void commands(PlayerCommandPreprocessEvent e)
		{
			String command = e.getMessage().substring(1);
			BukkitCommand bukkitCommand;
			AnnotationCommand.CommandData commandData;
			if (command.contains(" "))
			{
				commandData = AnnotationCommand.commands.get(command.substring(0, command.indexOf(" ")).toLowerCase());
			} else
			{
				commandData = AnnotationCommand.commands.get(command.toLowerCase());
			}
			if (commandData == null)
				return;
			bukkitCommand = commandData.executor();

			if (bukkitCommand != null)
			{
				e.setCancelled(true);

				if (command.contains(" "))
				{
					String[] split = command.split(" +");
					String[] subarray = ArrayUtils.subarray(split, 1, split.length);
					bukkitCommand.execute(e.getPlayer(), "", subarray);
				}
				else
				{
					bukkitCommand.execute(e.getPlayer(), "", new String[0]);
				}
			}
		}
	}

	/*
	 * Built-in stuff
	 */

	public static CustomBlock TELEPORT_BUTTON_BLOCK;
	public static CustomBlock MULTI_BLOCK;

	public static CustomItem LOCATION_MARKER;
	public static CustomItem AREA_LOCATION_MARKER;
	public static CustomItem TELEPORT_BUTTON_ITEM;
	public static CustomItem ADMIN_WRENCH;
	public static CustomItem STRUCTURE;

	private static void initBuiltin()
	{
		Codec.registerCodec(new LocationCodec());
		Codec.registerCodec(new ItemStackCodec());
		Codec.registerCodec(new EntityCodec());
		Codec.registerCodec(new WorldCodec());
		Codec.registerCodec(new StringListCodec());

		Codec.regDefCodec(Location.class, new LocationCodec());
		Codec.regDefCodec(ItemStack.class, new ItemStackCodec());
		Codec.regDefCodec(Entity.class, new EntityCodec());
		Codec.regDefCodec(World.class, new WorldCodec());

		Blocks.registerBlock(TELEPORT_BUTTON_BLOCK = new TeleportButtonBlock());
		Blocks.registerBlock(MULTI_BLOCK = new MultiBlock());

		Items.registerAdminItem(LOCATION_MARKER = new MarkerItem());
		Items.registerAdminItem(AREA_LOCATION_MARKER = new AreaMarkerItem());
		Items.registerAdminItem(ADMIN_WRENCH = new AdminWrenchItem());
		Items.registerAdminItem(STRUCTURE = new StructureItem());

		Items.registerItem(TELEPORT_BUTTON_ITEM = new BlockPlacerItem(TELEPORT_BUTTON_BLOCK, "teleport_button", Material.STONE_BUTTON, "Teleport Button", 0));
	}
}
