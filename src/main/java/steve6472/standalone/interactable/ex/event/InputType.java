package steve6472.standalone.interactable.ex.event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.data.AreaSelection;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.ItemStackBuilder;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 6/18/2023
 * Project: StevesFunnyLibrary <br>
 */
public class InputType<T>
{
	public static final InputType<Player> PLAYER = new InputType<>("player", ItemStackBuilder.quick(Material.PLAYER_HEAD, "Player"), (nbt, obj) -> nbt.setUUID("data", obj.getUniqueId()), nbt -> Bukkit.getPlayer(nbt.getUUID("data")));
	public static final InputType<Integer> INT = new InputType<>("int", ItemStackBuilder.quick(Material.IRON_NUGGET, "Integer"), (nbt, obj) -> nbt.setInt("data", obj), nbt -> nbt.getInt("data", 0));
	public static final InputType<String> STRING = new InputType<>("string", ItemStackBuilder.quick(Material.STRING, "String"), (nbt, obj) -> nbt.setString("data", obj), nbt -> nbt.getString("data", ""));
	public static final InputType<Location> LOCATION = new InputType<>("loc", ItemStackBuilder.quick(Material.ENDER_PEARL, "Location"), (nbt, obj) -> nbt.setLocation("data", obj), nbt -> nbt.getLocation("data", null));
	public static final InputType<Marker> MARKER = new InputType<>("marker", ItemStackBuilder.quick(Material.ARMOR_STAND, "Marker"), (nbt, obj) -> obj.toNBT(nbt), Marker::new);
	public static final InputType<AreaSelection> AREA = new InputType<>("area", ItemStackBuilder.quick(Material.LIGHT_BLUE_STAINED_GLASS, "Area"), (nbt, obj) -> obj.toNBT(nbt), AreaSelection::new);
	public static final InputType<ItemStack> ITEM = new InputType<>("item", ItemStackBuilder.quick(Material.STONE, "Item"), (nbt, obj) -> nbt.setItemStack("data", obj), nbt -> nbt.getItemStack("data"));

	private final String id;
	private final ItemStack emptyIcon;
	private final BiConsumer<NBT, T> toNBT;
	private final Function<NBT, T> fromNBT;

	InputType(String id, ItemStack emptyIcon, BiConsumer<NBT, T> toNBT, Function<NBT, T> fromNBT)
	{
		this.id = id;
		this.emptyIcon = emptyIcon;
		this.toNBT = toNBT;
		this.fromNBT = fromNBT;
	}

	public String getId()
	{
		return id;
	}

	public ItemStack getEmptyIcon()
	{
		return emptyIcon.clone();
	}

	public void toNBT(NBT compound, T obj)
	{
		toNBT.accept(compound, obj);
	}

	public T fromNBT(NBT compound)
	{
		return fromNBT.apply(compound);
	}

	@Override
	public String toString()
	{
		return "InputType{" + "id='" + id + '\'' + ", emptyIcon=" + emptyIcon + '}';
	}
}
