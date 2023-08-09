package steve6472.funnylib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.v1_20_R1.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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
			return cpdc.serialize();
		}
		throw new RuntimeException("PDC not instance of CraftPersistentDataContainer");
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
		for (net.minecraft.world.level.gameevent.GameEvent gameEvent : BuiltInRegistries.GAME_EVENT)
		{
			GameEvent spigotEvent = GameEvent.getByKey(new NamespacedKey("minecraft", gameEvent.getName()));
			GAME_EVENTS.put(spigotEvent, new GameEventEntry(spigotEvent, gameEvent, gameEvent.getNotificationRadius()));
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
