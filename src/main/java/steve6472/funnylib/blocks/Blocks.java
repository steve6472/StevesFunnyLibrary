package steve6472.funnylib.blocks;

import it.unimi.dsi.fastutil.ints.IntIterator;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import steve6472.funnylib.*;
import steve6472.funnylib.blocks.events.BlockBreakResult;
import steve6472.funnylib.blocks.events.BlockClickEvents;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.blocks.events.BreakBlockEvent;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.blocks.stateengine.properties.IProperty;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.context.BlockFaceContext;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.events.ServerTickEvent;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.serialize.ChunkNBT;
import steve6472.funnylib.util.Log;
import steve6472.funnylib.util.MetaUtil;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.Pair;

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
				if (!loadedChunk.isEntitiesLoaded())
					continue;

				Chunk.LoadLevel loadLevel = loadedChunk.getLoadLevel();
				if (loadLevel == Chunk.LoadLevel.INACCESSIBLE || loadLevel == Chunk.LoadLevel.BORDER)
					continue;

				CustomChunk chunk = getCustomChunk(loadedChunk);
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
							((BlockTick) state.getObject()).tick(new BlockContext(location, state, chunk.getBlockData(location)));
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

		CustomChunk chunk = getCustomChunk(e.getBlock().getChunk());
		State blockState = chunk.getBlockState(location);
		World world = e.getBlock().getWorld();
		if (blockState != null)
		{
			CustomBlock block = ((CustomBlock) blockState.getObject());

			if (!Items.callWithItemContextR(e.getPlayer(), EquipmentSlot.HAND, ic -> block.canPlayerBreak(new PlayerBlockContext(ic, new BlockFaceContext(location, MetaUtil.getValue(e.getPlayer(), BlockFace.class, "last_face"))))))
			{
				e.setCancelled(true);
				return;
			}

			boolean dropItems = true;

			if (blockState.getObject() instanceof BreakBlockEvent bbe)
			{
				BlockBreakResult result = new BlockBreakResult();
				Items.callWithItemContext(e.getPlayer(), EquipmentSlot.HAND, ic -> bbe.playerBreakBlock(ic, new BlockFaceContext(location, MetaUtil.getValue(e.getPlayer(), BlockFace.class, "last_face")), result));
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
				Items.callWithItemContext(e.getPlayer(), EquipmentSlot.HAND, ic -> block.getDrops(new PlayerBlockContext(ic, new BlockFaceContext(location, MetaUtil.getValue(e.getPlayer(), BlockFace.class, "last_face"))), drops));
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

		CustomChunk chunk = getCustomChunk(e.getBlock().getChunk());
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
	public void physics(BlockPhysicsEvent e)
	{
		// TODO: physics updates
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
			// TODO: remove the custom state
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
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				Items.callWithItemContext(e.getPlayer(), e.getHand(), ic -> bce.rightClick(new PlayerBlockContext(ic, new BlockFaceContext(block.getLocation(), e.getBlockFace())), e));
			} else if (e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				Items.callWithItemContext(e.getPlayer(), e.getHand(), ic -> bce.leftClick(new PlayerBlockContext(ic, new BlockFaceContext(block.getLocation(), e.getBlockFace())), e));
			}
		}
	}

	/*
	 * Fancy stuff
	 */

	// TODO: maybe replace with long map . . . inside world hash map
	private static final Map<Chunk, CustomChunk> CHUNK_MAP = new HashMap<>();

	public static void addCustomChunk(Chunk chunk, CustomChunk customChunk)
	{
		CHUNK_MAP.put(chunk, customChunk);
		Log.debug("Added custom chunk " + chunk.getX() + "/" + chunk.getZ());
	}

	public static void removeCustomChunk(Chunk chunk)
	{
		CustomChunk remove = CHUNK_MAP.remove(chunk);
		if (remove == null)
			Log.warning("Tried to remove non custom chunk " + chunk.getX() + "/" + chunk.getZ());
		Log.debug("Removed custom chunk " + chunk.getX() + "/" + chunk.getZ() + ", cur= " + CHUNK_MAP.size());
	}

	public static CustomChunk getCustomChunk(Chunk chunk)
	{
		return CHUNK_MAP.get(chunk);
	}

	public static Map<Chunk, CustomChunk> getCustomChunks()
	{
		return CHUNK_MAP;
	}

	@EventHandler
	public void loadChunk(ChunkLoadEvent e)
	{
		loadChunk(e.getChunk());
	}

	@EventHandler
	public void unloadChunk(ChunkUnloadEvent e)
	{
		saveChunk(e.getChunk(), true);

		Log.debug("Chunk " + e.getChunk().getX() + "/" + e.getChunk().getZ() + " unloaded (save=" + e.isSaveChunk() + ", async=" + e.isAsynchronous() + ")");
	}

	public static void loadChunk(Chunk chunk)
	{
		CustomChunk customChunk = new CustomChunk(chunk);
		ChunkNBT chunkNBT = ChunkNBT.create(chunk);
		if (chunkNBT.hasCompound("custom_blocks"))
		{
			NBT customBlocks = chunkNBT.getCompound("custom_blocks");
			customChunk.fromNBT(customBlocks);
		}
		addCustomChunk(chunk, customChunk);
	}

	public static void saveChunk(Chunk chunk, boolean unloading)
	{
		CustomChunk customChunk = getCustomChunk(chunk);
		if (customChunk == null)
			return;
		if (unloading)
		{
			customChunk.unload();
			removeCustomChunk(chunk);
		}
		ChunkNBT chunkNBT = ChunkNBT.create(chunk);
		NBT customBlocks = chunkNBT.createCompound();
		customChunk.toNBT(customBlocks);
		chunkNBT.setCompound("custom_blocks", customBlocks);
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
		getCustomChunk(location.getChunk()).setBlockState(location, state);
	}

	public static void changeBlockState(Location location, State state)
	{
		getCustomChunk(location.getChunk()).changeBlockState(location, state);
	}

	public static void setBlockState(Location location, State state, int flags)
	{
		getCustomChunk(location.getChunk()).setBlockState(location, state, flags);
	}

	public static State getBlockState(Location location)
	{
		return getCustomChunk(location.getChunk()).getBlockState(location);
	}

	public static CustomBlockData getBlockData(Location location)
	{
		return getCustomChunk(location.getChunk()).getBlockData(location);
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
		getCustomChunk(location.getChunk()).setBlockData(location, data);
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

	public static void stateToCompound(NBT compound, State state)
	{
		CustomBlock block = ((CustomBlock) state.getObject());
		compound.setString("id", block.id());
		NBT stateTag = compound.createCompound();
		if (state.getProperties() != null)
		{
			state.getProperties().forEach((k, v) -> stateTag.setString(k.getName(), k.toString(v)));
		}
		compound.setCompound("state", stateTag);
	}

	public static State compoundToState(NBT compound)
	{
		String id = compound.getString("id");
		CustomBlock blockById = Blocks.getCustomBlockById(id);
		if (blockById == null)
			throw new RuntimeException("Old block '" + id + "' can not be created! " + compound);
		State defaultState = blockById.getDefaultState();
		if (defaultState == null)
			throw new RuntimeException("Block '" + id + "' returned null default state");
		State state = defaultState;
		if (state.getProperties() != null)
		{
			NBT stateTag = compound.getCompound("state");
			//noinspection rawtypes
			for (IProperty iProperty : defaultState.getProperties().keySet())
			{
				String string = stateTag.getString(iProperty.getName());
				//noinspection unchecked
				state = state.with(iProperty, iProperty.fromString(string));
			}
		}

		return state;
	}

	/*
	 * Block Data
	 */

	public static void dataToCompound(NBT compound, CustomBlockData data, boolean unloading)
	{
		compound.setString("data_class", data.getClass().getName());
		compound.setString("block_id", data.getBlock().id());
		compound.setString("world_name", data.pos.getWorld().getName());
		compound.set3i("pos", data.pos.getBlockX(), data.pos.getBlockY(), data.pos.getBlockZ());

		NBT dataCompound = compound.createCompound();
		data.toNBT(dataCompound);
		compound.setCompound("block_data", dataCompound);

		if (unloading)
		{
			if (data instanceof IBlockEntity iBlockEntity)
			{
				iBlockEntity.despawnEntities(new BlockContext(data.pos.clone()));
			}
		}
	}

	public static CustomBlockData compoundToData(NBT compound, State state)
	{
		String classPath = compound.getString("data_class");

		try
		{
			Class<?> clazz = Class.forName(classPath);
			CustomBlockData blockData = (CustomBlockData) clazz.getConstructor().newInstance();

			NBT data = compound.getCompound("block_data");
			blockData.fromNBT(data);

			CustomBlock block = Blocks.getCustomBlockById(compound.getString("block_id"));
			blockData.setLogic(block);
			Vector3i pos = compound.get3i("pos");
			Location location = new Location(Bukkit.getWorld(compound.getString("world_name")), pos.x, pos.y, pos.z);
			blockData.setPos(location);
			if (blockData instanceof IBlockEntity iBlockEntity)
			{
				iBlockEntity.spawnEntities(new BlockContext(location, state, blockData));
			}

			return blockData;
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
		         InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
}
