package steve6472.funnylib.blocks;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.blocks.events.BreakBlockEvent;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.blocks.stateengine.properties.IProperty;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.json.codec.Codec;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Blocks implements Listener
{
	public static final int CREATE_DATA = 1;

	public static final int DEFAULT_PLACE_FLAGS = CREATE_DATA;

	public static final Map<String, CustomBlock> BLOCKS = new HashMap<>();
	private static final NamespacedKey CUSTOM_BLOCKS_KEY = new NamespacedKey(FunnyLib.getPlugin(), "custom_blocks");

	public static void registerBlock(CustomBlock customBlock)
	{
		BLOCKS.put(customBlock.id(), customBlock);
	}

	public static CustomBlock getCustomBlockById(String id)
	{
		return BLOCKS.get(id);
	}

	/*
	 * Events
	 */

	@EventHandler
	public void tick(ServerTickEvent e)
	{
		List<Chunk> toLoad = new ArrayList<>();

		for (World world : Bukkit.getWorlds())
		{
			for (Chunk loadedChunk : world.getLoadedChunks())
			{
				CustomChunk chunk = CHUNK_MAP.get(loadedChunk);
				if (chunk == null)
				{
					toLoad.add(loadedChunk);
					continue;
				}

				for (int key : chunk.ticking)
				{
					State state = chunk.blocks.get(key);
					((BlockTick) state.getObject())
						.tick(
							state,
							new Location(world, CustomChunk.keyToX(key) + loadedChunk.getX() * 16, CustomChunk.keyToY(key), CustomChunk.keyToZ(key) + loadedChunk.getZ() * 16),
							chunk.blockData.get(key)
						);
				}
			}
		}

		for (Chunk chunk : toLoad)
		{
			CustomChunk customChunk = new CustomChunk(chunk);
			String custom_blocks = chunk
				.getPersistentDataContainer()
				.get(CUSTOM_BLOCKS_KEY, PersistentDataType.STRING);
			customChunk.fromJson(new JSONObject(custom_blocks == null ? "{}" : custom_blocks));
			CHUNK_MAP.put(chunk, customChunk);
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e)
	{
		if (e.isCancelled())
			return;

		Location location = e.getBlock().getLocation();

		CustomChunk chunk = CHUNK_MAP.get(e.getBlock().getChunk());
		State blockState = chunk.getBlockState(location);
		if (blockState.getObject() instanceof BreakBlockEvent bbe)
		{
			bbe.breakBlock(e.getPlayer().getInventory().getItem(EquipmentSlot.HAND), blockState, chunk.getBlockData(location), e);
		}
		chunk.setBlockState(location, null);
	}

	@EventHandler
	public void click(PlayerInteractEvent e)
	{
		Block block = e.getClickedBlock();
		if (block == null) return;

		State state = getBlockState(e.getClickedBlock().getLocation());
		if (state == null) return;

		CustomBlock customBlock = ((CustomBlock) state.getObject());

		if (customBlock instanceof BlockClickEvents bce)
		{
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				bce.rightClick(state, e.getItem(), e.getPlayer(), e);
			}
			if (e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				bce.leftClick(state, e.getItem(), e.getPlayer(), e);
			}
		}
	}

	/*
	 * Fancy stuff
	 */

	// TODO: maybe replace with long map . . . inside world hash map
	public static final Map<Chunk, CustomChunk> CHUNK_MAP = new HashMap<>();

	@EventHandler
	public void loadChunk(ChunkLoadEvent e)
	{
		CustomChunk customChunk = new CustomChunk(e.getChunk());
		String custom_blocks = e
			.getChunk()
			.getPersistentDataContainer()
			.get(CUSTOM_BLOCKS_KEY, PersistentDataType.STRING);
		customChunk.fromJson(new JSONObject(custom_blocks == null ? "{}" : custom_blocks));
		CHUNK_MAP.put(e.getChunk(), customChunk);
	}

	@EventHandler
	public void unloadChunk(ChunkUnloadEvent e)
	{
		CustomChunk customChunk = CHUNK_MAP.get(e.getChunk());
		JSONObject json = new JSONObject();
		customChunk.toJson(json);
		e.getChunk().getPersistentDataContainer().set(CUSTOM_BLOCKS_KEY, PersistentDataType.STRING, json.toString());
	}

	@EventHandler
	public void worldSave(WorldSaveEvent e)
	{
		saveWorld(e.getWorld());
	}

	public static void saveWorld(World world)
	{
		for (Chunk loadedChunk : world.getLoadedChunks())
		{
			CustomChunk customChunk = CHUNK_MAP.get(loadedChunk);
			JSONObject json = new JSONObject();
			customChunk.toJson(json);
			loadedChunk.getPersistentDataContainer().set(CUSTOM_BLOCKS_KEY, PersistentDataType.STRING, json.toString());
		}
	}

	public static void setBlockState(Location location, State state)
	{
		CHUNK_MAP.get(location.getChunk()).setBlockState(location, state);
	}

	public static void setBlockState(Location location, State state, int flags)
	{
		CHUNK_MAP.get(location.getChunk()).setBlockState(location, state, flags);
	}

	public static State getBlockState(Location location)
	{
		return CHUNK_MAP.get(location.getChunk()).getBlockState(location);
	}

	public static CustomBlockData getBlockData(Location location)
	{
		return CHUNK_MAP.get(location.getChunk()).getBlockData(location);
	}

	public static <T extends CustomBlockData> T getBlockData(Location location, Class<T> expectedType)
	{
		CustomBlockData blockData = getBlockData(location);
		if (blockData.getClass().isAssignableFrom(expectedType))
			return (T) blockData;
		throw new RuntimeException("Expected type " + expectedType.getName()+ ", got " + blockData.getClass().getName());
	}

	public static void setBlockData(Location location, CustomBlockData data)
	{
		CHUNK_MAP.get(location.getChunk()).setBlockData(location, data);
	}

	/*
	 * Save Util
	 */

	public static JSONObject stateToJson(State state)
	{
		JSONObject json = new JSONObject();
		CustomBlock block = ((CustomBlock) state.getObject());
		json.put("id", block.id());
		if (state.getProperties() != null)
		{
			state.getProperties().forEach((k, v) -> json.put(k.getName(), k.toString(v)));
		}
		return json;
	}

	public static State jsonToState(JSONObject json)
	{
		State defaultState = Blocks.getCustomBlockById(json.getString("id")).getDefaultState();
		State state = defaultState;
		if (state.getProperties() != null)
		{
			for (IProperty iProperty : defaultState.getProperties().keySet())
			{
				String string = json.getString(iProperty.getName());
				state = state.with(iProperty, iProperty.fromString(string));
			}
		}
		return state;
	}

	public static JSONObject dataToJson(CustomBlockData data)
	{
		JSONObject blockData = Codec.save(data);
		JSONObject json = new JSONObject();
		json.put("blockData", blockData);
		json.put("dataClass", data.getClass().getName());
		json.put("blockId", data.getBlock().id());
		json.put("worldName", data.worldName);
		json.put("x", data.x);
		json.put("y", data.y);
		json.put("z", data.z);
		return json;
	}

	public static CustomBlockData jsonToData(JSONObject json)
	{
		// TODO: replace this with registry
		String classPath = json.getString("dataClass");
		try
		{
			Class<?> clazz = Class.forName(classPath);
			Object o = clazz.getConstructor().newInstance();
			CustomBlockData blockData = (CustomBlockData) Codec.load(o, json.getJSONObject("blockData"));

			CustomBlock block = Blocks.getCustomBlockById(json.getString("blockId"));
			blockData.setLogic(block);
			blockData.setLocation(Bukkit.getWorld(json.getString("worldName")), json.getInt("x"), json.getInt("y"), json.getInt("z"));

			return blockData;
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
		         InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
}
