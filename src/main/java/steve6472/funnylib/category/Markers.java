package steve6472.funnylib.category;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.data.AreaSelection;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.menu.MenuBuilder;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.Messages;

/**
 * Created by steve6472
 * Date: 2/19/2023
 * Project: StevesFunnyLibrary <br>
 */
public class Markers extends GenericStorage
{
	public Markers()
	{
		super(Markers::new);

		registerType("marker", () -> new Marker(0, 0, 0, null),
			obj -> ItemStackBuilder
				.create(obj.icon())
				.setName(obj.name() == null ? "Marker" : obj.name(), ChatColor.DARK_AQUA)
				.addLore(Messages.createLocationMessage(obj.x(), obj.y(), obj.z()))
				.buildItemStack(),
			Marker::toItem,
			Marker::fromItem,
			builder -> builder.slot(0, 1, SlotBuilder
				.create(ItemStackBuilder.quick(Material.COMMAND_BLOCK, "Teleport", ChatColor.WHITE))
				.allow(ClickType.LEFT)
				.allow(InventoryAction.PICKUP_ALL)
				.onClick((c, cm) ->
				{
					Marker clickedObject = getClickedObject(Marker.class, c);
					c.player().teleport(new Location(c.player().getWorld(), clickedObject.x(), clickedObject.y(), clickedObject.z()));
					return Response.exit();
				})),
			itemStack -> Items.getCustomItem(itemStack) == FunnyLib.LOCATION_MARKER);

		registerType("area_selection", () -> new AreaSelection(new Vector3i(), new Vector3i(), null),
			obj -> ItemStackBuilder
				.create(obj.icon())
				.setName(obj.name() == null ? "Area Selection" : obj.name(), ChatColor.DARK_AQUA)
				.addLore(Messages.createLocationMessage("Start: ", obj.getStart().x(), obj.getStart().y(), obj.getStart().z()))
				.addLore(Messages.createLocationMessage("End: ", obj.getEnd().x(), obj.getEnd().y(), obj.getEnd().z()))
				.buildItemStack(),
			AreaSelection::toItem,
			AreaSelection::fromItem,
			itemStack -> Items.getCustomItem(itemStack) == FunnyLib.AREA_LOCATION_MARKER);
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
	protected String storageId()
	{
		return "markers";
	}

	@Override
	protected String itemName()
	{
		return "Marker";
	}
}
