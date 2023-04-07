package steve6472.funnylib.category;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.builtin.MarkerItem;
import steve6472.funnylib.json.IJsonConfig;
import steve6472.funnylib.json.codec.Codec;
import steve6472.funnylib.json.codec.codecs.MarkerCodec;
import steve6472.funnylib.menu.*;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MetaUtil;
import steve6472.funnylib.util.MiscUtil;
import steve6472.standalone.interactable.ex.ExpItems;
import steve6472.standalone.interactable.ex.ExpressionMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 2/18/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class GenericStorage<T extends ICategorizable> implements IJsonConfig
{
	private record Point(int x, int y) {}

	private final Class<T> clazz;
	private final Codec<T> codec;

	public GenericStorage(Class<T> clazz, Codec<T> codec)
	{
		this.clazz = clazz;
		this.codec = codec;
	}

	private final List<T> LIST = new ArrayList<>();

	private final Function<T, SlotBuilder> SLOT = (object) -> SlotBuilder.create(createIcon(object))
		.allow(ClickType.LEFT, ClickType.RIGHT)
		.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
		.onClick(ClickType.LEFT, (c, m) -> Response.setItemToCursor(createFromObject(object)))
		.onClick(ClickType.RIGHT, (c, m) ->
		{
			MetaUtil.setMeta(c.player(), "clicked_object", object);
			openPopup(m);
			return Response.cancel();
		});

	protected final Mask MAIN_MASK = Mask.createMask()
		.addRow(".........", 5)
		.addRow("AXXXXXXLR")
		.addItem('X', SlotBuilder
			.create(ItemStackBuilder.create(Material.WHITE_STAINED_GLASS_PANE).setName("").buildItemStack()).setSticky())
		.addItem('L', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Left").setCustomModelData(10).setArmorColor(0xdddddd).setHideFlags(ItemFlag.HIDE_DYE).buildItemStack(), (c, m) -> m.move(-9, 0)))
		.addItem('R', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Right").setCustomModelData(11).setArmorColor(0xdddddd).setHideFlags(ItemFlag.HIDE_DYE).buildItemStack(), (c, m) -> m.move(9, 0)))
		.addItem('A', SlotBuilder.stickyButtonSlot_(createAddSlot()
			, (c, m) ->
			{
				if (canBeAdded(c.itemOnCursor()))
				{
					T object = createFromItem(c.itemOnCursor());
					LIST.add(object);
					Point location = getItemLocation(LIST.size() - 1);
					m.setSlot(location.x, location.y, SLOT.apply(object));
					m.reload();
				}
			}).allow(InventoryAction.SWAP_WITH_CURSOR))
		;

	protected final Mask POPUP = Mask.createMask()
		.addRow(".._____..")
		.addRow(".______X.")
		.addRow("._______.")
		.addRow(".______U.")
		.addRow(".______D.")
		.addRow("____P____")
		.addItem('_', SlotBuilder.create(MiscUtil.AIR).setSticky())
		.addItem('X', SlotBuilder.stickyButtonSlot_(ExpItems.POPUP_CLOSE.newItemStack(), (c, m) ->
		{
			construct(c.player(), m);
			m.applyMask(MAIN_MASK);
		}))
//		.addItem('U', SlotBuilder.stickyButtonSlot_(MiscUtil.AIR, (c, m) ->
//		{
//			Menu popupMenu = m.getMetadata("popup", Menu.class);
//			popupMenu.move(0, -1);
//			popupMenu.overlay(m, 1, 1, 6, 4);
//			c.slot().setItem(popupMenu.getOffsetY() <= 0 ? MiscUtil.AIR : ExpItems.POPUP_UP.newItemStack());
//		}))
//		.addItem('D', SlotBuilder.stickyButtonSlot_(ExpItems.POPUP_DOWN.newItemStack(), (c, m) ->
//		{
//			Menu popupMenu = m.getMetadata("popup", Menu.class);
//			popupMenu.move(0, 1);
//			popupMenu.overlay(m, 1, 1, 6, 4);
//
//			m.getSlot(c.slot().getX(), c.slot().getY() - 1).setItem(popupMenu.getOffsetY() <= 0 ? MiscUtil.AIR : ExpItems.POPUP_UP.newItemStack());
//		}))
		;

	private static Point getItemLocation(int item)
	{
		int page = item / 45;
		int x = ((item % 45) % 9) + (page * 9);
		int y = (item % 45) / 9;
		return new Point(x, y);
	}

	private final MenuBuilder BUILDER = MenuBuilder.create(6, "Markers")
		.applyMask(MAIN_MASK)
		.allowPlayerInventory();

	protected void construct(Player player, Menu menu)
	{
		menu.clear();

		for (int i = 0; i < LIST.size(); i++)
		{
			T marker = LIST.get(i);
			Point itemLoc = getItemLocation(i);
			menu.setSlot(itemLoc.x(), itemLoc.y(), SLOT.apply(marker));
		}
	}

	public void showToPlayer(Player player)
	{
		Menu menu = BUILDER.build();

		construct(player, menu);
		menu.applyMask(MAIN_MASK);

		menu.showToPlayer(player);
	}

	private void populatePopup(MenuBuilder builder)
	{
		// Custom Icon
		builder.slot(0, 0, SlotBuilder.stickyButtonSlot_(ItemStackBuilder.quick(Material.TORCH, "Set Icon"), (c, m) ->
		{
			T clickedObject = MetaUtil.getValue(c.player(), clazz, "clicked_object");
			if (clickedObject == null)
			{
				c.player().sendMessage(ChatColor.RED + "'clicked_marker' meta is missing!");
			} else
			{
				clickedObject.setIcon(c.itemOnCursor().getType().isAir() ? Material.PAPER : c.itemOnCursor().getType());
				c.player().sendMessage(ChatColor.GREEN + "Icon for Marker " + ChatColor.WHITE + "'" + clickedObject.name() + "'" + ChatColor.GREEN + " changed!");

				construct(c.player(), m);
				m.applyMask(MAIN_MASK);
				openPopup(m);
			}
		}).allow(InventoryAction.SWAP_WITH_CURSOR));

		// Rename Marker
		builder.slot(1, 0, SlotBuilder.stickyButtonSlot_(ItemStackBuilder.quick(Material.NAME_TAG, "Rename Marker"), (c, m) ->
		{
			T clickedObject = MetaUtil.getValue(c.player(), clazz, "clicked_object");
			if (clickedObject == null)
			{
				c.player().sendMessage(ChatColor.RED + "'clicked_marker' meta is missing!");
			} else
			{
				showRenameGUI(clickedObject, m, c.player());
			}
		}).allow(InventoryAction.SWAP_WITH_CURSOR));

		// Remove Marker
		builder.slot(5, 3, SlotBuilder.stickyButtonSlot_(ItemStackBuilder.quick(Material.BARRIER, "Remove Marker", ChatColor.RED), (c, m) ->
		{
			T clickedObject = MetaUtil.getValue(c.player(), clazz, "clicked_object");
			if (clickedObject == null)
			{
				c.player().sendMessage(ChatColor.RED + "'clicked_marker' meta is missing!");
			} else
			{
				LIST.remove(clickedObject);
				c.player().sendMessage(ChatColor.GREEN + "Marker " + ChatColor.WHITE + "'" + clickedObject.name() + "'" + ChatColor.GREEN + " removed!");

				construct(c.player(), m);
				m.applyMask(MAIN_MASK);
			}
		}).allow(InventoryAction.SWAP_WITH_CURSOR));
	}

	private void showRenameGUI(T object, Menu menu, Player player)
	{
		MetaUtil.setMeta(player, "old_menu", menu);
		player.closeInventory();

		new AnvilGUI.Builder()
			.onComplete((completion) ->
			{
				object.setName(completion.getText());
				return Collections.singletonList(AnvilGUI.ResponseAction.run(() ->
				{
					Menu oldMenu = MetaUtil.getValue(player, Menu.class, "old_menu");
					if (oldMenu == null)
					{
						player.sendMessage(ChatColor.RED + "'old_menu' meta is missing!");
					} else
					{
						construct(player, oldMenu);
						oldMenu.applyMask(MAIN_MASK);
						openPopup(oldMenu);
						oldMenu.showToPlayer(player);
					}
				}));
			})
			.text(" ")
			.itemLeft(new ItemStack(Material.PAPER))
			.title("Enter new name")
			.plugin(FunnyLib.getPlugin())
			.open(player);
	}

	protected Menu openPopup(Menu menu)
	{
		menu.applyMask(POPUP);
		menu.applyMask(ExpressionMenu.POPUP_NO_BACKGROUND_TOP);
		MenuBuilder builder = MenuBuilder.create(3, "POPUP_MENU_MARKER");
		builder.limitOffset(0, 0, 0, Integer.MAX_VALUE);
		populatePopup(builder);

		Menu popupMenu = builder.build();
		menu.setMetadata("popup", popupMenu);
		popupMenu.overlay(menu, 1, 1, 6, 4);

		return popupMenu;
	}

	@Override
	public void save(JSONObject json)
	{
		JSONArray markers = new JSONArray();
		for (T marker : LIST)
		{
			JSONObject jsonObject = new JSONObject();
			codec.toJson(marker, jsonObject);
			markers.put(jsonObject);
		}
		json.put("markers", markers);
	}

	@Override
	public void load(JSONObject json)
	{
		LIST.clear();
		JSONArray markers = json.optJSONArray("markers");
		if (markers == null)
			return;
		for (int i = 0; i < markers.length(); i++)
		{
			JSONObject jsonObject = markers.getJSONObject(i);
			LIST.add(codec.fromJson(jsonObject));
		}
	}

	protected abstract ItemStack createIcon(T obj);
	protected abstract ItemStack createAddSlot();
	protected abstract T createFromItem(ItemStack itemStack);
	protected abstract ItemStack createFromObject(T obj);
	protected abstract boolean canBeAdded(ItemStack itemStack);
}
