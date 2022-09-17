package steve6472.funnylib;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.builtin.SilkLeavesBlock;
import steve6472.funnylib.blocks.builtin.TeleportButtonBlock;
import steve6472.funnylib.command.AnnotationCommand;
import steve6472.funnylib.command.BuiltInCommands;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.item.BlockPlacerItem;
import steve6472.funnylib.item.builtin.*;
import steve6472.funnylib.json.codec.Codec;
import steve6472.funnylib.json.codec.codecs.EntityCodec;
import steve6472.funnylib.json.codec.codecs.ItemStackCodec;
import steve6472.funnylib.json.codec.codecs.LocationCodec;
import steve6472.funnylib.menu.MenuListener;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.events.ArmorEventListener;
import steve6472.funnylib.util.Log;

/**
 * Created by steve6472
 * Date: 9/8/2022
 * Project: StevesFunnyLibrary
 */
public class FunnyLib
{
	private static final ServerTickEvent SERVER_TICK_EVENT = new ServerTickEvent();

	private static Plugin PLUGIN;
	private static long uptimeTicks;
	private static ArmorEventListener armorEventListener;
	private static MenuListener menuListener;

	private FunnyLib()
	{
		new JSONObject();
		Class<JSONString> jsonStringClass = JSONString.class;
	}

	public static void init(Plugin plugin, boolean builtInItems)
	{
//		if (FunnyLib.PLUGIN != null)
//			throw new RuntimeException("Plugin %s tried to initialize FunnyLib again. This is not allowed!".formatted(plugin.getName()));

		FunnyLib.PLUGIN = plugin;

		Log.init(plugin);

		// TODO: remove
		AnnotationCommand.registerCommands(MenuTest.class);

		AnnotationCommand.registerCommands(BuiltInCommands.class);
		Bukkit.getPluginManager().registerEvents(armorEventListener = new ArmorEventListener(), plugin);
		Bukkit.getPluginManager().registerEvents(menuListener = new MenuListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new CustomCommandRunner(), plugin);
		Bukkit.getPluginManager().registerEvents(new Items(), plugin);
		Bukkit.getPluginManager().registerEvents(new Blocks(), plugin);

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
			Blocks.saveWorld(world);
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

	public static CustomItem LOCATION_MARKER;
	public static CustomItem AREA_LOCATION_MARKER;
	public static CustomItem TELEPORT_BUTTON_ITEM;
	public static CustomItem ADMIN_WRENCH;

	/*
	 * Skyblock Beginnings
	 */
	public static CustomBlock SILK_LEAVES;

	public static CustomItem WOODEN_CROOCK;
	public static CustomItem SILKWORM;

	private static void initBuiltin()
	{
		Codec.registerCodec(new LocationCodec());
		Codec.registerCodec(new ItemStackCodec());
		Codec.registerCodec(new EntityCodec());

		Blocks.registerBlock(TELEPORT_BUTTON_BLOCK = new TeleportButtonBlock());
		Blocks.registerBlock(SILK_LEAVES = new SilkLeavesBlock());

		Items.registerAdminItem(LOCATION_MARKER = new MarkerItem());
		Items.registerAdminItem(AREA_LOCATION_MARKER = new AreaMarkerItem());
		Items.registerAdminItem(ADMIN_WRENCH = new AdminWrenchItem());

		Items.registerItem(TELEPORT_BUTTON_ITEM = new BlockPlacerItem(TELEPORT_BUTTON_BLOCK, "teleport_button", Material.STONE_BUTTON, "Teleport Button", 0));
		Items.registerItem(WOODEN_CROOCK = new WoodenCroockItem());
		Items.registerItem(SILKWORM = new SilkwormItem());
	}
}
