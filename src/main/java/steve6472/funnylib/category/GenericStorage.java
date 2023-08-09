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
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.slots.IconSlot;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.MetaUtil;
import steve6472.funnylib.util.MiscUtil;
import steve6472.standalone.interactable.ex.ExpItems;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
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
	final Supplier<GenericStorage> nestedStorageConstructor;

	final List<Categorizable> itemList = new ArrayList<>();

	private final Map<Function<ItemStack, Boolean>, RegistratedType> checks = new HashMap<>();
	private final HashMap<String, RegistratedType> types = new HashMap<>();

	public GenericStorage(Supplier<GenericStorage> nestedStorageConstructor)
	{
		this.nestedStorageConstructor = nestedStorageConstructor;

		registerType("folder", () -> new Folder(this), Folder::createIcon, obj -> null, item -> null, item -> false);
	}

	RegistratedType getType(ItemStack itemStack)
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

	RegistratedType getType(String id)
	{
		return types.get(id);
	}

	@SuppressWarnings("unchecked")
	public <T extends Categorizable> void registerType(
		String id,
		Supplier<T> constructor,
		Function<T, ItemStack> icon,
		Function<T, ItemStack> itemFromObject,
		Function<ItemStack, T> objectFromItem,
		Consumer<Menu> populatePopup,
		Function<ItemStack, Boolean> check)
	{
		RegistratedType registratedType = new RegistratedType(
			(Supplier<Categorizable>) constructor,
			(Function<Categorizable, ItemStack>) icon,
			(Function<Categorizable, ItemStack>) itemFromObject,
			(Function<ItemStack, Categorizable>) objectFromItem,
			populatePopup);
		types.put(id, registratedType);
		checks.put(check, registratedType);
	}

	public <T extends Categorizable> void registerType(
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
	{/*
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
*/
	}

	public List<Categorizable> getItemList()
	{
		return itemList;
	}

	public Categorizable getItem(String id)
	{
		return itemList.stream().filter(p -> p.name().equals(id)).findFirst().orElse(null);
	}

	protected Menu createMenu()
	{
		return new GenericStorageMenu(this, itemName() + "s");
	}

	public void showToPlayer(Player player)
	{
		createMenu().showToPlayer(player);
	}

	@Override
	public void save(NBT nbt)
	{
		NBT[] compoundArray = nbt.createCompoundArray(itemList.size());
		for (int i = 0; i < itemList.size(); i++)
		{
			Categorizable object = itemList.get(i);
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

				Categorizable byId = types.get(id).constructor().get();
				byId.fromNBT(nbtObject);
				itemList.add(byId);
			}
		}
	}

	@SuppressWarnings({"unchecked", "unused"})
	protected <T> T getClickedObject(Class<T> clazz, Click c)
	{
		Categorizable clickedObject = MetaUtil.getValue(c.player(), Categorizable.class, "clicked_object");
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
