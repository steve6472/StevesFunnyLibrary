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
import steve6472.funnylib.json.INbtConfig;
import steve6472.funnylib.menu.*;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.MetaUtil;
import steve6472.funnylib.util.MiscUtil;
import steve6472.standalone.interactable.ex.ExpItems;
import steve6472.standalone.interactable.ex.ExpressionMenu;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 2/18/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class GenericStorage implements INbtConfig
{
	private record Point(int x, int y) {}

	private final Supplier<GenericStorage> nestedStorageConstructor;

	private final List<ICategorizable> itemList = new ArrayList<>();

	protected final Mask MAIN_MASK;
	protected final Mask POPUP;
	private final MenuBuilder BUILDER;

	private record RegistratedType(
		Supplier<ICategorizable> constructor,
		Function<ICategorizable, ItemStack> icon,
		Function<ICategorizable, ItemStack> itemFromObject,
		Function<ItemStack, ICategorizable> objectFromItem,
		Consumer<MenuBuilder> populatePopup) {}

	private final Map<Function<ItemStack, Boolean>, RegistratedType> checks = new HashMap<>();
	private final HashMap<String, RegistratedType> types = new HashMap<>();

	public GenericStorage(Supplier<GenericStorage> nestedStorageConstructor)
	{
		this.nestedStorageConstructor = nestedStorageConstructor;

		MAIN_MASK = Mask.createMask()
		.addRow(".........", 5)
		.addRow("AXFBXXXLR")
		.addItem('X', SlotBuilder
			.create(ItemStackBuilder.create(Material.WHITE_STAINED_GLASS_PANE).setName("").buildItemStack()).setSticky())
		.addItem('B', SlotBuilder
			.create(ItemStackBuilder.create(Material.WHITE_STAINED_GLASS_PANE).setName("").buildItemStack()).setSticky())
		.addItem('L', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Left").setCustomModelData(10).setArmorColor(0xdddddd).setHideFlags(ItemFlag.HIDE_DYE).buildItemStack(), (c, m) -> m.move(-9, 0)))
		.addItem('R', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setName("Right").setCustomModelData(11).setArmorColor(0xdddddd).setHideFlags(ItemFlag.HIDE_DYE).buildItemStack(), (c, m) -> m.move(9, 0)))
		.addItem('A', SlotBuilder.stickyButtonSlot_(createAddSlot()
			, (c, m) ->
			{
				RegistratedType type = getType(c.itemOnCursor());
				if (type != null)
				{
					ICategorizable object = type.objectFromItem.apply(c.itemOnCursor());
					itemList.add(object);
					Point location = getItemLocation(itemList.size() - 1);
					m.setSlot(location.x, location.y, slot(object));
					m.reload();
				}
			}).allow(InventoryAction.SWAP_WITH_CURSOR))
		.addItem('F', SlotBuilder.stickyButtonSlot_(ItemStackBuilder.quick(Material.CHEST, "Add Folder", "#FFA500")
			, (c, m) ->
			{
				Folder folder = new Folder(nestedStorageConstructor.get());
				folder.setPrevious(this);
				itemList.add(folder);
				Point location = getItemLocation(itemList.size() - 1);
				m.setSlot(location.x, location.y, folderSlot(folder));
				m.reload();
			}).allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF))
		;

		POPUP = Mask.createMask()
			.addRow(".._____..")
			.addRow(".______X.")
			.addRow("._______.")
			.addRow(".______U.")
			.addRow(".______D.")
			.addRow("____P____")
			.addItem('_', SlotBuilder.create(MiscUtil.AIR).setSticky())
			.addItem('X', SlotBuilder.stickyButtonSlot_(ExpItems.POPUP_CLOSE.newItemStack(), (c, m) ->
			{
				construct(m);
				m.applyMask(MAIN_MASK);
			}))
		;

		BUILDER = MenuBuilder.create(6, itemName() + "s")
			.applyMask(MAIN_MASK)
			.allowPlayerInventory()
		;

		registerType("folder", () -> new Folder(this), Folder::createIcon, obj -> null, item -> null, item -> false);
	}

	private RegistratedType getType(ItemStack itemStack)
	{
		for (Function<ItemStack, Boolean> itemStackBooleanFunction : checks.keySet())
		{
			if (itemStackBooleanFunction.apply(itemStack))
			{
				return checks.get(itemStackBooleanFunction);
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends ICategorizable> void registerType(
		String id,
		Supplier<T> constructor,
		Function<T, ItemStack> icon,
		Function<T, ItemStack> itemFromObject,
		Function<ItemStack, T> objectFromItem,
		Consumer<MenuBuilder> populatePopup,
		Function<ItemStack, Boolean> check)
	{
		RegistratedType registratedType = new RegistratedType(
			(Supplier<ICategorizable>) constructor,
			(Function<ICategorizable, ItemStack>) icon,
			(Function<ICategorizable, ItemStack>) itemFromObject,
			(Function<ItemStack, ICategorizable>) objectFromItem,
			populatePopup);
		types.put(id, registratedType);
		checks.put(check, registratedType);
	}

	public <T extends ICategorizable> void registerType(
		String id,
		Supplier<T> constructor,
		Function<T, ItemStack> icon,
		Function<T, ItemStack> itemFromObject,
		Function<ItemStack, T> objectFromItem,
		Function<ItemStack, Boolean> check)
	{
		registerType(id, constructor, icon, itemFromObject, objectFromItem, null, check);
	}

	public void setPrevious(GenericStorage previous)
	{
		MAIN_MASK.addItem('B', SlotBuilder.create(
			ItemStackBuilder
				.create(Material.LEATHER_HORSE_ARMOR)
				.setName("Back")
				.setCustomModelData(10)
				.setArmorColor(0x70c4ff)
				.setHideFlags(ItemFlag.HIDE_DYE)
				.buildItemStack())
			.allow(ClickType.LEFT, ClickType.RIGHT)
			.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
			.onClick((c, m) -> Response.redirect(previous.createMenu())));

		BUILDER.applyMask(MAIN_MASK);

	}

	private SlotBuilder slot(ICategorizable object)
	{
		RegistratedType registratedType = types.get(object.id());
		ItemStack icon = registratedType.icon.apply(object);
		ItemStackBuilder.editNonStatic(icon).addLore(JSONMessage.create()).addLore(JSONMessage.create("Type: " + object.id()).color(ChatColor.DARK_GRAY).setItalic(JSONMessage.ItalicType.FALSE));

		return SlotBuilder.create(icon)
			.allow(ClickType.LEFT, ClickType.RIGHT)
			.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
			.onClick(ClickType.LEFT, (c, m) -> Response.setItemToCursor(registratedType.itemFromObject.apply(object)))
			.onClick(ClickType.RIGHT, (c, m) ->
			{
				MetaUtil.setMeta(c.player(), "clicked_object", object);
				openPopup(object, m, true);
				return Response.cancel();
			});
	}

	private static Point getItemLocation(int item)
	{
		int page = item / 45;
		int x = ((item % 45) % 9) + (page * 9);
		int y = (item % 45) / 9;
		return new Point(x, y);
	}

	public SlotBuilder folderSlot(Folder folder)
	{
		return SlotBuilder.create(folder.createIcon())
		.allow(ClickType.LEFT, ClickType.RIGHT)
		.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF)
		.onClick(ClickType.LEFT, (c, m) -> Response.redirect(folder.getNestedStorage().createMenu()))
		.onClick(ClickType.RIGHT, (c, m) ->
		{
			MetaUtil.setMeta(c.player(), "clicked_object", folder);
			openPopup(folder, m, false);
			return Response.cancel();
		});
	}

	public List<ICategorizable> getItemList()
	{
		return itemList;
	}

	protected void construct(Menu menu)
	{
		menu.clear();

		for (int i = 0; i < itemList.size(); i++)
		{
			ICategorizable object = itemList.get(i);
			Point itemLoc = getItemLocation(i);
			if (object instanceof Folder folder)
			{
				menu.setSlot(itemLoc.x(), itemLoc.y(), folderSlot(folder));
			} else
			{
				menu.setSlot(itemLoc.x(), itemLoc.y(), slot(object));
			}
		}
	}

	protected Menu createMenu()
	{
		Menu menu = BUILDER.build();

		construct(menu);
		menu.applyMask(MAIN_MASK);
		return menu;
	}

	public void showToPlayer(Player player)
	{
		createMenu().showToPlayer(player);
	}
	
	private void populatePopup(ICategorizable clicked, MenuBuilder builder, boolean customPopulate)
	{
		// Custom Icon
		builder.slot(0, 0, SlotBuilder.stickyButtonSlot_(ItemStackBuilder.quick(Material.TORCH, "Set Icon"), (c, m) ->
		{
			ICategorizable clickedObject = getClickedObject(c);
			clickedObject.setIcon(c.itemOnCursor().getType().isAir() ? Material.PAPER : c.itemOnCursor().getType());
			c.player().sendMessage(ChatColor.GREEN + "Icon for " + itemName() + " " + ChatColor.WHITE + "'" + clickedObject.name() + "'" + ChatColor.GREEN + " changed!");

			construct(m);
			m.applyMask(MAIN_MASK);
			openPopup(clicked, m, true);
		}).allow(InventoryAction.SWAP_WITH_CURSOR));

		// Rename Marker
		builder.slot(1, 0, SlotBuilder.stickyButtonSlot_(ItemStackBuilder.quick(Material.NAME_TAG, "Rename " + itemName()), (c, m) ->
		{
			ICategorizable clickedObject = getClickedObject(c);
			showRenameGUI(clickedObject, m, c.player());
		}).allow(InventoryAction.SWAP_WITH_CURSOR));

		// Remove Marker
		builder.slot(5, 3, SlotBuilder.stickyButtonSlot_(ItemStackBuilder.quick(Material.BARRIER, "Remove " + itemName(), ChatColor.RED), (c, m) ->
		{
			ICategorizable clickedObject = getClickedObject(c);
			itemList.remove(clickedObject);
			c.player().sendMessage(ChatColor.GREEN + (clickedObject instanceof Folder ? "Folder" : itemName()) + " " + ChatColor.WHITE + "'" + clickedObject.name() + "'" + ChatColor.GREEN + " removed!");

			construct(m);
			m.applyMask(MAIN_MASK);
		}).allow(InventoryAction.SWAP_WITH_CURSOR));

		if (customPopulate)
		{
			Consumer<MenuBuilder> populatePopup = types.get(clicked.id()).populatePopup;
			if (populatePopup != null)
				populatePopup.accept(builder);
		}
	}

	private void showRenameGUI(ICategorizable object, Menu menu, Player player)
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
						construct(oldMenu);
						oldMenu.applyMask(MAIN_MASK);
						openPopup(object, oldMenu, true);
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

	protected void openPopup(ICategorizable clicked, Menu menu, boolean customPopulate)
	{
		menu.applyMask(POPUP);
		menu.applyMask(ExpressionMenu.POPUP_NO_BACKGROUND_TOP);
		MenuBuilder builder = MenuBuilder.create(3, "POPUP_MENU_STORAGE");
		builder.limitOffset(0, 0, 0, Integer.MAX_VALUE);
		populatePopup(clicked, builder, customPopulate);

		Menu popupMenu = builder.build();
		menu.setMetadata("popup", popupMenu);
		popupMenu.overlay(menu, 1, 1, 6, 4);
	}

	@Override
	public void save(NBT nbt)
	{
		NBT[] compoundArray = nbt.createCompoundArray(itemList.size());
		for (int i = 0; i < itemList.size(); i++)
		{
			ICategorizable object = itemList.get(i);
			NBT nbtObject = compoundArray[i];
			if (object instanceof Folder folder)
			{
				nbtObject.setBoolean("_storage_folder", true);
				folder.toNBT(nbtObject);
			} else
			{
				object.toNBT(nbtObject);
				nbtObject.setString("_id", object.id());
			}
		}
		nbt.setCompoundArray(storageId(), compoundArray);
	}

	@Override
	public void load(NBT nbt)
	{
		itemList.clear();
		if (!nbt.hasCompoundArray(storageId()))
			return;
		NBT[] objects = nbt.getCompoundArray(storageId());
		for (NBT nbtObject : objects)
		{
			if (nbtObject.getBoolean("_storage_folder", false))
			{
				Folder folder = new Folder(nestedStorageConstructor.get());
				folder.setPrevious(this);
				folder.fromNBT(nbtObject);
				itemList.add(folder);
			} else
			{
				String id = nbtObject.getString("_id");

				ICategorizable byId = types.get(id).constructor.get();
				byId.fromNBT(nbtObject);
				itemList.add(byId);
			}
		}
	}
	
	protected ICategorizable getClickedObject(Click c)
	{
		ICategorizable clickedObject = MetaUtil.getValue(c.player(), ICategorizable.class, "clicked_object");
		if (clickedObject == null)
		{
			c.player().sendMessage(ChatColor.RED + "'clicked_object' meta is missing!");
			throw new RuntimeException("'clicked_object' meta is missing!");
		} else
		{
			return clickedObject;
		}
	}

	@SuppressWarnings({"unchecked", "unused"})
	protected <T> T getClickedObject(Class<T> clazz, Click c)
	{
		ICategorizable clickedObject = MetaUtil.getValue(c.player(), ICategorizable.class, "clicked_object");
		if (clickedObject == null)
		{
			c.player().sendMessage(ChatColor.RED + "'clicked_object' meta is missing!");
			throw new RuntimeException("'clicked_object' meta is missing!");
		} else
		{
			return (T) clickedObject;
		}
	}

	protected abstract ItemStack createAddSlot();
	protected abstract String storageId();
	protected abstract String itemName();
}
