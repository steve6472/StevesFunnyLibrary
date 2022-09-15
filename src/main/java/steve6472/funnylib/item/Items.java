package steve6472.funnylib.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import steve6472.funnylib.item.events.*;
import steve6472.funnylib.util.ItemStackBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**********************
 * Created by steve6472
 * On date: 3/18/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public class Items implements Listener
{
	public record ItemEventEntry(boolean requireAdmin, CustomItem customItem)
	{
		private ItemEventEntry(CustomItem customItem)
		{
			this(false, customItem);
		}

		@Override
		public String toString()
		{
			return "ItemEventEntry{" + "requireAdmin=" + requireAdmin + ", customItem=" + customItem + '}';
		}
	}

	public static final Map<String, ItemEventEntry> ITEMS = new HashMap<>();

	public static void registerItem(CustomItem customItem)
	{
		ITEMS.put(customItem.id(), new ItemEventEntry(customItem));
	}

	public static void registerAdminItem(CustomItem customItem)
	{
		ITEMS.put(customItem.id(), new ItemEventEntry(true, customItem));
	}

	public Items()
	{
		/*
		 * Admin Items
		 *//*
		registerAdminItem(new MarkerItem());
		registerAdminItem(new TagsItem());
		registerAdminItem(new StagesItem());
		registerAdminItem(new ThunderHammer());
		registerAdminItem(new TranslocatorItem());
		registerAdminItem(new FoldersItem());
		registerAdminItem(new MarkerMoverItem());
		registerAdminItem(new EffectGiver());
		registerAdminItem(new MarkerCopyItem());
		registerAdminItem(new ArrowsItem());
		registerAdminItem(new CobwebSpawner());
		registerAdminItem(new KillerRabbitSpawner());
		registerAdminItem(new HoleInTheFloorItem());
		registerAdminItem(new SDFHelper());
		registerAdminItem(new InventoryBundle());

		registerItem(new BingoItem());
		registerItem(new NightVisionGoggles());
		registerItem(new HealthySoupItem());
		registerItem(new CryptMainKey());
		registerItem(new ReflectiveCrystalChestplate());
		registerItem(new HeartBrakerAxe());
		registerItem(new NegatingCrystalChestplate());
		registerItem(new Railzer());
		registerItem(new CheckpointTeleporter());
		registerItem(new FartInABottle());
		registerItem(new TipOfAnIceSword());
		registerItem(new VaultKey());
		registerItem(new CryptBridgeKey());*/
	}

	private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	public static void tick()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			ItemStack handItem = player.getInventory().getItem(EquipmentSlot.HAND);
			callEventOnCustomItem(player, TickInHandEvent.class, handItem, (ev, i) -> ev.tickInHand(player, i, EquipmentSlot.HAND));

			ItemStack offHandItem = player.getInventory().getItem(EquipmentSlot.OFF_HAND);
			callEventOnCustomItem(player, TickInHandEvent.class, offHandItem, (ev, i) -> ev.tickInHand(player, i, EquipmentSlot.OFF_HAND));


			for (EquipmentSlot armorSlot : ARMOR_SLOTS)
			{
				ItemStack armorContent = player.getInventory().getItem(armorSlot);

				callEventOnCustomItem(player, ArmorEvents.class, armorContent, (ev, i) -> ev.wearTick(player, i, armorSlot));
			}
		}
	}

	public static <T> void callEventOnCustomItem(Player player, Class<T> eventClass, ItemStack itemStack, BiConsumer<T, ItemStack> event)
	{
		ItemEventEntry customItemEntry = Items.getCustomItemEntry(itemStack);
		if (customItemEntry == null) return;
		if (customItemEntry.requireAdmin() && !player.isOp()) return;
		CustomItem customItem = customItemEntry.customItem();
		if (!eventClass.isAssignableFrom(customItem.getClass())) return;
		event.accept((T) customItem, itemStack);
	}

	public static boolean isCustomItem(ItemStack itemStack)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		return itemMeta != null && itemMeta.getPersistentDataContainer().has(ItemStackBuilder.CUSTOM_ID, PersistentDataType.STRING);
	}

	public static String getCustomItemId(ItemStack itemStack)
	{
		if (itemStack == null)
			return null;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null)
			return null;
		return itemMeta.getPersistentDataContainer().get(ItemStackBuilder.CUSTOM_ID, PersistentDataType.STRING);
	}

	public static ItemEventEntry getCustomItemEntry(ItemStack itemStack)
	{
		String id = getCustomItemId(itemStack);
		if (id == null)
			return null;
		return ITEMS.get(id);
	}

	public static CustomItem getCustomItem(ItemStack itemStack)
	{
		String id = getCustomItemId(itemStack);
		if (id == null)
			return null;
		return ITEMS.get(id).customItem();
	}

	@EventHandler
	public void inventoryEvent(InventoryClickEvent e)
	{
		ItemStack currentItem = e.getCurrentItem();
		ItemEventEntry customItemEntry = getCustomItemEntry(currentItem);

		if (customItemEntry != null && (customItemEntry.requireAdmin() && e.getWhoClicked().isOp() || !customItemEntry.requireAdmin()))
		{
			callEventOnCustomItem(((Player) e.getWhoClicked()), ItemInvEvents.class, currentItem, (ev, i) -> ev.clickInInventoryEvent(((Player) e.getWhoClicked()), i, e.getInventory(), e.getSlot(), e.getAction(), e));
		}
	}

	@EventHandler
	public void consumeEvent(PlayerItemConsumeEvent e)
	{
		ItemStack currentItem = e.getItem();
		ItemEventEntry customItemEntry = getCustomItemEntry(currentItem);
		if (customItemEntry != null && (customItemEntry.requireAdmin() && e.getPlayer().isOp() || !customItemEntry.requireAdmin()))
		{
			callEventOnCustomItem(e.getPlayer(), ConsumeEvent.class, currentItem, (ce, i) -> ce.consumed(e.getPlayer(), i));
		}
	}

	@EventHandler
	public void damageEvent(EntityDamageByEntityEvent e)
	{
		if (e.getEntity() instanceof Player player)
		{
			for (EquipmentSlot armorSlot : ARMOR_SLOTS)
			{
				ItemStack armorContent = player.getInventory().getItem(armorSlot);

				callEventOnCustomItem(player, ArmorEvents.class, armorContent, (ev, i) -> ev.takeDamage(player, e, i, armorSlot));
			}

//			ItemStack handItem = player.getInventory().getItem(EquipmentSlot.HAND);
//			callEventOnCustomItem(player, WeaponEvents.class, handItem, (we, i) -> we.dealDamage(player, e, i, EquipmentSlot.HAND));
		}

		if (e.getDamager() instanceof Player player)
		{
			ItemStack handItem = player.getInventory().getItem(EquipmentSlot.HAND);
			callEventOnCustomItem(player, WeaponEvents.class, handItem, (we, i) -> we.dealDamage(player, e, i, EquipmentSlot.HAND));
		}
	}

	@EventHandler
	public void itemEntityEvents(PlayerInteractEntityEvent e)
	{
		if (e.getHand() != EquipmentSlot.HAND)
			return;

		ItemStack item = e.getPlayer().getInventory().getItem(EquipmentSlot.HAND);

		if (item == null || item.getType() == Material.AIR)
			return;

		if (ItemStackBuilder.edit(item).getCustomId() == null)
			return;

		String id = ItemStackBuilder.edit(item).getCustomId();

		ItemEventEntry itemEventEntry = ITEMS.get(id);

		if (itemEventEntry.requireAdmin && !e.getPlayer().isOp())
			return;

		CustomItem customItem = itemEventEntry.customItem;
		if (customItem instanceof ItemClickEvents itemEvent)
		{
			itemEvent.rightClickEntity(item, e);
		}
	}

	@EventHandler
	public void itemEvents(PlayerInteractEvent e)
	{
		if (e.getHand() != EquipmentSlot.HAND)
			return;

		ItemStack item = e.getItem();
		if (item == null || item.getType() == Material.AIR)
			return;

		if (ItemStackBuilder.edit(item).getCustomId() == null)
			return;

		String id = ItemStackBuilder.edit(item).getCustomId();

		ItemEventEntry itemEventEntry = ITEMS.get(id);

		if (itemEventEntry.requireAdmin && !e.getPlayer().isOp())
			return;

		CustomItem customItem = itemEventEntry.customItem;
		if (customItem instanceof ItemClickEvents itemEvent)
		{
			if (e.getClickedBlock() == null)
			{
				if (e.getAction() == Action.LEFT_CLICK_AIR)
				{
					itemEvent.leftClickAir(item, e);
					itemEvent.leftClick(item, e);
				}
				else if (e.getAction() == Action.RIGHT_CLICK_AIR)
				{
					itemEvent.rightClickAir(item, e);
					itemEvent.rightClick(item, e);
				}
			} else
			{
				if (e.getAction() == Action.LEFT_CLICK_BLOCK)
				{
					itemEvent.leftClickBlock(item, e);
					itemEvent.leftClick(item, e);
				}
				else if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					itemEvent.rightClickBlock(item, e);
					itemEvent.rightClick(item, e);
				}
			}
		}
	}
}
