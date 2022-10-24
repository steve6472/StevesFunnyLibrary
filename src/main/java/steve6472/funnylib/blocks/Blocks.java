package steve6472.funnylib.blocks;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import steve6472.funnylib.*;
import steve6472.funnylib.blocks.events.BlockBreakResult;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.blocks.events.BreakBlockEvent;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.blocks.stateengine.properties.IProperty;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.json.codec.Codec;
import steve6472.funnylib.util.Log;
import steve6472.funnylib.util.MetaUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Blocks implements Listener
{
	/*
	 * Flags
	 */
	public static final int CREATE_DATA = 1;
	public static final int CALL_ON_REMOVE = 2;

	/*
	 * Flag Configurations
	 */
	public static final int DEFAULT_PLACE_FLAGS = CREATE_DATA | CALL_ON_REMOVE;
	public static final int CHANGE_STATE_FLAGS = 0;

	/*
	 * Visual Separator
	 */

	public static final Map<String, CustomBlock> BLOCKS = new HashMap<>();
	public static final NamespacedKey CUSTOM_BLOCKS_KEY = new NamespacedKey(FunnyLib.getPlugin(), "custom_blocks");
	@Nullable
	public static Chunk currentLoadingChunk = null;

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

				for (IntIterator iterator = chunk.ticking.iterator(); iterator.hasNext(); )
				{
					int key = iterator.next();
					State state = chunk.blocks.get(key);
					if (state == null)
					{
						iterator.remove();
					} else
					{
						Location location = new Location(world, CustomChunk.keyToX(key) + loadedChunk.getX() * 16, CustomChunk.keyToY(key), CustomChunk.keyToZ(key) + loadedChunk.getZ() * 16);
						try
						{
							((BlockTick) state.getObject()).tick(new BlockContext(location, state));
						} catch (Exception ex)
						{
							Log.error("Error while ticking custom block at location " + location);
							ex.printStackTrace();
						}
					}
				}
			}
		}

		for (Chunk chunk : toLoad)
		{
			loadChunk(chunk);
		}
	}

	public static boolean callBlockBreak(PlayerBlockContext context)
	{
		BlockBreakEvent e = new BlockBreakEvent(context.getBlock(), context.getPlayer());
		FunnyLib.getBlocks().blockBreak(e);
		return e.isCancelled();
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e)
	{
		if (e.isCancelled())
			return;

		Location location = e.getBlock().getLocation();

		CustomChunk chunk = CHUNK_MAP.get(e.getBlock().getChunk());
		State blockState = chunk.getBlockState(location);
		World world = e.getBlock().getWorld();
		if (blockState != null)
		{
			CustomBlock block = ((CustomBlock) blockState.getObject());
			PlayerContext playerContext = new PlayerContext(e.getPlayer());
			BlockFaceContext blockContext = new BlockFaceContext(location, MetaUtil.getValue(e.getPlayer(), BlockFace.class, "last_face"));

			if (!block.canPlayerBreak(new PlayerBlockContext(playerContext, blockContext)))
			{
				e.setCancelled(true);
				return;
			}

			boolean dropItems = true;

			if (blockState.getObject() instanceof BreakBlockEvent bbe)
			{
				BlockBreakResult result = new BlockBreakResult();
				bbe.playerBreakBlock(playerContext, blockContext, result);
				dropItems = result.dropsItems();

				e.setCancelled(result.isCancelled());

				if (result.isResultChanged())
				{
					if (result.getResultCustomBlock() != null)
					{
						chunk.setBlockState(location, result.getResultCustomBlock().getDefaultState());
					} else if (result.getResultState() != null)
					{
						chunk.setBlockState(location, result.getResultState());
					} else
					{
						e.getBlock().setBlockData(result.getResultBlock());
					}
				}
			} else
			{
				e.getBlock().setType(Material.AIR);
			}

			if (dropItems && Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_TILE_DROPS)))
			{
				List<ItemStack> drops = new ArrayList<>();
				block.getDrops(new PlayerBlockContext(playerContext, blockContext), drops);
				dropItems(location, drops);
			}

			e.setDropItems(block.vanillaBlockDrops());
			if (!e.isCancelled())
			{
				chunk.setBlockState(location, null);
			}
		}
	}

	@EventHandler
	public void explodeBlock(BlockExplodeEvent e)
	{
		if (e.isCancelled())
			return;

		Location location = e.getBlock().getLocation();

		CustomChunk chunk = CHUNK_MAP.get(e.getBlock().getChunk());
		State blockState = chunk.getBlockState(location);
		World world = e.getBlock().getWorld();
		if (blockState != null)
		{
			CustomBlock block = (CustomBlock) blockState.getObject();
			BlockContext blockContext = new BlockContext(location, blockState);
			List<ItemStack> drops = new ArrayList<>();
			if (!block.canBreakByExplosion(blockContext))
			{
				e.setCancelled(true);
			} else
			{
				if (Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_TILE_DROPS)))
				{
					block.getDrops(blockContext, drops);
					dropItems(location, drops);
				}
			}
		}
	}

	@EventHandler
	public void iHatePistons(BlockPistonExtendEvent e)
	{
		for (Block block : e.getBlocks())
		{
			if (getBlockState(block.getLocation()) != null)
			{
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void iHatePistons2ElectricBoogaloo(BlockPistonRetractEvent e)
	{
		for (Block block : e.getBlocks())
		{
			if (getBlockState(block.getLocation()) != null)
			{
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void blockBurn(BlockBurnEvent e)
	{
		State blockState = getBlockState(e.getBlock().getLocation());
		if (blockState == null)
			return;

		CustomBlock block = (CustomBlock) blockState.getObject();
		if (!block.canBurn())
		{
			e.setCancelled(true);
		} else
		{
			block.onBurn(new BlockContext(e.getBlock().getLocation(), blockState));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void setLastFace(PlayerInteractEvent e)
	{
		MetaUtil.setMeta(e.getPlayer(), "last_face", e.getBlockFace());
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
			PlayerBlockContext context = new PlayerBlockContext(new PlayerContext(e.getPlayer(), e.getHand()), new BlockFaceContext(block.getLocation(), e.getBlockFace()));

			if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				bce.rightClick(context, e);
			}
			if (e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				bce.leftClick(context, e);
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
		loadChunk(e.getChunk());
	}

	public static void loadChunk(Chunk chunk)
	{
		currentLoadingChunk = chunk;
		CustomChunk customChunk = new CustomChunk(chunk);
		String custom_blocks = chunk
			.getPersistentDataContainer()
			.get(CUSTOM_BLOCKS_KEY, PersistentDataType.STRING);
		customChunk.fromJson(new JSONObject(custom_blocks == null ? "{}" : custom_blocks));
		CHUNK_MAP.put(chunk, customChunk);
		currentLoadingChunk = null;
	}

	@EventHandler
	public void unloadChunk(ChunkUnloadEvent e)
	{
		saveChunk(e.getChunk(), true);
	}

	public static void saveChunk(Chunk chunk, boolean unloading)
	{
		CustomChunk customChunk = CHUNK_MAP.get(chunk);
		JSONObject json = new JSONObject();
		customChunk.toJson(json, unloading);
		chunk.getPersistentDataContainer().set(CUSTOM_BLOCKS_KEY, PersistentDataType.STRING, json.toString());
	}

	@EventHandler
	public void worldSave(WorldSaveEvent e)
	{
		saveWorld(e.getWorld(), false);
	}

	public static void saveWorld(World world, boolean unloadChunks)
	{
		for (Chunk loadedChunk : world.getLoadedChunks())
		{
			saveChunk(loadedChunk, unloadChunks);
		}
	}

	public static void setBlockState(Location location, State state)
	{
		CHUNK_MAP.get(location.getChunk()).setBlockState(location, state);
	}

	public static void changeBlockState(Location location, State state)
	{
		CHUNK_MAP.get(location.getChunk()).changeBlockState(location, state);
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
			//noinspection unchecked
			return (T) blockData;
		throw new RuntimeException("Expected type " + expectedType.getName()+ ", got " + blockData.getClass().getName());
	}

	public static void setBlockData(Location location, CustomBlockData data)
	{
		CHUNK_MAP.get(location.getChunk()).setBlockData(location, data);
	}

	/*
	 * Util
	 */

	private void dropItems(Location location, Collection<ItemStack> items)
	{
		World world = location.getWorld();
		if (world == null)
		{
			return;
		}

		for (ItemStack drop : items)
		{
			if (!drop.getType().isAir() || !drop.getType().isItem())
			{
				world.dropItemNaturally(location, drop);
			}
		}
	}

	/*
	 * Save Util
	 */

	/*
	 * State
	 */

	public static JSONObject stateToJson(State state, boolean unloading)
	{
		JSONObject json = new JSONObject();
		CustomBlock block = ((CustomBlock) state.getObject());
		json.put("id", block.id());
		if (state.getProperties() != null)
		{
			state.getProperties().forEach((k, v) -> json.put(k.getName(), k.toString(v)));
		}
		block.save(json, unloading);
		return json;
	}

	public static State jsonToState(JSONObject json)
	{
		State defaultState = Blocks.getCustomBlockById(json.getString("id")).getDefaultState();
		if (defaultState == null)
			throw new RuntimeException("Block '" + json.getString("id") + "' returned null default state");
		State state = defaultState;
		if (state.getProperties() != null)
		{
			//noinspection rawtypes
			for (IProperty iProperty : defaultState.getProperties().keySet())
			{
				String string = json.getString(iProperty.getName());
				//noinspection unchecked
				state = state.with(iProperty, iProperty.fromString(string));
			}
		}
		((CustomBlock) state.getObject()).load(json);
		return state;
	}

	/*
	 * Block Data
	 */

	public static JSONObject dataToJson(CustomBlockData data, boolean unloading)
	{
		JSONObject blockData = Codec.save(data);
		JSONObject json = new JSONObject();
		json.put("blockData", blockData);
		json.put("dataClass", data.getClass().getName());
		json.put("blockId", data.getBlock().id());
		json.put("worldName", data.pos.getWorld().getName());
		json.put("x", data.pos.getBlockX());
		json.put("y", data.pos.getBlockY());
		json.put("z", data.pos.getBlockZ());
		data.save(blockData, unloading);
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
			JSONObject data = json.getJSONObject("blockData");
			CustomBlockData blockData = (CustomBlockData) Codec.load(o, data);

			CustomBlock block = Blocks.getCustomBlockById(json.getString("blockId"));
			blockData.setLogic(block);
			Location location = new Location(Bukkit.getWorld(json.getString("worldName")), json.getInt("x"), json.getInt("y"), json.getInt("z"));
			blockData.setPos(location);
			blockData.load(data);

			return blockData;
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
		         InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
}
