package steve6472.funnylib.blocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.blocks.stateengine.State;
import steve6472.funnylib.data.BlockInfo;
import steve6472.funnylib.json.INBT;
import steve6472.funnylib.util.Log;
import steve6472.funnylib.serialize.NBT;

import java.util.*;

import static steve6472.funnylib.blocks.Blocks.*;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CustomChunk implements INBT
{
	private final Chunk bukkitChunk;
	public boolean isUnloading;

	public Int2ObjectLinkedOpenHashMap<State> blocks = new Int2ObjectLinkedOpenHashMap<>();
	public Int2ObjectLinkedOpenHashMap<CustomBlockData> blockData = new Int2ObjectLinkedOpenHashMap<>();
	public IntArraySet ticking = new IntArraySet();

	public CustomChunk(Chunk bukkitChunk)
	{
		this.bukkitChunk = bukkitChunk;
	}

	public void unload()
	{
		if (isUnloading)
			throw new RuntimeException("Chunk has already been unloaded!");
		isUnloading = true;
	}

	@Override
	public void toNBT(NBT compound)
	{
		NBT[] states = compound.createCompoundArray(blocks.size());
		NBT[] data = compound.createCompoundArray(blockData.size());
		int i = 0;

		List<State> paletteList = new ArrayList<>();
		Map<State, Integer> paletteMap = new HashMap<>();
		for (Map.Entry<Integer, State> entry : blocks.int2ObjectEntrySet())
		{
			State value = entry.getValue();
			if (!paletteList.contains(value))
			{
				paletteList.add(value);
				paletteMap.put(value, i);
			}
			i++;
		}

		NBT[] palette = compound.createCompoundArray(paletteList.size());
		for (i = 0; i < paletteList.size(); i++)
		{
			State state = paletteList.get(i);
			Blocks.stateToCompound(palette[i], state);
		}

		i = 0;
		for (Map.Entry<Integer, State> entry : blocks.int2ObjectEntrySet())
		{
			Integer k = entry.getKey();
			State state = entry.getValue();
			NBT locObj = states[i];
			locObj.setInt("loc_key", k);
			locObj.setInt("state", paletteMap.get(state));
			i++;
		}

		i = 0;
		for (Map.Entry<Integer, CustomBlockData> entry : blockData.int2ObjectEntrySet())
		{
			Integer key = entry.getKey();
			CustomBlockData datum = entry.getValue();
			NBT locObj = data[i];
			locObj.setInt("loc_key", key);
			try
			{
				NBT dataCompound = locObj.createCompound();
				Blocks.dataToCompound(dataCompound, datum, isUnloading);
				locObj.setCompound("data", dataCompound);
			} catch (Exception ex)
			{
				Log.error("Exception thrown when saving data of " + datum
					.getBlock()
					.id() + " at " + keyToX(key) + "/" + keyToY(key) + "/" + keyToZ(key));
				ex.printStackTrace();
			}
			i++;
		}
		compound.setCompoundArray("palette", palette);
		compound.setCompoundArray("states", states);
		compound.setCompoundArray("data", data);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		if (!compound.hasCompoundArray("states"))
		{
			return;
		}

		NBT[] paletteNbts = compound.getCompoundArray("palette");
		NBT[] states = compound.getCompoundArray("states");
		NBT[] data = compound.getCompoundArray("data");

		State[] palette = new State[paletteNbts.length];

		for (int i = 0; i < paletteNbts.length; i++)
		{
			NBT paletteNbt = paletteNbts[i];
			palette[i] = Blocks.compoundToState(paletteNbt);
		}

		for (NBT locObj : states)
		{
			int key = locObj.getInt("loc_key");
			int stateIndex = locObj.getInt("state");
			State state = palette[stateIndex];
			blocks.put(key, state);

			if (state.getObject() instanceof BlockTick)
				ticking.add(key);
		}

		for (NBT locObj : data)
		{
			int key = locObj.getInt("loc_key");
			try
			{
				CustomBlockData customData = Blocks.compoundToData(locObj.getCompound("data"), blocks.get(key));
				blockData.put(key, customData);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				blocks.remove(key);
				blockData.remove(key);
			}
		}
	}

	/*
	 * Block State
	 */

	public void setBlockState(@NotNull Location location, @Nullable State state)
	{
		setBlockState(location, state, Blocks.DEFAULT_PLACE_FLAGS);
	}

	public void changeBlockState(@NotNull Location location, @NotNull State state)
	{
		State blockState = getBlockState(location);

		if (blockState == null || blockState.getObject() != state.getObject())
			throw new RuntimeException("Can not change custom block from this method");

		setBlockState(location, state, Blocks.CHANGE_STATE_FLAGS);
	}

	public void setBlockState(@NotNull Location location, @Nullable State state, int flags)
	{
		if (state == null)
		{
			int key = locToKey(location);
			State existingState = blocks.get(key);
			if (existingState != null)
			{
				CustomBlockData blockData = getBlockData(location);
				BlockContext context = new BlockContext(location, existingState);
				((CustomBlock) existingState.getObject()).onRemove(context);

				if (blockData != null)
				{
					blockData.onRemove(context);

					if (blockData instanceof IBlockEntity iBlockEntity)
					{
						iBlockEntity.despawnEntities(context);
					}
				}
				setBlockData(location, null);
				blocks.remove(key);
				ticking.rem(key);
				location.getBlock().setType(Material.AIR);
			}

			return;
		}

		if (!(state.getObject() instanceof CustomBlock cb))
		{
			throw new RuntimeException("State not of type CustomBlock");
		}

		int key = locToKey(location);

		/*
		 * Replacing custom block with another custom block
		 */
		State existingState = blocks.get(key);
		if ((flags & CALL_ON_REMOVE) != 0 && existingState != null)
		{
			BlockContext context = new BlockContext(location, existingState);

			CustomBlockData blockData = getBlockData(location);
			if (blockData != null)
			{
				blockData.onRemove(context);

				if (blockData instanceof IBlockEntity iBlockEntity)
				{
					iBlockEntity.despawnEntities(context);
				}
			}

			cb.onRemove(context);
		}

		blocks.put(key, state);

		BlockData vanillaState = cb.getVanillaState(new BlockContext(location, state));
		location.getBlock().setBlockData(vanillaState);

		if (cb instanceof BlockTick)
		{
			ticking.add(key);
		}

		/*
		 * Create Block Data
		 */

		if ((flags & CREATE_DATA) != 0 && cb instanceof IBlockData data)
		{
			CustomBlockData blockData = data.createBlockData();
			blockData.setLogic(cb);
			blockData.setPos(location.clone());
			setBlockData(location, blockData);
			BlockContext context = new BlockContext(location, state, blockData);
			blockData.onPlace(context);
			cb.onPlace(context);
			if (blockData instanceof IBlockEntity iBlockEntity)
			{
				iBlockEntity.spawnEntities(context);
			}
		} else
		{
			cb.onPlace(new BlockContext(location, state));
		}
	}

	@Nullable
	public State getBlockState(@NotNull Location location)
	{
		int key = locToKey(location);
		return blocks.get(key);
	}

	/*
	 * Block data
	 */

	public void setBlockData(@NotNull Location location, @Nullable CustomBlockData blockData)
	{
		int key = locToKey(location);
		if (blockData == null)
		{
			this.blockData.remove(key);
		} else
		{
			this.blockData.put(key, blockData);
		}
	}

	public CustomBlockData getBlockData(@NotNull Location location)
	{
		int key = locToKey(location);
		return blockData.get(key);
	}

	public int locToKey(@NotNull Location location)
	{
		return posToKey(Math.floorMod(location.getBlockX(), 16), location.getBlockY(), Math.floorMod(location.getBlockZ(), 16));
	}

	// ____ ____ ____ Yyyy   yyyy yyyy zzzz xxxx
	public static int posToKey(int x, int y, int z)
	{
		if (y < -2048 || y > 2047)
			throw new RuntimeException("Out of world bounds");

		int xz = (x) | (z << 4);

		if (y < 0)
		{
			y = ~y;
			y = y & 0x7ff;
			y |= 1 << 11;
		}

		return xz | (y << 8);
	}

	public static int keyToX(int key)
	{
		return key & 0xf;
	}

	public static int keyToZ(int key)
	{
		return (key >> 4) & 0xf;
	}

	public static int keyToY(int key)
	{
		int y = (key >> 8);

		// check if negative
		if ((y & (1 << 11)) != 0)
		{
			return ~(y & 0x7ff);
		} else
		{
			return y & 0x7ff;
		}
	}
}
