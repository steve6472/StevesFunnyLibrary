package steve6472.funnylib.json.codec.codecs;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.builtin.TeleportButtonBlock;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.MarkerItem;
import steve6472.funnylib.json.codec.Codec;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.SlotBuilder;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MiscUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class MarkerCodec extends Codec<Vector>
{
	@Override
	public Vector fromJson(JSONObject json)
	{
		if (json.optBoolean("null", false))
			return null;
		return new Vector(json.getInt("x"), json.getInt("y"), json.getInt("z"));
	}

	@Override
	public void toJson(Vector obj, JSONObject json)
	{
		if (obj == null)
		{
			json.put("null", true);
			return;
		}
		json.put("x", obj.getBlockX());
		json.put("y", obj.getBlockY());
		json.put("z", obj.getBlockZ());
	}

	public static ItemStack toItem(Vector vec)
	{
		if (vec == null)
			return MiscUtil.AIR;

		return ItemStackBuilder.edit(FunnyLib.LOCATION_MARKER.newItemStack())
			.customTagInt("x", vec.getBlockX())
			.customTagInt("y", vec.getBlockY())
			.customTagInt("z", vec.getBlockZ())
			.removeLore()
			.addLore(ChatColor.DARK_GRAY + "Location: " + ChatColor.RED + vec.getBlockX() + ChatColor.WHITE + "/" + ChatColor.GREEN + vec.getBlockY() + ChatColor.WHITE + "/" + ChatColor.BLUE + vec.getBlockZ())
			.buildItemStack();
	}

	public static Vector toVector(ItemStack item)
	{
		if (item.getType().isAir())
			return null;

		ItemStackBuilder edit = ItemStackBuilder.edit(item);
		int x = edit.getCustomTagInt("x");
		int y = edit.getCustomTagInt("y");
		int z = edit.getCustomTagInt("z");

		return new Vector(x, y, z);
	}

	public static SlotBuilder slotBuilder(Vector current, Consumer<Vector> set)
	{
		return SlotBuilder
			.create(toItem(current))
			.allow(InventoryAction.PICKUP_ALL, InventoryAction.PLACE_ALL)
			.allow(ClickType.LEFT)
			.onClick((c, cm) ->
			{
				if (c.itemOnCursor().getType().isAir())
				{
					set.accept(null);
					c.slot().setItem(MiscUtil.AIR);
					return Response.cancel();
				}

				CustomItem customItem = Items.getCustomItem(c.itemOnCursor());
				if (customItem != FunnyLib.LOCATION_MARKER)
					return Response.cancel();

				set.accept(toVector(c.itemOnCursor()));
				ItemStack clone = c.itemOnCursor().clone();
				clone.setAmount(1);
				c.slot().setItem(clone);
				return Response.cancel();
			});
	}
}
