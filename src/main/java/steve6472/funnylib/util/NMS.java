package steve6472.funnylib.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.v1_20_R3.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by steve6472
 * Date: 11/6/2022
 * Project: StevesFunnyLibrary <br>
 */
public class NMS
{
	private static final Map<Item, Integer> BURN_TIME = AbstractFurnaceBlockEntity.getFuel();

	public static int getBurnTime(Material material)
	{
		Item item = CraftMagicNumbers.getItem(material);
		return BURN_TIME.getOrDefault(item, 0);
	}

	public static boolean isFuel(Material material)
	{
		Item item = CraftMagicNumbers.getItem(material);
		return BURN_TIME.containsKey(item);
	}

	public static Map<String, Object> serializePDC(PersistentDataContainer pdc)
	{
		if (pdc instanceof CraftPersistentDataContainer cpdc)
		{
			return ((Map<String, Object>) serialize(cpdc.toTagCompound()));
		}
		throw new RuntimeException("PDC not instance of CraftPersistentDataContainer");
	}

	public static Entity getEntityInWorld(World world, UUID entityUUID)
	{
		net.minecraft.world.entity.Entity entity = ((CraftWorld) world).getHandle().getEntity(entityUUID);
		if (entity != null)
			return entity.getBukkitEntity();
		return null;
	}

	private static Object serialize(Tag base)
	{
		if (base instanceof CompoundTag compoundTag)
		{
			Map<String, Object> innerMap = new HashMap<>();
			for (String key : compoundTag.getAllKeys())
			{
				innerMap.put(key, serialize(compoundTag.get(key)));
			}

			return innerMap;
		} else if (base instanceof ListTag list)
		{
			List<Object> baseList = new ArrayList<>();
			for (Tag tag : list)
			{
				baseList.add(serialize(tag));
			}

			return baseList;
		} else if (base instanceof StringTag)
		{
			String string = base.toString();
			return string.substring(1, string.length() - 1);
		} else if (base instanceof IntTag)
		{
			return base + "i";
		}

		return base.toString();
	}

	public static void addLore(ItemStack bukkitStack, String json)
	{
		try
		{
			boolean copy = false;
			CraftItemStack craftStack;
			if (bukkitStack instanceof CraftItemStack)
			{
				craftStack = (CraftItemStack) bukkitStack;
			} else
			{
				craftStack = CraftItemStack.asCraftCopy(bukkitStack);
				copy = true;
			}

			Field handle = craftStack.getClass().getDeclaredField("handle");
			handle.setAccessible(true);
			var nmsStack = (net.minecraft.world.item.ItemStack) handle.get(craftStack);

			// get Lore tag
			CompoundTag tag = nmsStack.getOrCreateTag();
			if (tag == null)
			{
				nmsStack.setTag(tag = new CompoundTag());
			}
			CompoundTag display = tag.contains("display", Tag.TAG_COMPOUND) ? tag.getCompound("display") : new CompoundTag();
			ListTag lore = display.getList("Lore", Tag.TAG_STRING);

			// add lore line
			lore.add(StringTag.valueOf(json));

			// put edited data back to itemstack
			display.put("Lore", lore);
			tag.put("display", display);

			if (copy)
			{
				bukkitStack.setItemMeta(craftStack.getItemMeta());
			}


			// debug
//			System.out.println("TAG: -> " + tag.getAsString());
		} catch (ReflectiveOperationException exception)
		{
			throw new RuntimeException(exception);
		}
	}

	public static void resentBiomes(World world, int blockFromX, int blockFromZ, int blockToX, int blockToZ)
	{
		ServerLevel handle = ((CraftWorld) world).getHandle();
		List<ChunkAccess> chunkList = new ArrayList<>();
		ChunkAccess var14;
		for(int var12 = SectionPos.blockToSectionCoord(blockFromZ); var12 <= SectionPos.blockToSectionCoord(blockToZ); ++var12) {
			for(int var13 = SectionPos.blockToSectionCoord(blockFromX); var13 <= SectionPos.blockToSectionCoord(blockToX); ++var13) {
				var14 = handle.getChunk(var13, var12, ChunkStatus.FULL, false);

				chunkList.add(var14);
			}
		}
		handle.getChunkSource().chunkMap.resendBiomesForChunks(chunkList);
	}

	public static BlockPos locToBlockPos(Location location)
	{
		return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public static void addLore(ItemStack bukkitStack, JSONMessage message)
	{
		addLore(bukkitStack, message.toJSON().toString());
	}

	public static PersistentDataContainer newCraftContainer()
	{
		return new CraftPersistentDataContainer(new CraftPersistentDataTypeRegistry());
	}

	/*
	 * Game Events
	 */

	private record GameEventEntry(GameEvent bukkitEvent, net.minecraft.world.level.gameevent.GameEvent nmsEvent, int notificationRadius) {}

	private static Map<GameEvent, GameEventEntry> GAME_EVENTS;

	private static void lazyEventMapInit()
	{
		if (GAME_EVENTS != null) return;
		GAME_EVENTS = new HashMap<>();
		for (Map.Entry<ResourceKey<net.minecraft.world.level.gameevent.GameEvent>, net.minecraft.world.level.gameevent.GameEvent> entry : BuiltInRegistries.GAME_EVENT.entrySet())
		{
			GameEvent spigotEvent = GameEvent.getByKey(new NamespacedKey("minecraft", entry.getKey().registry().getPath()));
			GAME_EVENTS.put(spigotEvent, new GameEventEntry(spigotEvent, entry.getValue(), entry.getValue().getNotificationRadius()));
		}
	}

	public static void fireGameEvent(@Nullable Entity entity, Location location, GameEvent gameEvent)
	{
		lazyEventMapInit();
		Preconditions.checkNotNull(location.getWorld(), "World can not be null!");

		net.minecraft.world.entity.Entity vanillaEntity = null;
		if (entity != null)
		{
			vanillaEntity = ((CraftEntity) entity).getHandle();
		}

		GameEventEntry gameEventEntry = GAME_EVENTS.get(gameEvent);
		if (gameEventEntry != null)
			((CraftWorld) location.getWorld()).getHandle().gameEvent(vanillaEntity, gameEventEntry.nmsEvent, locToBlockPos(location));
		else
			Log.error("Tried to use old GameEvent, this will not work");
	}
}
