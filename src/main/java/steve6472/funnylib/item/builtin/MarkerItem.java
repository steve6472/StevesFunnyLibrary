package steve6472.funnylib.item.builtin;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.json.codec.codecs.MarkerCodec;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.ParticleUtil;

import java.util.Arrays;
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

		updateItem(context.getHandItem(), clickedBlock);

		result.setCancelled(true);
	}

	private static void updateItem(ItemStack itemStack, Block clickedBlock)
	{
		ItemStackBuilder builder = ItemStackBuilder.edit(itemStack);

		if (clickedBlock != null)
			builder
				.customTagInt("x", clickedBlock.getX())
				.customTagInt("y", clickedBlock.getY())
				.customTagInt("z", clickedBlock.getZ());

		builder.removeLore();

		if (builder.getCustomTagString("name") != null)
			builder
				.addLore(
					JSONMessage.create("Name: ").color(ChatColor.DARK_GRAY)
						.then(builder.getCustomTagString("name")).color(ChatColor.WHITE)
						.setItalic(JSONMessage.ItalicType.FALSE)
				);

		if (clickedBlock != null)
			builder
				.addLore(JSONMessage
					.create("Location: ")
					.color(ChatColor.DARK_GRAY)
					.then(Integer.toString(clickedBlock.getX()))
					.color(ChatColor.RED)
					.then("/")
					.color(ChatColor.WHITE)
					.then(Integer.toString(clickedBlock.getY()))
					.color(ChatColor.GREEN)
					.then("/")
					.color(ChatColor.WHITE)
					.then(Integer.toString(clickedBlock.getZ()))
					.color(ChatColor.BLUE)
					.setItalic(JSONMessage.ItalicType.FALSE)
				);
		else
			builder
				.addLore(JSONMessage
					.create("Location: ")
					.color(ChatColor.DARK_GRAY)
					.then(Integer.toString(builder.getCustomTagInt("x")))
					.color(ChatColor.RED)
					.then("/")
					.color(ChatColor.WHITE)
					.then(Integer.toString(builder.getCustomTagInt("y")))
					.color(ChatColor.GREEN)
					.then("/")
					.color(ChatColor.WHITE)
					.then(Integer.toString(builder.getCustomTagInt("z")))
					.color(ChatColor.BLUE)
					.setItalic(JSONMessage.ItalicType.FALSE)
				);

		builder
			.addLore(HELP_TEXT_0)
			.addLore(HELP_TEXT_1)
			.buildItemStack();
	}

	@Override
	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result)
	{
		if (!useType.isRight())
			return;

		final ItemStack handItem = context.getHandItem();
		int slot = context.getPlayer().getInventory().getHeldItemSlot();

		new AnvilGUI.Builder()
			.onComplete((completion) ->
			{
				ItemStack edited = ItemStackBuilder
					.editNonStatic(handItem)
					.customTagString("name", completion.getText())
					.buildItemStack();

				updateItem(edited, null);

				context.getPlayer().getInventory().setHeldItemSlot(slot);
				context.getPlayer().getInventory().setItem(EquipmentSlot.HAND, edited);

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

		ItemStackBuilder edit = ItemStackBuilder.edit(context.getHandItem());
		int x = edit.getCustomTagInt("x");
		int y = edit.getCustomTagInt("y");
		int z = edit.getCustomTagInt("z");

		ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, x, y, z, x + 1, y + 1, z + 1, 0, 0.2, OPTIONS);
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.PAPER).setName("Location Marker", ChatColor.DARK_AQUA).addLore(HELP_TEXT_0).addLore(HELP_TEXT_1).buildItemStack();
	}

	public static ItemStack newMarker(int x, int y, int z, String name, Material icon)
	{
		ItemStackBuilder itemStackBuilder = ItemStackBuilder
			.editNonStatic(FunnyLib.LOCATION_MARKER.newItemStack())
			.customTagInt("x", x)
			.customTagInt("y", y)
			.customTagInt("z", z)
			.customTagString("icon", icon.name());

		if (name != null)
			itemStackBuilder.customTagString("name", name);

		ItemStack itemStack = itemStackBuilder.buildItemStack();

		updateItem(itemStack, null);

		return itemStack;
	}

	public static ItemStack newMarker(MarkerCodec.Marker marker)
	{
		return newMarker(marker.x(), marker.y(), marker.z(), marker.name(), marker.icon());
	}
}
