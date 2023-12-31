package steve6472.funnylib.item.builtin;

import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.data.BlockInfo;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.SwapHandEvent;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.*;

import java.util.*;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class StructureItem extends CustomItem implements TickInHandEvent, SwapHandEvent
{
	private static final Particle.DustOptions OPTIONS = new Particle.DustOptions(Color.AQUA, 0.75f);
	public static final String KEY = "block_states";

	public enum Mode
	{
		SELECTING(JSONMessage.create("Selecting").color(ChatColor.GREEN)),
		SAVING(JSONMessage.create("Saving").color(ChatColor.GREEN)),
		LOADING(JSONMessage.create("Loading").color(ChatColor.GREEN)),
		NONE(JSONMessage.create("None").color(ChatColor.GRAY));

		private final JSONMessage name;

		Mode(JSONMessage name)
		{
			this.name = name;
		}

		public JSONMessage getName()
		{
			return name;
		}

		// Create a private copy of array to avoid copying the array each time next() is called
		private static final Mode[] VALS = values();

		public Mode next()
		{
			return VALS[(this.ordinal() + 1) % VALS.length];
		}

		public Mode previous()
		{
			return VALS[Math.floorMod((this.ordinal() - 1), VALS.length)];
		}
	}

	@Override
	public void useOnAir(PlayerItemContext context, UseType useType, CancellableResult result)
	{
		ItemNBT itemData = context.getItemData();
		Mode mode = itemData.getEnum(Mode.class, "mode");

		if (mode != Mode.SAVING) return;

		Vector3i start = itemData.get3i("start");
		Vector3i end = itemData.get3i("end");

		NBT compound = itemData.createCompound();
		GameStructure
			.fromWorld(context.getWorld(), start.x, start.y, start.z, end.x, end.y, end.z)
			.toNBT(compound);
		compound.remove("start");
		compound.remove("end");
		itemData.setCompound(KEY, compound);

		updateLore(itemData);
		result.setCancelled(true);
		context.getPlayer().sendMessage(ChatColor.GREEN + "Saved!");
	}

	@Override
	public void useOnBlock(PlayerBlockContext context, UseType useType, CancellableResult result)
	{
		ItemNBT itemData = context.getItemData();
		Mode currentMode = itemData.getEnum(Mode.class, "mode");

		Block clickedBlock = context.getBlock();
		if (useType == UseType.LEFT)
		{
			if (currentMode != Mode.SELECTING)
				return;

			itemData.set3i("start", clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ());
			AreaMarkerItem.fixCoordinates(itemData, "start", "end");
			updateLore(itemData);
			result.setCancelled(true);
		} else
		{
			if (currentMode == Mode.SELECTING)
			{
				itemData.set3i("end", clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ());
				AreaMarkerItem.fixCoordinates(itemData, "start", "end");
				updateLore(itemData);
				result.setCancelled(true);
			} else if (currentMode == Mode.LOADING)
			{
				Location location = clickedBlock.getLocation().add(context.getFace().getDirection());

				GameStructure structure = GameStructure.structureFromNBT(itemData.getCompound(KEY));
				BlockInfo[] blockStates = structure.getBlocks();

				for (BlockInfo blockInfo : blockStates)
				{
					location
						.clone()
						.add(blockInfo.position().x(), blockInfo.position().y(), blockInfo.position().z())
						.getBlock()
						.setBlockData(blockInfo.data());
				}

				context.getPlayer().setCooldown(Material.BOOK, 5);
			}
		}
	}

	private static void updateLore(ItemNBT itemData)
	{
		ItemStackBuilder builder = ItemStackBuilder.edit(itemData).removeLore();
		Mode currentMode = builder.nbt().getEnum(Mode.class, "mode");

		if (builder.hasString("name"))
			builder
				.addLore(
					JSONMessage.create("Name: ").color(ChatColor.DARK_GRAY)
						.then(builder.getString("name")).color(ChatColor.WHITE)
						.setItalic(JSONMessage.ItalicType.FALSE)
				);

		builder.addLore(JSONMessage
			.create("Mode: ").color(ChatColor.DARK_GRAY)
			.then(currentMode.getName())
			.setItalic(JSONMessage.ItalicType.FALSE));

		if (currentMode == Mode.SELECTING)
		{
			if (itemData.has3i("start"))
			{
				Vector3i start = itemData.get3i("start");
				builder.addLore(JSONMessage
					.create("Start: ").color(ChatColor.DARK_GRAY)
					.then("" + start.x, ChatColor.RED)
					.then("/", ChatColor.WHITE)
					.then("" + start.y, ChatColor.GREEN)
					.then("/", ChatColor.WHITE)
					.then("" + start.z, ChatColor.BLUE)
					.setItalic(JSONMessage.ItalicType.FALSE));
			}
			if (itemData.has3i("end"))
			{
				Vector3i end = itemData.get3i("end");
				builder.addLore(JSONMessage
					.create("End: ").color(ChatColor.DARK_GRAY)
					.then("" + end.x, ChatColor.RED)
					.then("/", ChatColor.WHITE)
					.then("" + end.y, ChatColor.GREEN)
					.then("/", ChatColor.WHITE)
					.then("" + end.z, ChatColor.BLUE)
					.setItalic(JSONMessage.ItalicType.FALSE));
			}
		} else
		{
			if (itemData.hasCompound(KEY))
			{
				Vector3i size = itemData.getCompound(KEY).get3i("size");
				builder.addLore(JSONMessage
					.create("Size: ").color(ChatColor.DARK_GRAY)
					.then("" + size.x + "/" + size.y + "/" + size.z, ChatColor.WHITE)
					.setItalic(JSONMessage.ItalicType.FALSE));
			}
		}
		builder.buildItemStack();
	}

	private static final HashMap<Player, Collection<BlockDisplay>> GHOST_PREVIEW = new HashMap<>();
	private static final HashMap<Player, Triple<Vector3i, Vector3i, Vector2i>> LAST_LOC = new HashMap<>();

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (context.getHand() != EquipmentSlot.HAND) return;

		NBT itemData = context.getItemData();
		Mode currentMode = itemData.getEnum(Mode.class, "mode");

		if (currentMode != Mode.LOADING)
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

		if (currentMode == Mode.SELECTING)
		{
			if (FunnyLib.getUptimeTicks() % 3 != 0) return;

			if (!itemData.has3i("start") || !itemData.has3i("end"))
				return;

			Vector3i start = itemData.get3i("start");
			Vector3i end = itemData.get3i("end");

			int x0 = start.x;
			int y0 = start.y;
			int z0 = start.z;
			int x1 = end.x + 1;
			int y1 = end.y + 1;
			int z1 = end.z + 1;

			ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, x0, y0, z0, x1, y1, z1, 0, 0.5, OPTIONS);
		} else if (currentMode == Mode.LOADING)
		{
			renderPreview(context);
		}
	}

	private void renderPreview(PlayerItemContext context)
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

		NBT itemData = context.getItemData();
		Location location = rayTraceResult.getHitBlock().getLocation().add(rayTraceResult.getHitBlockFace().getDirection());

		GameStructure structure = GameStructure.structureFromNBT(itemData.getCompound(KEY));

		if (FunnyLib.getUptimeTicks() % 3 == 0)
		{
			Vector3i size = structure.getSize();
			if ((size.x + 1) * (size.y + 1) * (size.z + 1) <= 0)
				return;

			int x0 = location.getBlockX();
			int y0 = location.getBlockY();
			int z0 = location.getBlockZ();
			int x1 = size.x + location.getBlockX() + 1;
			int y1 = size.y + location.getBlockY() + 1;
			int z1 = size.z + location.getBlockZ() + 1;

			ParticleUtil.boxAbsolute(context.getPlayer(), Particle.REDSTONE, x0, y0, z0, x1, y1, z1, 0, 0.5, OPTIONS);
		}

		renderFancyPreview(context, structure, location);
	}

	private void renderFancyPreview(PlayerItemContext context, GameStructure structure, Location location)
	{
		// Pair of starting-location and last-location
		Triple<Vector3i, Vector3i, Vector2i> locationPair = LAST_LOC.computeIfAbsent(context.getPlayer(),
			p -> Triple.of(
				new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
				new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
				new Vector2i()));

		BlockInfo[] blockInfos = structure.getBlocks();

		final boolean[] wasAbsent = {false};

		Collection<BlockDisplay> displayList = GHOST_PREVIEW.computeIfAbsent(context.getPlayer(), k ->
		{
			LinkedList<BlockDisplay> blockDisplays = new LinkedList<>();
			for (BlockInfo blockInfo : blockInfos)
			{
				// Ignore air blocks
				if (blockInfo.data().getMaterial().isAir())
					continue;

				BlockDisplay blockDisplay = context.getWorld().spawn(location.clone().add(blockInfo.position().x, blockInfo.position().y, blockInfo.position().z), BlockDisplay.class, bd ->
				{
					bd.setBlock(blockInfo.data());
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

	@Override
	public void swapHands(Player player, ItemStack customMainHand, ItemStack offHand, CancellableResult result)
	{
		ItemStackBuilder edit = ItemStackBuilder.edit(customMainHand);
		Mode currentMode = edit.nbt().getEnum(Mode.class, "mode");

		JSONMessage.create("Current Mode: ").then(currentMode.next().getName()).send(player);
		edit.nbt().setEnum("mode", currentMode.next());

		updateLore(edit.nbt());

		// 'cause reasons
		ItemStack item = edit.buildItemStack();
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
		ItemStackBuilder structue = ItemStackBuilder.create(Material.BOOK).setName("Structue", ChatColor.DARK_AQUA);
		structue.nbt().setEnum("mode", Mode.NONE);
		return structue.buildItemStack();
	}

	public static ItemStack newStructureItem(GameStructure structure, String name, Material icon)
	{
		ItemNBT data = ItemNBT.create(FunnyLib.STRUCTURE.newItemStack());
		NBT structureNBT = data.createCompound();
		structure.toNBT(structureNBT);
		data.set3i("start", GameStructure.startFromCompound(structureNBT));
		data.set3i("end", GameStructure.endFromCompound(structureNBT));
		structureNBT.remove("start");
		structureNBT.remove("end");
		data.setCompound(KEY, structureNBT);

		data.setString("icon", icon.name());
		if (name != null)
			data.setString("name", name);

		updateLore(data);
		data.save();
		return data.getItemStack();
	}
}
