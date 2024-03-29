package steve6472.funnylib.item.builtin;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3i;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.data.Marker;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.*;

import java.util.Collections;

/**
 * Created by steve6472
 * Date: 9/10/2022
 * Project: StevesFunnyLibrary
 */
public class MarkerItem extends CustomItem implements TickInHandEvent
{
	public static final String ID = "location_marker";
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.WHITE, 0.75f);
	private static final JSONMessage HELP_TEXT_0 = JSONMessage.create("Left click a block to mark it").color(ChatColor.GRAY).setItalic(JSONMessage.ItalicType.FALSE);
	private static final JSONMessage HELP_TEXT_1 = JSONMessage.create("Right click air to name marker").color(ChatColor.GRAY).setItalic(JSONMessage.ItalicType.FALSE);

	@Override
	public String id()
	{
		return ID;
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		if (!useType.isLeft())
			return;

		Block clickedBlock = context.getBlock();
		if (clickedBlock == null) return;

		updateItem(context.getItemData(), clickedBlock);

		result.setCancelled(true);
	}

	private static void updateItem(ItemNBT data, Block clickedBlock)
	{
		ItemStackBuilder builder = ItemStackBuilder.edit(data);

		if (clickedBlock != null)
			data.set3i("location", clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ());

		builder.removeLore();

		if (builder.hasString("name"))
			builder
				.addLore(
					JSONMessage.create("Name: ").color(ChatColor.DARK_GRAY)
						.then(builder.getString("name")).color(ChatColor.WHITE)
						.setItalic(JSONMessage.ItalicType.FALSE)
				);

		if (clickedBlock != null)
			builder.addLore(Messages.createLocationMessage(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ()));
		else
		{
			Vector3i location = data.get3i("location");
			builder.addLore(Messages.createLocationMessage(location.x, location.y, location.z));
		}

		builder
			.addLore(HELP_TEXT_0)
			.addLore(HELP_TEXT_1);
	}

	@Override
	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result)
	{
		if (!useType.isRight())
			return;

		new AnvilGUI.Builder()
			.onClick((slot, state) ->
			{
				if (slot != AnvilGUI.Slot.OUTPUT)
					return Collections.emptyList();

				context.getItemData().setString("name", state.getText());
				updateItem(context.getItemData(), null);

				return Collections.singletonList(AnvilGUI.ResponseAction.close());
			})
			.text(" ")
			.itemLeft(new ItemStack(Material.PAPER))                      //use a custom item for the first slot
			.title("Enter new name")
			.plugin(FunnyLib.getPlugin())
			.open(context.getPlayer());
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (FunnyLib.getUptimeTicks() % 3 != 0) return;

		ItemNBT data = context.getItemData();

		if (!data.hasCompound("location"))
			return;

		Vector3i location = data.get3i("location");

		int x = location.x;
		int y = location.y;
		int z = location.z;

		ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, x, y, z, x + 1, y + 1, z + 1, 0, 0.2, OPTIONS);

		Location playerLoc = context.getPlayer().getEyeLocation();

		if (playerLoc.toVector().toVector3i().distance(location) <= 32)
			return;

		Vector3f normalize = playerLoc
			.toVector()
			.toVector3f()
			.sub(new Vector3f(location).add(0.5f, 0.5f, 0.5f))
			.normalize()
			.mul(-1f);
		Vector3f a = normalize.mul(2f, new Vector3f());
		Vector3f b = normalize.mul(4f, new Vector3f());

		float px = (float) playerLoc.getX();
		float py = (float) playerLoc.getY();
		float pz = (float) playerLoc.getZ();
		ParticleUtil.line(context.getPlayer(), Particle.REDSTONE, a.x + px, a.y + py, a.z + pz, b.x + px, b.y + py, b.z + pz, 0, 0.2, OPTIONS);
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.PAPER).setName("Location Marker", ChatColor.DARK_AQUA).addLore(HELP_TEXT_0).addLore(HELP_TEXT_1).buildItemStack();
	}

	public static ItemStack newMarker(int x, int y, int z, String name, Material icon)
	{
		ItemNBT data = ItemNBT.create(FunnyLib.LOCATION_MARKER.newItemStack());
		data.set3i("location", x, y, z);

		data.setString("icon", icon.name());
		if (name != null)
			data.setString("name", name);

		updateItem(data, null);

		data.save();
		return data.getItemStack();
	}

	public static ItemStack newMarker(Marker marker)
	{
		return newMarker(marker.x(), marker.y(), marker.z(), marker.name(), marker.icon());
	}
}
