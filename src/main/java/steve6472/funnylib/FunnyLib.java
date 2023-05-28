package steve6472.funnylib;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;
import steve6472.funnylib.blocks.Blocks;
import steve6472.funnylib.blocks.CustomBlock;
import steve6472.funnylib.blocks.CustomNoteBlocks;
import steve6472.funnylib.blocks.builtin.CustomNoteBlock;
import steve6472.funnylib.blocks.builtin.MultiBlock;
import steve6472.funnylib.command.impl.DebugCommands;
import steve6472.funnylib.json.IJsonConfig;
import steve6472.funnylib.json.INbtConfig;
import steve6472.funnylib.json.JsonConfig;
import steve6472.funnylib.json.JsonNBT;
import steve6472.funnylib.json.codec.codecs.*;
import steve6472.funnylib.serialize.PdcNBT;
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
import steve6472.funnylib.util.NMS;

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
	private static JsonConfig configJson, configNbt;
	private static LibSettings settings;

	private static Set<IJsonConfig> configurationsJson;
	private static Set<INbtConfig> configurationsNbt;

	private FunnyLib()
	{
		new MavenSux();
	}

	public static void init(Plugin plugin, LibSettings settings)
	{
		new MavenSux();

		configurationsJson = new HashSet<>();
		configurationsNbt = new HashSet<>();
		configJson = new JsonConfig("config", plugin);
		configNbt = new JsonConfig("config_nbt", plugin);
		FunnyLib.settings = settings;

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

		if (settings.enableCustomNoteBlocks)
		{
			Bukkit.getPluginManager().registerEvents(new CustomNoteBlocks(), plugin);
		}

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

	public static LibSettings getSettings()
	{
		return settings;
	}

	public static void registerConfig(IJsonConfig config)
	{
		configurationsJson.add(config);
	}

	public static void registerConfig(INbtConfig config)
	{
		configurationsNbt.add(config);
	}

	public static void save()
	{
		configurationsJson.forEach(c -> configJson.save(c::save));
		configurationsNbt.forEach(c -> configNbt.save(json ->
		{
			PdcNBT nbt = PdcNBT.fromPDC(NMS.newCraftContainer());
			c.save(nbt);
			JSONObject jsonObject = JsonNBT.containertoJSON(nbt.getContainer());
			for (String s : jsonObject.keySet())
			{
				json.put(s, jsonObject.get(s));
			}
		}));
	}

	public static void load()
	{
		configurationsJson.forEach(c -> configJson.load(c::load));
		configurationsNbt.forEach(c -> configNbt.load(nbt ->
		{
			c.load(PdcNBT.fromPDC(JsonNBT.JSONtoNBT(nbt)));
		}));
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
	public static CustomBlock NOTE_BLOCK;

	public static CustomItem LOCATION_MARKER;
	public static CustomItem AREA_LOCATION_MARKER;
	public static CustomItem TELEPORT_BUTTON_ITEM;
	public static CustomItem ADMIN_WRENCH;
	public static CustomItem STRUCTURE;
	public static CustomItem ENTITY_HITBOX_DEBUGGER;
	public static CustomItem ITEM_DISPLAY_EDITOR;
	public static CustomItem NOTE_BLOCK_TUNER;

	private static void initBuiltin()
	{
		Codec.registerCodec(new ItemStackCodec());
		Codec.registerCodec(new EntityCodec());

		Codec.regDefCodec(ItemStack.class, new ItemStackCodec());
		Codec.regDefCodec(Entity.class, new EntityCodec());

		Blocks.registerBlock(TELEPORT_BUTTON_BLOCK = new TeleportButtonBlock());
		Blocks.registerBlock(MULTI_BLOCK = new MultiBlock());

		if (getSettings().enableCustomNoteBlocks)
		{
			Blocks.registerBlock(NOTE_BLOCK = new CustomNoteBlock());
		}

		Items.registerAdminItem(LOCATION_MARKER = new MarkerItem());
		Items.registerAdminItem(AREA_LOCATION_MARKER = new AreaMarkerItem());
		Items.registerAdminItem(ADMIN_WRENCH = new AdminWrenchItem());
		Items.registerAdminItem(STRUCTURE = new StructureItem());
		Items.registerAdminItem(ENTITY_HITBOX_DEBUGGER = new EntityHitboxDebuger());
		Items.registerAdminItem(ITEM_DISPLAY_EDITOR = new ItemDisplayEditor());

		Items.registerItem(TELEPORT_BUTTON_ITEM = new BlockPlacerItem(TELEPORT_BUTTON_BLOCK, "teleport_button", Material.STONE_BUTTON, "Teleport Button", 0));

		Items.registerItem(NOTE_BLOCK_TUNER = new NoteBlockTuner());
	}
}
