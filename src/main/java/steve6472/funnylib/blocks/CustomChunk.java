package steve6472.funnylib.blocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.blocks.events.BlockTick;
import steve6472.funnylib.blocks.stateengine.State;

import static steve6472.funnylib.blocks.Blocks.CREATE_DATA;

/**
 * Created by steve6472
 * Date: 9/17/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CustomChunk
{
	private final Chunk bukkitChunk;

	public Int2ObjectLinkedOpenHashMap<State> blocks = new Int2ObjectLinkedOpenHashMap<>();
	public Int2ObjectLinkedOpenHashMap<CustomBlockData> blockData = new Int2ObjectLinkedOpenHashMap<>();
	public IntArrayList ticking = new IntArrayList();

	public CustomChunk(Chunk bukkitChunk)
	{
		this.bukkitChunk = bukkitChunk;
	}

	public void fromJson(JSONObject json)
	{
		if (!json.has("states"))
		{
			return;
		}

		JSONArray states = json.getJSONArray("states");

		for (int i = 0; i < states.length(); i++)
		{
			JSONObject locObj = states.getJSONObject(i);
			State state = Blocks.jsonToState(locObj.getJSONObject("state"));
			int key = locObj.getInt("locKey");
			blocks.put(key, state);

			if (state.getObject() instanceof BlockTick)
				ticking.add(key);
		}

		if (!json.has("data"))
		{
			return;
		}

		JSONArray data = json.getJSONArray("data");
		for (int i = 0; i < data.length(); i++)
		{
			JSONObject locObj = data.getJSONObject(i);
			int key = locObj.getInt("locKey");
			try
			{
				CustomBlockData customData = Blocks.jsonToData(locObj.getJSONObject("data"));
				blockData.put(key, customData);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				blocks.remove(key);
				blockData.remove(key);
			}
		}
	}

	public void toJson(JSONObject json)
	{
		JSONArray states = new JSONArray();
		JSONArray data = new JSONArray();
		blocks.forEach((key, state) -> {
			JSONObject locObj = new JSONObject();
			locObj.put("locKey", key);
			locObj.put("state", Blocks.stateToJson(state));
			states.put(locObj);
		});
		blockData.forEach((key, datum) -> {
			JSONObject locObj = new JSONObject();
			locObj.put("locKey", key);
			locObj.put("data", Blocks.dataToJson(datum));
			data.put(locObj);
		});
		json.put("states", states);
		json.put("data", data);
	}

	/*
	 * Block State
	 */

	public void setBlockState(@NotNull Location location, @Nullable State state)
	{
		setBlockState(location, state, Blocks.DEFAULT_PLACE_FLAGS);
	}

	public void setBlockState(@NotNull Location location, @Nullable State state, int flags)
	{
		if (state == null)
		{
			int key = locToKey(location);
			State existingState = blocks.get(key);
			if (existingState != null)
			{
				((CustomBlock) existingState.getObject()).onRemove(location, existingState, getBlockData(location));
				setBlockData(location, null);
				blocks.remove(key);
				ticking.rem(key);
			}

			return;
		}

		if (!(state.getObject() instanceof CustomBlock cb))
		{
			throw new RuntimeException("State not of type CustomBlock");
		}

		int key = locToKey(location);
		State existingState = blocks.get(key);
		if (existingState != null)
		{
			cb.onRemove(location, existingState, getBlockData(location));
		}
		blocks.put(key, state);

		BlockData vanillaState = cb.getVanillaState(state);
		location.getBlock().setBlockData(vanillaState);

		if (cb instanceof BlockTick)
		{
			ticking.add(key);
		}

		if ((flags & CREATE_DATA) != 0 && cb instanceof IBlockData data)
		{
			CustomBlockData blockData = data.createBlockData();
			blockData.setLogic(cb);
			blockData.setLocation(bukkitChunk.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
			setBlockData(location, blockData);
			cb.onPlace(location, state, blockData);
		} else
		{
			cb.onPlace(location, state, null);
		}
	}

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
