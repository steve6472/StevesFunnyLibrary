package steve6472.funnylib.item.builtin;

import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.events.SwapHandEvent;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.ParticleUtil;

import java.util.*;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class StructureItem extends CustomItem implements TickInHandEvent, SwapHandEvent
{
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.AQUA, 0.75f);

	@Override
	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result)
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

		edit
			.customTagByte("selecting", (byte) 0)
			.customTagJsonArray("blocks", blocks)
			.customTagInt("lx", Math.abs(x1 - x0))
			.customTagInt("ly", Math.abs(y1 - y0))
			.customTagInt("lz", Math.abs(z1 - z0))
			.buildItemStack();
		updateLore(context.getHandItem());
		result.setCancelled(true);
		context.getPlayer().sendMessage(ChatColor.GREEN + "Saved!");
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		Block clickedBlock = context.getBlock();
		if (useType == UseType.LEFT)
		{
			ItemStackBuilder edit = ItemStackBuilder.edit(context.getHandItem());

			if (edit.getCustomTagByte("selecting") == 0)
				return;

			edit
				.customTagInt("x0", clickedBlock.getX())
				.customTagInt("y0", clickedBlock.getY())
				.customTagInt("z0", clickedBlock.getZ())
				.buildItemStack();
			updateLore(context.getHandItem());
			result.setCancelled(true);
		} else
		{
			ItemStackBuilder edit = ItemStackBuilder.edit(context.getHandItem());

			if (edit.getCustomTagByte("selecting") == 1)
			{
				edit
					.customTagInt("x1", clickedBlock.getX())
					.customTagInt("y1", clickedBlock.getY())
					.customTagInt("z1", clickedBlock.getZ())
					.buildItemStack();
				updateLore(context.getHandItem());
				result.setCancelled(true);
			} else if (edit.getCustomTagByte("placing") == 1)
			{
				JSONArray blocks = edit.getCustomJsonArray("blocks");
				if (blocks == null)
				{
					context.getPlayer().sendMessage(ChatColor.RED + "No structure found!");
					return;
				}
				int lx = edit.getCustomTagInt("lx");
				int ly = edit.getCustomTagInt("ly");
				int lz = edit.getCustomTagInt("lz");
				if ((lx + 1) * (ly + 1) * (lz + 1) <= 0)
				{
					context.getPlayer().sendMessage(ChatColor.RED + "Structure does not contain any blocks!");
					return;
				}

				if (blocks.length() != (lx + 1) * (ly + 1) * (lz + 1))
				{
					context.getPlayer().sendMessage(ChatColor.RED + "Structure block count does not match with size! (" + (lx + 1) + "*" + (ly + 1) + "*" + (lz + 1) + ") " + blocks.length());
					return;
				}

				Location location = clickedBlock.getLocation().add(context.getFace().getDirection());

				for (int i = 0; i < blocks.length(); i++)
				{
					JSONObject js = blocks.getJSONObject(i);
					BlockData blockData = Bukkit.createBlockData(js.getString("b"));
					location.clone().add(js.getInt("x"), js.getInt("y"), js.getInt("z")).getBlock().setBlockData(blockData);
				}

				context.getPlayer().setCooldown(Material.BOOK, 5);
			}
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

		JSONArray s = edit.getCustomJsonArray("blocks");
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

		JSONArray blocks = edit.getCustomJsonArray("blocks");
		if (blocks == null)
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
			.then(mode).color(ChatColor.WHITE)
			.setItalic(JSONMessage.ItalicType.FALSE));

		if (builder.getCustomTagByte("selecting") == 1)
		{
			builder.addLore(JSONMessage
				.create("Start: ").color(ChatColor.GRAY)
				.then("" + builder.getCustomTagInt("x0")).color(ChatColor.RED)
				.then("/").color(ChatColor.WHITE)
				.then("" + builder.getCustomTagInt("y0")).color(ChatColor.GREEN)
				.then("/").color(ChatColor.WHITE)
				.then("" + builder.getCustomTagInt("z0")).color(ChatColor.BLUE)
				.setItalic(JSONMessage.ItalicType.FALSE));
			builder.addLore(JSONMessage
				.create("End: ").color(ChatColor.GRAY)
				.then("" + builder.getCustomTagInt("x1")).color(ChatColor.RED)
				.then("/").color(ChatColor.WHITE)
				.then("" + builder.getCustomTagInt("y1")).color(ChatColor.GREEN)
				.then("/").color(ChatColor.WHITE)
				.then("" + builder.getCustomTagInt("z1")).color(ChatColor.BLUE)
				.setItalic(JSONMessage.ItalicType.FALSE));
		} else
		{
			builder.addLore(JSONMessage
				.create("Size: ").color(ChatColor.GRAY)
				.then("" + builder.getCustomTagInt("lx") + "/" + builder.getCustomTagInt("ly") + "/" + builder.getCustomTagInt("lz")).color(ChatColor.WHITE)
				.setItalic(JSONMessage.ItalicType.FALSE));
		}
		builder.buildItemStack();
	}

	private static final HashMap<Player, Collection<BlockDisplay>> GHOST_PREVIEW = new HashMap<>();
	private static final HashMap<Player, Triple<Vector3i, Vector3i, Vector2i>> LAST_LOC = new HashMap<>();

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (FunnyLib.getUptimeTicks() % 3 != 0) return;
		if (context.getHand() != EquipmentSlot.HAND) return;

		ItemStackBuilder edit = ItemStackBuilder.edit(context.getHandItem());

		if (edit.getCustomTagByte("placing") != 1)
		{
			Collection<BlockDisplay> displayList = GHOST_PREVIEW.get(context.getPlayer());
			if (displayList != null)
			{
				displayList.forEach(Entity::remove);
				displayList.clear();
				GHOST_PREVIEW.remove(context.getPlayer());
			}
			LAST_LOC.remove(context.getPlayer());
		}

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
			{
				Collection<BlockDisplay> displayList = GHOST_PREVIEW.get(context.getPlayer());
				if (displayList != null)
				{
					displayList.forEach(Entity::remove);
					displayList.clear();
					GHOST_PREVIEW.remove(context.getPlayer());
				}
				LAST_LOC.remove(context.getPlayer());
				return;
			}

			Location location = rayTraceResult.getHitBlock().getLocation().add(rayTraceResult.getHitBlockFace().getDirection());

			// Pair of starting-location and last-location
			Triple<Vector3i, Vector3i, Vector2i> locationPair = LAST_LOC.computeIfAbsent(context.getPlayer(),
				p -> Triple.of(
					new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
					new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
					new Vector2i()));

			if ((edit.getCustomTagInt("lx") + 1) * (edit.getCustomTagInt("ly") + 1) * (edit.getCustomTagInt("lz") + 1) <= 0)
				return;

			int x0 = location.getBlockX();
			int y0 = location.getBlockY();
			int z0 = location.getBlockZ();
			int x1 = edit.getCustomTagInt("lx") + location.getBlockX();
			int y1 = edit.getCustomTagInt("ly") + location.getBlockY();
			int z1 = edit.getCustomTagInt("lz") + location.getBlockZ();

			ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, Math.min(x0, x1 + 1), Math.min(y0, y1 + 1), Math.min(z0, z1 + 1), Math.max(x0, x1 + 1), Math.max(y0, y1 + 1), Math.max(z0, z1 + 1), 0, 0.5, OPTIONS);

			/*
			 * Real fancy preview
			 */

			List<BlockInfo> blockInfos = toBlocks(context.getHandItem());

			final boolean[] wasAbsent = {false};

			Collection<BlockDisplay> displayList = GHOST_PREVIEW.computeIfAbsent(context.getPlayer(), k ->
			{
				LinkedList<BlockDisplay> blockDisplays = new LinkedList<>();
				for (BlockInfo blockInfo : blockInfos)
				{
					// Ignore air blocks
					if (blockInfo.data.getMaterial().isAir())
						continue;

					BlockDisplay blockDisplay = context.getWorld().spawn(location.clone().add(blockInfo.position), BlockDisplay.class, bd ->
					{
						bd.setBlock(blockInfo.data);
						bd.setTransformation(new Transformation(new Vector3f(0.5f, 0.5f, 0.5f), new Quaternionf(), new Vector3f(0, 0, 0), new Quaternionf()));
					});
					blockDisplays.add(blockDisplay);
				}
				wasAbsent[0] = true;
				return blockDisplays;
			});

			if (!wasAbsent[0] && locationPair.getRight().x == 0)
			{
				locationPair.getRight().x = 1;
				displayList.forEach(bd ->
				{
					bd.setInterpolationDuration(5);
					bd.setInterpolationDelay(0);
					bd.setTransformation(new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1), new Quaternionf()));
				});
			}

			if (!locationPair.getMiddle().equals(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
			{
				locationPair.getMiddle().set(location.getBlockX(), location.getBlockY(), location.getBlockZ());

				int offsetX = location.getBlockX() - locationPair.getLeft().x;
				int offsetY = location.getBlockY() - locationPair.getLeft().y;
				int offsetZ = location.getBlockZ() - locationPair.getLeft().z;

				displayList.forEach(bd ->
				{
					bd.setTransformation(new Transformation(new Vector3f(offsetX, offsetY, offsetZ), new Quaternionf(), new Vector3f(1, 1, 1), new Quaternionf()));
					bd.setInterpolationDuration(5);
					bd.setInterpolationDelay(0);
				});
			}
		}
	}

	@Override
	public void swapHands(Player player, ItemStack customMainHand, ItemStack offHand, CancellableResult result)
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

		result.cancel();
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
