package steve6472.funnylib.data;

import joptsimple.internal.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;
import steve6472.funnylib.category.Categorizable;
import steve6472.funnylib.item.builtin.StructureItem;
import steve6472.funnylib.json.Itemizable;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.Pair;
import steve6472.funnylib.util.Preconditions;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by steve6472
 * Date: 4/22/2023
 * Project: StevesFunnyLibrary <br>
 */
public final class GameStructure implements Categorizable, Itemizable<GameStructure>
{
	private String name;
	private Material icon;
	private BlockInfo[] blocks;
	private int uniqueBlocks;
	private final Vector3i size;
	public final Vector3i start, end;

	public GameStructure(BlockInfo[] blocks, Vector3i size, Vector3i start, Vector3i end, int uniqueBlocks)
	{
		this.blocks = blocks;
		this.size = size;
		this.uniqueBlocks = uniqueBlocks;
		this.start = start;
		this.end = end;
		icon = Material.BOOK;
	}

	public GameStructure()
	{
		this(new BlockInfo[]{}, new Vector3i(0, 0, 0), new Vector3i(0, 0, 0), new Vector3i(0, 0, 0), 0);
	}

	public Vector3i getSize()
	{
		return new Vector3i(size);
	}

	public BlockInfo getBlockInfo(int x, int y, int z)
	{
		int index = (size.y + 1) * (size.x + 1) * z + (size.x + 1) * y + x;
		return blocks[index];
	}

	public BlockData getBlock(int x, int y, int z)
	{
		int index = (size.y + 1) * (size.x + 1) * z + (size.x + 1) * y + x;
		return blocks[index].data();
	}

	public String name()
	{
		return name;
	}

	public Material icon()
	{
		return icon;
	}

	public void setIcon(Material icon)
	{
		this.icon = icon;
	}

	@Override
	public String id()
	{
		return "structure";
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public BlockInfo[] getBlocks()
	{
		return blocks;
	}

	private String[] createPalette()
	{
		Set<BlockData> blocksInPallete = new HashSet<>();
		String[] data = new String[uniqueBlocks];
		int lastIndex = 0;

		for (BlockInfo block : blocks)
		{
			BlockData blockData = block.data();
			if (blocksInPallete.contains(blockData))
				continue;
			blocksInPallete.add(blockData);
			data[lastIndex] = blockData.getAsString(true);
			lastIndex++;
		}

		return data;
	}

	private int[] createDataWithPalette(String[] palette)
	{
		int[] data = new int[(getSize().x + 1) * (getSize().y + 1) * (getSize().z + 1)];

		BlockInfo[] blocks = getBlocks();
		for (int i = 0; i < blocks.length; i++)
		{
			BlockInfo block = blocks[i];
			BlockData blockData = block.data();
			int index = indexOf(palette, blockData.getAsString(true));
			data[i] = index;
		}

		return data;
	}

	public ItemStack toItem()
	{
		return StructureItem.newStructureItem(this, name(), icon());
	}

	public void place(World world, int x, int y, int z, int offsetX, int offsetY, int offsetZ)
	{
		Preconditions.checkNotNull(world);

		Set<Pair<Chunk, Boolean>> chunks = new HashSet<>();

		for (int i = (x + offsetX) >> 4; i < (x + offsetX + size.x) >> 4; i++)
		{
			for (int j = (z + offsetZ) >> 4; j < (z + offsetZ + size.z) >> 4; j++)
			{
				Chunk chunkAt = world.getChunkAt(i, j);
				chunkAt.load();
				boolean lastForceState = chunkAt.isForceLoaded();
				chunkAt.setForceLoaded(true);
				chunks.add(new Pair<>(chunkAt, lastForceState));
			}
		}

		for (BlockInfo block : blocks)
		{
			world.getBlockAt(x + block.position().x + offsetX, y + block.position().y + offsetY, z + block.position().z + offsetZ).setBlockData(block.data().clone());
		}

		chunks.forEach(c -> c.a().setForceLoaded(c.b()));
	}

	public void placeCentered(World world, int x, int y, int z)
	{
		place(world, x, y, z, -size.x / 2, -size.y / 2, -size.z / 2);
	}

	public void unplace(World world, int x, int y, int z, int offsetX, int offsetY, int offsetZ)
	{
		Preconditions.checkNotNull(world);

		Set<Pair<Chunk, Boolean>> chunks = new HashSet<>();

		for (int i = (x + offsetX) >> 4; i < (x + offsetX + getSize().x) >> 4; i++)
		{
			for (int j = (z + offsetZ) >> 4; j < (z + offsetZ + getSize().z) >> 4; j++)
			{
				Chunk chunkAt = world.getChunkAt(i, j);
				chunkAt.load();
				boolean lastForceState = chunkAt.isForceLoaded();
				chunkAt.setForceLoaded(true);
				chunks.add(new Pair<>(chunkAt, lastForceState));
			}
		}

		for (BlockInfo block : getBlocks())
		{
			world.getBlockAt(x + block.position().x + offsetX, y + block.position().y + offsetY, z + block.position().z + offsetZ).setType(Material.AIR);
		}

		chunks.forEach(c -> c.a().setForceLoaded(c.b()));
	}

	public void unplaceCentered(World world, int x, int y, int z)
	{
		unplace(world, x, y, z, -size.x / 2, -size.y / 2, -size.z / 2);
	}

	public static GameStructure fromWorld(World world, int startX, int startY, int startZ, int endX, int endY, int endZ)
	{
		int width = (endX - startX);
		int height = (endY - startY);
		int depth = (endZ - startZ);

		BlockInfo[] data = new BlockInfo[(width + 1) * (height + 1) * (depth + 1)];
		Set<BlockData> uniqueBlocks = new HashSet<>();

		for (int i = startX; i <= endX; i++)
		{
			for (int j = startY; j <= endY; j++)
			{
				for (int k = startZ; k <= endZ; k++)
				{
					int x = i - startX;
					int y = j - startY;
					int z = k - startZ;
					int index = (height + 1) * (width + 1) * z + (width + 1) * y + x;

					Block blockAt = world.getBlockAt(i, j, k);
					BlockData blockData = blockAt.getBlockData();
					uniqueBlocks.add(blockData);
					data[index] = new BlockInfo(blockData, new Vector3i(x, y, z));
				}
			}
		}

		return new GameStructure(data, new Vector3i(width, height, depth), new Vector3i(startX, startY, startZ), new Vector3i(endX, endY, endZ), uniqueBlocks.size());
	}

	public static int[] dataFromCompound(NBT nbt)
	{
		return nbt.getIntArray("blocks");
	}

	public static String[] paletteFromCompound(NBT nbt)
	{
		return nbt.getString("block_palette").split(";");
	}

	public static Vector3i sizeFromCompound(NBT nbt)
	{
		return nbt.get3i("size");
	}

	public static Vector3i startFromCompound(NBT nbt)
	{
		return nbt.get3i("start");
	}

	public static Vector3i endFromCompound(NBT nbt)
	{
		return nbt.get3i("end");
	}

	public static GameStructure structureFromNBT(NBT nbt)
	{
		GameStructure gameStructure = new GameStructure();
		gameStructure.fromNBT(nbt);
		return gameStructure;
	}

	public static BlockInfo[] blocksFromData(String[] palette, int[] data, Vector3i size)
	{
		BlockInfo[] blocks = new BlockInfo[(size.x + 1) * (size.y + 1) * (size.z + 1)];

		int width = size.x;
		int height = size.y;
		int depth = size.z;
		if ((width + 1) * (height + 1) * (depth + 1) <= 0)
		{
			return blocks;
		}

		for (int i = 0; i <= width; i++)
		{
			for (int j = 0; j <= height; j++)
			{
				for (int k = 0; k <= depth; k++)
				{
					int index = (height + 1) * (width + 1) * k + (width + 1) * j + i;
					BlockData blockData = Bukkit.createBlockData(palette[data[index]]);
					blocks[index] = new BlockInfo(blockData, new Vector3i(i, j, k));
				}
			}
		}

		return blocks;
	}

	public static GameStructure structureFromData(String[] palette, int[] data, Vector3i size, Vector3i start, Vector3i end, String name, Material icon)
	{
		BlockInfo[] blocks = new BlockInfo[(size.x + 1) * (size.y + 1) * (size.z + 1)];

		int width = size.x;
		int height = size.y;
		int depth = size.z;
		if ((width + 1) * (height + 1) * (depth + 1) <= 0)
		{
			return new GameStructure();
		}

		for (int i = 0; i <= width; i++)
		{
			for (int j = 0; j <= height; j++)
			{
				for (int k = 0; k <= depth; k++)
				{
					int index = (height + 1) * (width + 1) * k + (width + 1) * j + i;
					BlockData blockData = Bukkit.createBlockData(palette[data[index]]);
					blocks[index] = new BlockInfo(blockData, new Vector3i(i, j, k));
				}
			}
		}

		GameStructure gameStructure = new GameStructure(blocks, size, start, end, palette.length);
		gameStructure.setName(name);
		gameStructure.setIcon(icon);
		return gameStructure;
	}

	private static int indexOf(String[] palette, String lookingFor)
	{
		for (int i = 0; i < palette.length; i++)
		{
			if (palette[i].equals(lookingFor))
				return i;
		}
		return -1;
	}

	public static GameStructure fromItem(ItemStack item)
	{
		if (item.getType().isAir())
			return null;

		ItemNBT nbt = ItemNBT.create(item);

		if (!nbt.hasCompound(StructureItem.KEY))
			return new GameStructure();

		String name = nbt.getString("name", null);
		String icon = nbt.getString("icon", null);

		NBT compound = nbt.getCompound(StructureItem.KEY);
		compound.set3i("start", nbt.get3i("start"));
		compound.set3i("end", nbt.get3i("end"));

		GameStructure structure = structureFromNBT(compound);
		structure.setName(name);

		if (icon != null)
			structure.setIcon(Material.matchMaterial(icon));

		return structure;
	}

	@Override
	public void toNBT(NBT compound)
	{
		String[] palette = createPalette();
		int[] blockStates = createDataWithPalette(palette);
		compound.setString("block_palette", Strings.join(palette, ";"));
		compound.setIntArray("blocks", blockStates);
		compound.set3i("size", size);
		compound.set3i("start", start);
		compound.set3i("end", end);
		compound.setEnum("icon", icon);
		if (name != null)
			compound.setString("name", name);
	}

	@Override
	public void fromNBT(NBT compound)
	{
		String[] palette = paletteFromCompound(compound);
		int[] data = dataFromCompound(compound);
		Vector3i size = sizeFromCompound(compound);
		Vector3i start = !compound.has3i("start") ? new Vector3i() : startFromCompound(compound);
		Vector3i end = !compound.has3i("end") ? new Vector3i() : endFromCompound(compound);

		this.blocks = blocksFromData(palette, data, size);
		this.uniqueBlocks = palette.length;
		this.size.set(size);
		this.start.set(start);
		this.end.set(end);
		this.setName(compound.getString("name", null));
		this.setIcon(compound.getEnum(Material.class, "icon", Material.BOOK));
	}
}
