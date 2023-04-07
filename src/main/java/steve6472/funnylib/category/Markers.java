package steve6472.funnylib.category;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.MarkerItem;
import steve6472.funnylib.json.codec.codecs.MarkerCodec;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 2/19/2023
 * Project: StevesFunnyLibrary <br>
 */
public class Markers extends GenericStorage<MarkerCodec.Marker>
{
	public Markers()
	{
		super(MarkerCodec.Marker.class, new MarkerCodec());
	}

	@Override
	protected ItemStack createIcon(MarkerCodec.Marker obj)
	{
		return ItemStackBuilder
			.create(obj.icon())
			.setName(obj.name() == null ? "Marker" : obj.name(), ChatColor.DARK_AQUA)
			.addLore(JSONMessage
				.create("Location: ")
				.color(ChatColor.DARK_GRAY)
				.then(Integer.toString(obj.x()))
				.color(ChatColor.RED)
				.then("/")
				.color(ChatColor.WHITE)
				.then(Integer.toString(obj.y()))
				.color(ChatColor.GREEN)
				.then("/")
				.color(ChatColor.WHITE)
				.then(Integer.toString(obj.z()))
				.color(ChatColor.BLUE)
				.setItalic(JSONMessage.ItalicType.FALSE))
			.buildItemStack();
	}

	@Override
	protected ItemStack createAddSlot()
	{
		return ItemStackBuilder
			.create(Material.MAP)
			.setName("Add Marker", ChatColor.WHITE)
			.addLore(JSONMessage.create("Click with Marker item on cursor").color(ChatColor.GRAY).setItalic(JSONMessage.ItalicType.FALSE))
			.addLore(JSONMessage.create("to add it to the end of the list").color(ChatColor.GRAY).setItalic(JSONMessage.ItalicType.FALSE))
			.buildItemStack();
	}

	@Override
	protected MarkerCodec.Marker createFromItem(ItemStack itemStack)
	{
		ItemStackBuilder edit = ItemStackBuilder.editNonStatic(itemStack);
		String icon = edit.getCustomTagString("icon");
		return new MarkerCodec.Marker(
			edit.getCustomTagInt("x"),
			edit.getCustomTagInt("y"),
			edit.getCustomTagInt("z"),
			edit.getCustomTagString("name"),
			icon == null ? Material.PAPER : Material.valueOf(icon)
		);
	}

	@Override
	protected ItemStack createFromObject(MarkerCodec.Marker obj)
	{
		return MarkerItem.newMarker(obj);
	}

	@Override
	protected boolean canBeAdded(ItemStack itemStack)
	{
		return Items.getCustomItem(itemStack) == FunnyLib.LOCATION_MARKER;
	}
}
