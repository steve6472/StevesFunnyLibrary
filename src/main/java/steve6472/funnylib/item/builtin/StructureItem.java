package steve6472.funnylib.item.builtin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.joml.Vector3f;
import org.joml.Vector3i;
import steve6472.funnylib.CancellableResult;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.context.PlayerBlockContext;
import steve6472.funnylib.context.PlayerItemContext;
import steve6472.funnylib.context.UseType;
import steve6472.funnylib.data.BlockInfo;
import steve6472.funnylib.entity.BlockStructureEntity;
import steve6472.funnylib.entity.FrameDisplayEntity;
import steve6472.funnylib.entity.MultiDisplayEntity;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.item.events.SwapHandEvent;
import steve6472.funnylib.item.events.TickInHandEvent;
import steve6472.funnylib.data.GameStructure;
import steve6472.funnylib.serialize.ItemNBT;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.util.*;

/**
 * Created by steve6472
 * Date: 9/20/2022
 * Project: StevesFunnyLibrary <br>
 */
public class StructureItem extends CustomItem implements TickInHandEvent, SwapHandEvent
{
	public static final String KEY = "block_states";

	private static final double RAY_DISTANCE = 64;

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

		if (mode == Mode.LOADING)
		{
			GameStructure structure = GameStructure.structureFromNBT(itemData.getCompound(KEY));

			RayTraceResult rayTraceResult = context.getPlayer().rayTraceBlocks(RAY_DISTANCE);
			if (rayTraceResult != null && rayTraceResult.getHitBlock() != null && rayTraceResult.getHitBlockFace() != null)
			{
				paste(rayTraceResult.getHitBlock(), rayTraceResult.getHitBlockFace(), structure, context.getPlayer());
			}
			return;
		}

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
				GameStructure structure = GameStructure.structureFromNBT(itemData.getCompound(KEY));
				paste(clickedBlock, context.getFace(), structure, context.getPlayer());
			}
		}
	}

	private void paste(Block clickedBlock, BlockFace face, GameStructure structure, Player player)
	{
		Location location = clickedBlock.getLocation().add(face.getDirection());

		BlockInfo[] blockStates = structure.getBlocks();
		Vector3i size = structure.getSize();

		for (BlockInfo blockInfo : blockStates)
		{
			location
				.clone()
				.add(blockInfo.position().x() - ((size.x + 1) / 2f) + ((size.x + 1) / 2f) % 1, blockInfo.position().y(), blockInfo.position().z() - ((size.z + 1) / 2f) + ((size.z + 1) / 2f) % 1)
				.getBlock()
				.setBlockData(blockInfo.data());
		}

		player.setCooldown(Material.BOOK, 5);
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
					.then(size.x + "/" + size.y + "/" + size.z, ChatColor.WHITE)
					.setItalic(JSONMessage.ItalicType.FALSE));
			}
		}
		builder.buildItemStack();
	}

	@Override
	public void tickInHand(PlayerItemContext context)
	{
		if (context.getHand() != EquipmentSlot.HAND) return;

		NBT itemData = context.getItemData();
		Mode currentMode = itemData.getEnum(Mode.class, "mode");

		if (currentMode == Mode.SELECTING)
		{
			renderSelecting(context, itemData);
		} else if (currentMode == Mode.LOADING)
		{
			renderPreview(context, itemData);
		}
	}

	private void renderSelecting(PlayerItemContext context, NBT itemData)
	{
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

		Location location = new Location(context.getWorld(), x0, y0, z0);
		Vector3f size = new Vector3f(x1 - x0, y1 - y0, z1 - z0);

		FrameDisplayEntity structureFrame = FunnyLib
			.getPlayerboundEntityManager()
			.getOrCreateMultiEntity(context.getPlayer(), nbt -> nbt.getBoolean("structure_frame_select", false), () ->
			{
				FrameDisplayEntity frameDisplayEntity = new FrameDisplayEntity(
					context.getPlayer(),
					new Location(location.getWorld(), x0 + (size.x) / 2f, y0 + (size.y) / 2f, z0 + (size.z) / 2f),
					FrameDisplayEntity.FrameType.MEDIUMAQUA_MARINE,
					1f / 16f);
				frameDisplayEntity.setAliveCondition(context.getPlayer(), MultiDisplayEntity.holdingCustomItemWithNBTCondition(context.getPlayer(), FunnyLib.STRUCTURE, itemNBT -> itemNBT.getEnum(Mode.class, "mode") == Mode.SELECTING));
				frameDisplayEntity.getEntityPDC().setBoolean("structure_frame_select", true);
				return frameDisplayEntity;
			});

		structureFrame.setScale((size.x) / 2f, (size.y) / 2f, (size.z) / 2f);
		structureFrame.setRadius((float) (Math.sin(Math.toRadians(FunnyLib.getUptimeTicks() % 3600)) * 0.25 + 1.5) * (1f / 16f));
		structureFrame.move(x0 + (size.x) / 2f, y0 + (size.y) / 2f, z0 + (size.z) / 2f);
	}

	private void renderPreview(PlayerItemContext context, NBT itemData)
	{
		RayTraceResult rayTraceResult = context.getPlayer().rayTraceBlocks(RAY_DISTANCE);
		if (rayTraceResult == null || rayTraceResult.getHitBlock() == null || rayTraceResult.getHitBlockFace() == null)
		{
			FunnyLib.getPlayerboundEntityManager().removeMultiEntity(context.getPlayer(), nbt -> nbt.getBoolean("structure_frame", false));
			FunnyLib.getPlayerboundEntityManager().removeMultiEntity(context.getPlayer(), nbt -> nbt.getBoolean("structure_preview", false));
			return;
		}

		Location location = rayTraceResult.getHitBlock().getLocation().add(rayTraceResult.getHitBlockFace().getDirection());

		GameStructure structure = GameStructure.structureFromNBT(itemData.getCompound(KEY));

		// Frame
		Vector3i size = structure.getSize();
		if ((size.x + 1) * (size.y + 1) * (size.z + 1) <= 0)
			return;

		FrameDisplayEntity structureFrame = FunnyLib
			.getPlayerboundEntityManager()
			.getOrCreateMultiEntity(context.getPlayer(), nbt -> nbt.getBoolean("structure_frame", false), () ->
			{
				FrameDisplayEntity frameDisplayEntity = new FrameDisplayEntity(
					context.getPlayer(),
					new Location(location.getWorld(), location.getX() + ((size.x + 1) / 2f) % 1f, location.getY() + (size.y + 1) / 2f, location.getZ() + ((size.z + 1) / 2f) % 1f),
					FrameDisplayEntity.FrameType.UGLY_PURPLE,
					1f / 16f);
				frameDisplayEntity.setAliveCondition(context.getPlayer(), MultiDisplayEntity.holdingCustomItemWithNBTCondition(context.getPlayer(), FunnyLib.STRUCTURE, itemNBT -> itemNBT.getEnum(Mode.class, "mode") == Mode.LOADING));
				frameDisplayEntity.getEntityPDC().setBoolean("structure_frame", true);
				return frameDisplayEntity;
			});

		structureFrame.setScale((size.x + 1) / 2f, (size.y + 1) / 2f, (size.z + 1) / 2f);
		structureFrame.setRadius((float) (Math.sin(Math.toRadians(FunnyLib.getUptimeTicks() % 3600)) * 0.25 + 1.5) * (1f / 16f));
		structureFrame.move(location.getX() + ((size.x + 1) / 2f) % 1f, location.getY() + (size.y + 1) / 2f, location.getZ() + ((size.z + 1) / 2f) % 1f);

		BlockStructureEntity blockStructure = FunnyLib
			.getPlayerboundEntityManager()
			.getOrCreateMultiEntity(context.getPlayer(), nbt -> nbt.getBoolean("structure_preview", false), () ->
			{
				BlockStructureEntity blockStructureEntity = BlockStructureEntity.createFromStructure(
					new Location(location.getWorld(), location.getX() + ((size.x + 1) / 2f) % 1f, location.getY() + (size.y + 1) / 2f, location.getZ() + ((size.z + 1) / 2f) % 1f),
					structure);

				blockStructureEntity.setAliveCondition(context.getPlayer(), MultiDisplayEntity.holdingCustomItemWithNBTCondition(context.getPlayer(), FunnyLib.STRUCTURE, itemNBT -> itemNBT.getEnum(Mode.class, "mode") == Mode.LOADING));
				blockStructureEntity.getEntityPDC().setBoolean("structure_preview", true);
				return blockStructureEntity;
			});

		blockStructure.move(location.getX() + ((size.x + 1) / 2f) % 1f, location.getY() + (size.y + 1) / 2f, location.getZ() + ((size.z + 1) / 2f) % 1f);
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
