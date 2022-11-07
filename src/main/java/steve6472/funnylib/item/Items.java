package steve6472.funnylib.item;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.*;
import steve6472.funnylib.item.events.*;
import steve6472.funnylib.util.Checks;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MetaUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**********************
 * Created by steve6472
 * On date: 3/18/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public class Items implements Listener
{
	private static final NamespacedKey CUSTOM_KEY = new NamespacedKey(FunnyLib.getPlugin(), ItemStackBuilder.CUSTOM_ID);

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

	public static CustomItem registerItem(CustomItem customItem)
	{
		ITEMS.put(customItem.id(), new ItemEventEntry(customItem));
		return customItem;
	}

	public static CustomItem registerAdminItem(CustomItem customItem)
	{
		ITEMS.put(customItem.id(), new ItemEventEntry(true, customItem));
		return customItem;
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
			callEventOnCustomItem(player, TickInHandEvent.class, handItem, (ev, i) -> callWithItemContext(player, EquipmentSlot.HAND, i, ev::tickInHand));

			ItemStack offHandItem = player.getInventory().getItem(EquipmentSlot.OFF_HAND);
			callEventOnCustomItem(player, TickInHandEvent.class, offHandItem, (ev, i) -> callWithItemContext(player, EquipmentSlot.OFF_HAND, i, ev::tickInHand));


			for (EquipmentSlot armorSlot : ARMOR_SLOTS)
			{
				ItemStack armorContent = player.getInventory().getItem(armorSlot);

				callEventOnCustomItem(player, ArmorEvents.class, armorContent, (ev, i) -> callWithItemContext(player, armorSlot, i, ev::wearTick));
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
		//noinspection unchecked
		event.accept((T) customItem, itemStack);
	}

	public static void callWithItemContext(EquipmentSlot hand, ItemStack itemStack, Consumer<CustomItemContext> consumer)
	{
		CustomItemContext context = new CustomItemContext(hand, itemStack);
		consumer.accept(context);
		if (isCustomItem(itemStack))
		{
			context.saveData();
		}
	}

	public static void callWithItemContext(Player player, EquipmentSlot hand, ItemStack itemStack, Consumer<PlayerItemContext> consumer)
	{
		//noinspection deprecation
		PlayerItemContext playerContext = new PlayerItemContext(player, hand, itemStack);
		consumer.accept(playerContext);
		if (isCustomItem(itemStack))
		{
			playerContext.saveItemData();
		}
	}

	public static void callWithItemContext(Player player, EquipmentSlot hand, Consumer<PlayerItemContext> consumer)
	{
		callWithItemContext(player, hand, player.getInventory().getItem(hand), consumer);
	}

	public static <R> R callWithItemContextR(Player player, EquipmentSlot hand, ItemStack itemStack, Function<PlayerItemContext, R> consumer)
	{
		//noinspection deprecation
		PlayerItemContext playerContext = new PlayerItemContext(player, hand, itemStack);
		R r = consumer.apply(playerContext);
		if (isCustomItem(itemStack))
		{
			playerContext.saveItemData();
		}
		return r;
	}

	public static <R> R callWithItemContextR(Player player, EquipmentSlot hand, Function<PlayerItemContext, R> func)
	{
		return callWithItemContextR(player, hand, player.getInventory().getItem(hand), func);
	}

	public static boolean isCustomItem(ItemStack itemStack)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_KEY, PersistentDataType.STRING);
	}

	public static String getCustomItemId(ItemStack itemStack)
	{
		if (itemStack == null)
			return null;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null)
			return null;
		return itemMeta.getPersistentDataContainer().get(CUSTOM_KEY, PersistentDataType.STRING);
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
	public void swapHands(PlayerSwapHandItemsEvent e)
	{
		callEventOnCustomItem(
			e.getPlayer(),
			SwapHandEvent.class,
			e.getOffHandItem(),
			(ev, i) -> callCancellable(e, new CancellableResult(), r -> ev.swapHands(e.getPlayer(), e.getOffHandItem(), e.getMainHandItem(), r))
		);
	}

	@EventHandler
	public void inventoryEvent(InventoryClickEvent e)
	{
		ItemStack currentItem = e.getCurrentItem();
		ItemEventEntry customItemEntry = getCustomItemEntry(currentItem);

		if (customItemEntry != null && (customItemEntry.requireAdmin() && e.getWhoClicked().isOp() || !customItemEntry.requireAdmin()))
		{
			Player player = ((Player) e.getWhoClicked());
			callEventOnCustomItem(player, ItemInvEvents.class, currentItem, (ev, i) -> callWithItemContext(player, EquipmentSlot.HAND, i, ic -> ev.clickInInventoryEvent(ic, e.getInventory(), e.getSlot(), e.getAction(), e)));
		}
	}

	@EventHandler
	public void consumeEvent(PlayerItemConsumeEvent e)
	{
		// FIXME: add find a way to detect hand
		ItemStack currentItem = e.getItem();
		ItemEventEntry customItemEntry = getCustomItemEntry(currentItem);
		if (customItemEntry != null && (customItemEntry.requireAdmin() && e.getPlayer().isOp() || !customItemEntry.requireAdmin()))
		{
			callEventOnCustomItem(e.getPlayer(), ConsumeEvent.class, currentItem, (ce, i) -> callWithItemContext(e.getPlayer(), EquipmentSlot.HAND, i, ce::consumed));
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

				callEventOnCustomItem(player, ArmorEvents.class, armorContent, (ev, i) -> callWithItemContext(player, armorSlot, i, ic -> ev.takeDamage(ic, e)));
			}
		}

		if (e.getDamager() instanceof Player player)
		{
			ItemStack handItem = player.getInventory().getItem(EquipmentSlot.HAND);
			callEventOnCustomItem(player, WeaponEvents.class, handItem, (we, i) -> callWithItemContext(player, EquipmentSlot.HAND, i, ic -> we.dealDamage(ic, e)));
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
		callCancellable(e, new CancellableResult(), r -> callWithItemContext(e.getPlayer(), e.getHand(), item, ic -> customItem.useOnEntity(new PlayerEntityContext(ic, new EntityContext(e.getRightClicked())), r)));
	}

	@EventHandler
	public void itemEvents(PlayerInteractEvent e)
	{
		ItemStack item = e.getItem();
		if (item == null || item.getType() == Material.AIR)
			return;

		if (e.getPlayer().getCooldown(item.getType()) != 0)
			return;

		if (ItemStackBuilder.edit(item).getCustomId() == null)
			return;

		String id = ItemStackBuilder.edit(item).getCustomId();

		ItemEventEntry itemEventEntry = ITEMS.get(id);
		if (itemEventEntry == null)
			return;

		if (itemEventEntry.requireAdmin && !e.getPlayer().isOp())
			return;

		CustomItem customItem = itemEventEntry.customItem;
		if (e.getClickedBlock() == null)
		{
			if (e.getAction() == Action.LEFT_CLICK_AIR)
			{
				callCancellable(e, new CancellableResult(), r -> callWithItemContext(e.getPlayer(), e.getHand(), item, ic -> customItem.useOnAir(ic, UseType.LEFT, r)));
			}
			else if (e.getAction() == Action.RIGHT_CLICK_AIR)
			{
				callCancellable(e, new CancellableResult(), r -> callWithItemContext(e.getPlayer(), e.getHand(), item, ic -> customItem.useOnAir(ic, UseType.RIGHT, r)));
			}
		} else
		{
			if (e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				callCancellable(e, new CancellableResult(), r -> callWithItemContext(e.getPlayer(), e.getHand(), item, ic -> customItem.useOnBlock(new PlayerBlockContext(ic, new BlockFaceContext(e
					.getClickedBlock()
					.getLocation(), e.getBlockFace())), UseType.LEFT, r)));
			}
			else if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				callCancellable(e, new CancellableResult(), r -> callWithItemContext(e.getPlayer(), e.getHand(), item, ic -> customItem.useOnBlock(new PlayerBlockContext(ic, new BlockFaceContext(e
					.getClickedBlock()
					.getLocation(), e.getBlockFace())), UseType.RIGHT, r)));
			}
		}
	}

	/*
	 * I made this function so I have a one line solution...
	 * The line got so long I had to split it on multiple lines
	 *
	 * I have failed
	 */
	public void callCancellable(Cancellable cancallable, CancellableResult result, Consumer<CancellableResult> run)
	{
		run.accept(result);
		cancallable.setCancelled(result.isCancelled());
	}

	@EventHandler
	public void breakBlock(BlockBreakEvent e)
	{
		if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
		ItemStack item = e.getPlayer().getInventory().getItem(EquipmentSlot.HAND);
		if (item == null) return;

		if (e.getPlayer().getGameMode() == GameMode.CREATIVE && Checks.isSwordMaterial(item.getType()))
			return;

		callEventOnCustomItem(e.getPlayer(), ItemBreakBlockEvent.class, item, (ev, i) -> callWithItemContext(e.getPlayer(), EquipmentSlot.HAND, item, ic -> e.setCancelled(!ev.breakBlock(new PlayerBlockContext(ic, new BlockFaceContext(e
			.getBlock()
			.getLocation(), MetaUtil.getValue(e.getPlayer(), BlockFace.class, "last_face")))))));
	}
}
