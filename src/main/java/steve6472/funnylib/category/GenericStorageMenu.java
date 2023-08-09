package steve6472.funnylib.category;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.menu.*;
import steve6472.funnylib.menu.slots.IconSlot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.menu.slots.buttons.MoveButtonSlot;
import steve6472.funnylib.menu.windows.Popup;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.MetaUtil;

import java.util.Collections;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 7/2/2023
 * Project: StevesFunnyLibrary <br>
 */
public class GenericStorageMenu extends Menu
{
	private final GenericStorage genericStorage;
	private Mask mainMask;

	public GenericStorageMenu(GenericStorage genericStorage, String title)
	{
		super(6, title, true);
		this.genericStorage = genericStorage;
		allowPlayerInventory();
	}

	@Override
	protected void setup()
	{
		mainMask = new Mask()
			.addRow(".........", 5)
			.addRow("AXFBXXXLR")
			.addItem('X', () -> new IconSlot(ItemStackBuilder.quick(Material.WHITE_STAINED_GLASS_PANE, ""), true))
			.addItem('B', () -> new IconSlot(ItemStackBuilder.quick(Material.WHITE_STAINED_GLASS_PANE, ""), true))
			.addItem('L', () -> new MoveButtonSlot(ItemStackBuilder
				.create(Material.LEATHER_HORSE_ARMOR)
				.setName("Left")
				.setCustomModelData(10)
				.setArmorColor(0xdddddd)
				.setHideFlags(ItemFlag.HIDE_DYE)
				.buildItemStack(), -9, 0, true))
			.addItem('R', () -> new MoveButtonSlot(ItemStackBuilder
				.create(Material.LEATHER_HORSE_ARMOR)
				.setName("Right")
				.setCustomModelData(11)
				.setArmorColor(0xdddddd)
				.setHideFlags(ItemFlag.HIDE_DYE)
				.buildItemStack(), 9, 0, true))
			.addItem('A', () -> new ButtonSlot(genericStorage.createAddSlot(), true)
				.setClick(c ->
				{
					RegistratedType type = genericStorage.getType(c.itemOnCursor());
					if (type != null)
					{
						Categorizable object = type.objectFromItem().apply(c.itemOnCursor());
						genericStorage.itemList.add(object);
						Point location = Point.getItemLocation(genericStorage.itemList.size() - 1);
						c.menu().setSlot(location.x(), location.y(), slot(object));
						c.menu().reload();
					}
					return Response.cancel();
				}))
			.addItem('F', () -> new ButtonSlot(ItemStackBuilder.quick(Material.CHEST, "Add Folder", "#FFA500"), true)
				.setClick(c ->
				{
					Folder folder = new Folder(genericStorage.nestedStorageConstructor.get());
					folder.setPrevious(genericStorage);
					genericStorage.itemList.add(folder);
					Point location = Point.getItemLocation(genericStorage.itemList.size() - 1);
					c.menu().setSlot(location.x(), location.y(), folderSlot(folder));
					c.menu().reload();
					return Response.cancel();
				})
			)
		;

		applyMask(mainMask);

		construct(this);
	}

	protected void construct(Menu menu)
	{
		menu.clear();

		applyMask(mainMask);

		for (int i = 0; i < genericStorage.itemList.size(); i++)
		{
			Categorizable object = genericStorage.itemList.get(i);
			Point itemLoc = Point.getItemLocation(i);
			if (object instanceof Folder folder)
			{
				menu.setSlot(itemLoc.x(), itemLoc.y(), folderSlot(folder));
			} else
			{
				menu.setSlot(itemLoc.x(), itemLoc.y(), slot(object));
			}
		}
	}

	//region util

	Slot slot(Categorizable object)
	{
		RegistratedType registratedType = genericStorage.getType(object.id());
		ItemStack icon = registratedType.icon().apply(object);
		ItemStackBuilder.editNonStatic(icon).addLore(JSONMessage.create()).addLore(JSONMessage.create("Type: " + object.id()).color(ChatColor.DARK_GRAY).setItalic(JSONMessage.ItalicType.FALSE));

		return new ButtonSlot(icon, false)
			.setClick(c ->
			{
				if (c.type() == ClickType.LEFT)
				{
					return Response.setItemToCursor(registratedType.itemFromObject().apply(object));
				} else if (c.type() == ClickType.RIGHT)
				{
					MetaUtil.setMeta(c.player(), "clicked_object", object);
					openPopup(object, c.menu(), true);
				}
				return Response.cancel();
			});
	}

	Slot folderSlot(Folder folder)
	{
		return new ButtonSlot(folder.createIcon(), false)
			.setClick(c ->
			{
				if (c.type() == ClickType.LEFT)
				{
					return Response.redirect(folder.getNestedStorage().createMenu());
				} else if (c.type() == ClickType.RIGHT)
				{
					MetaUtil.setMeta(c.player(), "clicked_object", folder);
					openPopup(folder, c.menu(), false);
				}
				return Response.cancel();
			});
	}

	private void showRenameGUI(Categorizable object, Menu menu, Player player)
	{
		MetaUtil.setMeta(player, "old_menu", menu);
		player.closeInventory();

		new AnvilGUI.Builder()
			.onClick((slot, state) ->
			{
				if (slot != AnvilGUI.Slot.OUTPUT)
					return Collections.emptyList();

				object.setName(state.getText());
				return Collections.singletonList(AnvilGUI.ResponseAction.run(() ->
				{
					Menu oldMenu = MetaUtil.getValue(player, Menu.class, "old_menu");
					if (oldMenu == null)
					{
						player.sendMessage(ChatColor.RED + "'old_menu' meta is missing!");
					} else
					{
						construct(oldMenu);
						//						oldMenu.applyMask(MAIN_MASK);
						//						openPopup(object, oldMenu, true);
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

	protected void openPopup(Categorizable clicked, Menu menu, boolean customPopulate)
	{
		Popup popup = new Popup(false);
		populatePopup(clicked, popup, menu, customPopulate);
		addWindow(popup);
		/*
		menu.applyMask(POPUP);
		menu.applyMask(ExpressionMenu.POPUP_NO_BACKGROUND_TOP);
		MenuBuilder builder = MenuBuilder.create(3, "POPUP_MENU_STORAGE");
		builder.limitOffset(0, 0, 0, Integer.MAX_VALUE);
		populatePopup(clicked, builder, customPopulate);

		Menu popupMenu = builder.build();
		menu.setMetadata("popup", popupMenu);
		popupMenu.overlay(menu, 1, 1, 6, 4);*/
	}

	private void populatePopup(Categorizable clicked, Popup popup, Menu menu, boolean customPopulate)
	{
		// Custom Icon
		popup.setPopupSlot(0, 0, new ButtonSlot(ItemStackBuilder.quick(Material.TORCH, "Set Icon"), false).setClick(c ->
		{
			Categorizable clickedObject = getClickedObject(c);
			clickedObject.setIcon(c.itemOnCursor().getType().isAir() ? Material.PAPER : c.itemOnCursor().getType());
			c.player().sendMessage(ChatColor.GREEN + "Icon for " + genericStorage.itemName() + " " + ChatColor.WHITE + "'" + clickedObject.name() + "'" + ChatColor.GREEN + " changed!");

			construct(c.menu());
//			menu.applyMask(MAIN_MASK);
			openPopup(clicked, menu, true);
			return Response.cancel();
		}));
//		popup.setPopupSlot(0, 0, SlotBuilder.stickyButtonSlot_(ItemStackBuilder.quick(Material.TORCH, "Set Icon"), (c, m) ->
//		{
//			Categorizable clickedObject = getClickedObject(c);
//			clickedObject.setIcon(c.itemOnCursor().getType().isAir() ? Material.PAPER : c.itemOnCursor().getType());
//			c.player().sendMessage(ChatColor.GREEN + "Icon for " + itemName() + " " + ChatColor.WHITE + "'" + clickedObject.name() + "'" + ChatColor.GREEN + " changed!");
//
//			construct(m);
//			m.applyMask(MAIN_MASK);
//			openPopup(clicked, m, true);
//		}).allow(InventoryAction.SWAP_WITH_CURSOR));

		// Rename Marker
//		popup.setPopupSlot(1, 0, SlotBuilder.stickyButtonSlot_(ItemStackBuilder.quick(Material.NAME_TAG, "Rename " + itemName()), (c, m) ->
//		{
//			Categorizable clickedObject = getClickedObject(c);
//			showRenameGUI(clickedObject, m, c.player());
//		}).allow(InventoryAction.SWAP_WITH_CURSOR));

		popup.setPopupSlot(1, 0, new ButtonSlot(ItemStackBuilder.quick(Material.NAME_TAG, "Rename " + genericStorage.itemName()), false).setClick(c ->
		{
			Categorizable clickedObject = getClickedObject(c);
			showRenameGUI(clickedObject, menu, c.player());
			return Response.cancel();
		}));

		// Remove Marker
		popup.setPopupSlot(0, 1, new ButtonSlot(ItemStackBuilder.quick(Material.BARRIER, "Remove " + genericStorage.itemName(), ChatColor.RED), true).setClick(c ->
		{
			Categorizable clickedObject = getClickedObject(c);
			genericStorage.itemList.remove(clickedObject);
			c.player().sendMessage(ChatColor.GREEN + (clickedObject instanceof Folder ? "Folder" : genericStorage.itemName()) + " " + ChatColor.WHITE + "'" + clickedObject.name() + "'" + ChatColor.GREEN + " removed!");

			construct(menu);
			//			menu.applyMask(MAIN_MASK);
			return Response.cancel();
		}));

		// Remove Marker
		popup.setExtraOption(new ButtonSlot(ItemStackBuilder.quick(Material.BARRIER, "Remove " + genericStorage.itemName(), ChatColor.RED), true).setClick(c ->
		{
			Categorizable clickedObject = getClickedObject(c);
			genericStorage.itemList.remove(clickedObject);
			c.player().sendMessage(ChatColor.GREEN + (clickedObject instanceof Folder ? "Folder" : genericStorage.itemName()) + " " + ChatColor.WHITE + "'" + clickedObject.name() + "'" + ChatColor.GREEN + " removed!");

			construct(menu);
//			menu.applyMask(MAIN_MASK);
			return Response.cancel();
		}));

		if (customPopulate)
		{
			Consumer<Menu> populatePopup = genericStorage.getType(clicked.id()).populatePopup();
			if (populatePopup != null)
				populatePopup.accept(popup);
		}
	}

	protected Categorizable getClickedObject(Click c)
	{
		Categorizable clickedObject = MetaUtil.getValue(c.player(), Categorizable.class, "clicked_object");
		if (clickedObject == null)
		{
			c.player().sendMessage(ChatColor.RED + "'clicked_object' meta is missing!");
			throw new RuntimeException("'clicked_object' meta is missing!");
		} else
		{
			return clickedObject;
		}
	}

	//endregion util
}
