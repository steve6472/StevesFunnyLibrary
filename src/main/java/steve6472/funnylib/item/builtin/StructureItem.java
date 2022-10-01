package steve6472.funnylib.item.builtin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerContext;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.events.ItemClickEvents;
import steve6472.funnylib.item.events.SwapHandEvent;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.ParticleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class StructureItem extends CustomItem implements ItemClickEvents, TickInHandEvent, SwapHandEvent
{
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.AQUA, 0.75f);

	@Override
	public void rightClickAir(PlayerContext context, PlayerInteractEvent e)
	{
		ItemStackBuilder edit = ItemStackBuilder.edit(context.getHandItem());

		if (edit.getCustomTagByte("saving") == 0)
			return;

		JSONArray blocks = new JSONArray();

		int x0 = edit.getCustomTagInt("x0");
		int y0 = edit.getCustomTagInt("y0");
		int z0 = edit.getCustomTagInt("z0");
		int x1 = edit.getCustomTagInt("x1");
		int y1 = edit.getCustomTagInt("y1");
		int z1 = edit.getCustomTagInt("z1");

		for (int i = x0; i <= x1; i++)
		{
			for (int j = y0; j <= y1; j++)
			{
				for (int k = z0; k <= z1; k++)
				{
					Block blockAt = context.getWorld().getBlockAt(i, j, k);
					JSONObject js = new JSONObject();
					js.put("x", i - x0);
					js.put("y", j - y0);
					js.put("z", k - z0);
					js.put("b", blockAt.getBlockData().getAsString());
					blocks.put(js);
				}
			}
		}

		JSONObject json = new JSONObject();
		json.put("blocks", blocks);

		edit
			.customTagByte("selecting", (byte) 0)
			.customTagString("blocks", json.toString())
			.customTagInt("lx", Math.abs(x1 - x0))
			.customTagInt("ly", Math.abs(y1 - y0))
			.customTagInt("lz", Math.abs(z1 - z0))
			.buildItemStack();
		updateLore(context.getHandItem());
		e.setCancelled(true);
	}

	@Override
	public void leftClickBlock(ItemStack item, PlayerInteractEvent e)
	{
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) return;

		ItemStackBuilder edit = ItemStackBuilder.edit(item);

		if (edit.getCustomTagByte("selecting") == 0)
			return;

		edit
			.customTagInt("x0", clickedBlock.getX())
			.customTagInt("y0", clickedBlock.getY())
			.customTagInt("z0", clickedBlock.getZ())
			.buildItemStack();
		updateLore(item);
		e.setCancelled(true);
	}

	@Override
	public void rightClickBlock(ItemStack item, PlayerInteractEvent e)
	{
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) return;

		ItemStackBuilder edit = ItemStackBuilder.edit(item);

		if (edit.getCustomTagByte("selecting") == 1)
		{
			edit
				.customTagInt("x1", clickedBlock.getX())
				.customTagInt("y1", clickedBlock.getY())
				.customTagInt("z1", clickedBlock.getZ())
				.buildItemStack();
			updateLore(item);
			e.setCancelled(true);
		} else if (edit.getCustomTagByte("placing") == 1)
		{
			String s = edit.getCustomTagString("blocks");
			if (s == null)
			{
				e.getPlayer().sendMessage(ChatColor.RED + "No structure found!");
				return;
			}
			int lx = edit.getCustomTagInt("lx");
			int ly = edit.getCustomTagInt("ly");
			int lz = edit.getCustomTagInt("lz");
			if ((lx + 1) * (ly + 1) * (lz + 1) <= 0)
			{
				e.getPlayer().sendMessage(ChatColor.RED + "Structure does not contain any blocks!");
				return;
			}

			JSONObject json = new JSONObject(s);
			JSONArray blocks = json.getJSONArray("blocks");
			if (blocks.length() != (lx + 1) * (ly + 1) * (lz + 1))
			{
				e.getPlayer().sendMessage(ChatColor.RED + "Structure block count does not match with size! (" + (lx + 1) + "*" + (ly + 1) + "*" + (lz + 1) + ") " + blocks.length());
				return;
			}

			Location location = clickedBlock.getLocation().add(e.getBlockFace().getDirection());

			for (int i = 0; i < blocks.length(); i++)
			{
				JSONObject js = blocks.getJSONObject(i);
				BlockData blockData = Bukkit.createBlockData(js.getString("b"));
				location.clone().add(js.getInt("x"), js.getInt("y"), js.getInt("z")).getBlock().setBlockData(blockData);
			}

			e.getPlayer().setCooldown(Material.BOOK, 5);
		}
	}

	public record BlockInfo(BlockData data, Vector position) {}

	public static Vector getSize(ItemStack item)
	{
		if (Items.getCustomItemId(item) == null)
		{
			return new Vector();
		}

		ItemStackBuilder edit = ItemStackBuilder.edit(item);

		String s = edit.getCustomTagString("blocks");
		if (s == null)
		{
			return new Vector();
		}
		int lx = edit.getCustomTagInt("lx");
		int ly = edit.getCustomTagInt("ly");
		int lz = edit.getCustomTagInt("lz");

		return new Vector(lx, ly, lz);
	}

	public static List<BlockInfo> toBlocks(ItemStack item)
	{
		ArrayList<BlockInfo> list = new ArrayList<>();
		if (Items.getCustomItemId(item) == null)
		{
			return list;
		}

		ItemStackBuilder edit = ItemStackBuilder.edit(item);

		String s = edit.getCustomTagString("blocks");
		if (s == null)
		{
			return list;
		}
		int lx = edit.getCustomTagInt("lx");
		int ly = edit.getCustomTagInt("ly");
		int lz = edit.getCustomTagInt("lz");
		if ((lx + 1) * (ly + 1) * (lz + 1) <= 0)
		{
			return list;
		}

		JSONObject json = new JSONObject(s);
		JSONArray blocks = json.getJSONArray("blocks");
		if (blocks.length() != (lx + 1) * (ly + 1) * (lz + 1))
		{
			return list;
		}

		jsonToBlocks(list, blocks);

		return list;
	}

	public static List<BlockInfo> jsonToBlocks(ArrayList<BlockInfo> list, JSONArray blocks)
	{
		for (int i = 0; i < blocks.length(); i++)
		{
			JSONObject js = blocks.getJSONObject(i);
			BlockData blockData = Bukkit.createBlockData(js.getString("b"));
			BlockInfo blockThing = new BlockInfo(blockData, new Vector(js.getInt("x"), js.getInt("y"), js.getInt("z")));
			list.add(blockThing);
		}

		return list;
	}

	private void updateLore(ItemStack item)
	{
		ItemStackBuilder builder = ItemStackBuilder.edit(item).removeLore();

		String mode = "None";

		if (builder.getCustomTagByte("selecting") == 1) mode = "Selecting";
		if (builder.getCustomTagByte("saving") == 1) mode = "Saving";
		if (builder.getCustomTagByte("placing") == 1) mode = "Placing";

		builder.addLore(JSONMessage
			.create("Mode: ").color(ChatColor.GRAY)
			.then(mode).color(ChatColor.WHITE));

		if (builder.getCustomTagByte("selecting") == 1)
		{
			builder.addLore(JSONMessage
				.create("Start: ").color(ChatColor.GRAY)
				.then("" + builder.getCustomTagInt("x0")).color(ChatColor.RED)
				.then("/").color(ChatColor.WHITE)
				.then("" + builder.getCustomTagInt("y0")).color(ChatColor.GREEN)
				.then("/").color(ChatColor.WHITE)
				.then("" + builder.getCustomTagInt("z0")).color(ChatColor.BLUE));
			builder.addLore(JSONMessage
				.create("End: ").color(ChatColor.GRAY)
				.then("" + builder.getCustomTagInt("x1")).color(ChatColor.RED)
				.then("/").color(ChatColor.WHITE)
				.then("" + builder.getCustomTagInt("y1")).color(ChatColor.GREEN)
				.then("/").color(ChatColor.WHITE)
				.then("" + builder.getCustomTagInt("z1")).color(ChatColor.BLUE));
		} else
		{
			builder.addLore(JSONMessage
				.create("Size: ").color(ChatColor.GRAY)
				.then("" + builder.getCustomTagInt("lx") + "/" + builder.getCustomTagInt("ly") + "/" + builder.getCustomTagInt("lz")).color(ChatColor.WHITE));
		}
		builder.buildItemStack();
	}

	@Override
	public void tickInHand(PlayerContext context)
	{
		if (FunnyLib.getUptimeTicks() % 3 != 0) return;

		ItemStackBuilder edit = ItemStackBuilder.edit(context.getHandItem());

		if (edit.getCustomTagByte("selecting") == 1)
		{
			int x0 = edit.getCustomTagInt("x0");
			int y0 = edit.getCustomTagInt("y0");
			int z0 = edit.getCustomTagInt("z0");
			int x1 = edit.getCustomTagInt("x1");
			int y1 = edit.getCustomTagInt("y1");
			int z1 = edit.getCustomTagInt("z1");

			if (Math.abs(x0 - x1) > 64)
				return;
			if (Math.abs(y0 - y1) > 64)
				return;
			if (Math.abs(z0 - z1) > 64)
				return;

			ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, Math.min(x0, x1 + 1), Math.min(y0, y1 + 1), Math.min(z0, z1 + 1), Math.max(x0, x1 + 1), Math.max(y0, y1 + 1), Math.max(z0, z1 + 1), 0, 0.5, OPTIONS);
		} else if (edit.getCustomTagByte("placing") == 1)
		{
			RayTraceResult rayTraceResult = context.getPlayer().rayTraceBlocks(10);
			if (rayTraceResult == null || rayTraceResult.getHitBlock() == null || rayTraceResult.getHitBlockFace() == null)
				return;
			Location location = rayTraceResult.getHitBlock().getLocation().add(rayTraceResult.getHitBlockFace().getDirection());

			if ((edit.getCustomTagInt("lx") + 1) * (edit.getCustomTagInt("ly") + 1) * (edit.getCustomTagInt("lz") + 1) <= 0)
				return;

			int x0 = location.getBlockX();
			int y0 = location.getBlockY();
			int z0 = location.getBlockZ();
			int x1 = edit.getCustomTagInt("lx") + location.getBlockX();
			int y1 = edit.getCustomTagInt("ly") + location.getBlockY();
			int z1 = edit.getCustomTagInt("lz") + location.getBlockZ();

			ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, Math.min(x0, x1 + 1), Math.min(y0, y1 + 1), Math.min(z0, z1 + 1), Math.max(x0, x1 + 1), Math.max(y0, y1 + 1), Math.max(z0, z1 + 1), 0, 0.5, OPTIONS);
		}
	}

	@Override
	public Response swapHands(Player player, ItemStack customMainHand, ItemStack offHand)
	{
		ItemStackBuilder edit = ItemStackBuilder.edit(customMainHand);

		if (edit.getCustomTagByte("selecting") == 1)
		{
			edit.customTagByte("selecting", (byte) 0);
			edit.customTagByte("saving", (byte) 1);
			JSONMessage.create("Current Mode: ").then("Saving").color(ChatColor.GREEN).send(player);
		} else if (edit.getCustomTagByte("saving") == 1)
		{
			edit.customTagByte("saving", (byte) 0);
			edit.customTagByte("placing", (byte) 1);
			JSONMessage.create("Current Mode: ").then("Placing").color(ChatColor.GREEN).send(player);
		} else if (edit.getCustomTagByte("placing") == 1)
		{
			edit.customTagByte("placing", (byte) 0);
			edit.customTagByte("selecting", (byte) 1);
			JSONMessage.create("Current Mode: ").then("Selecting").color(ChatColor.GREEN).send(player);
		} else
		{
			edit.customTagByte("selecting", (byte) 0);
			edit.customTagByte("placing", (byte) 0);
			edit.customTagByte("saving", (byte) 1);
			JSONMessage.create("Current Mode: ").then("Saving").color(ChatColor.GREEN).send(player);
		}

		ItemStack item = edit.buildItemStack();
		updateLore(item);
		player.getInventory().setItem(EquipmentSlot.HAND, item);

		return Response.cancel();
	}

	@Override
	public String id()
	{
		return "structure";
	}

	@Override
	protected ItemStack item()
	{
		return ItemStackBuilder.create(Material.BOOK).setName("Structue", ChatColor.DARK_AQUA).customTagByte("selecting", (byte) 1).buildItemStack();
	}
}
