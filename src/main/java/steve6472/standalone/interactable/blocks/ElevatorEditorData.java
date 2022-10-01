package steve6472.standalone.interactable.blocks;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.item.builtin.StructureItem;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveDouble;
import steve6472.funnylib.json.codec.codecs.ItemStackCodec;
import steve6472.funnylib.util.GlowingUtil;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MiscUtil;
import steve6472.standalone.interactable.Interactable;
import steve6472.standalone.interactable.ReflectionHacker;
import steve6472.standalone.interactable.worldbutton.WorldButton;
import steve6472.standalone.interactable.worldbutton.WorldButtonBuilder;

import java.util.ArrayList;
import java.util.List;

public class ElevatorEditorData extends CustomBlockData
{
	private static final WorldButtonBuilder builder = WorldButton
		.builder()
		.activeColor(ChatColor.WHITE)
		.disabledColor(ChatColor.BLACK)
		.remote(true)
		.glowAlways(true)
		.labelSubtitle()
		.size(1);

	@Save(type = ItemStackCodec.class)
	public ItemStack structure = MiscUtil.AIR;

	public WorldButton addCollision, removeCurrentCollision, saveCollision;
	public WorldButton forward, back, up, down, left, right;

	public ArmorStand editingCollision;
	public Shulker editingCollision_;
	private final List<Vector> offsets = new ArrayList<>();
	private final List<ArmorStand> collisions = new ArrayList<>();

	@SaveDouble
	public double offsetX, offsetY, offsetZ;

	private static ItemStack horseIcon(int color, int data)
	{
		return ItemStackBuilder
			.create(Material.LEATHER_HORSE_ARMOR)
			.setArmorColor(color)
			.setCustomModelData(data)
			.buildItemStack();
	}

	@Override
	public void onPlace(BlockContext context)
	{
		loadButtons(context.getLocation());
	}

	@Override
	public void onRemove(BlockContext context)
	{
		clearButtons();
		removeEditingStuff();
		clearCollisions();
	}

	@Override
	public void load(JSONObject json)
	{
		loadButtons(pos);
		if (json.getBoolean("editing"))
		{
			createEditingStuff(pos);
		}

		this.offsets.clear();
		JSONArray offsets = json.optJSONArray("offsets");
		for (int i = 0; i < offsets.length(); i++)
		{
			JSONObject off = offsets.getJSONObject(i);
			double x = off.getDouble("x");
			double y = off.getDouble("y");
			double z = off.getDouble("z");
			this.offsets.add(new Vector(x, y, z));
		}
		createCollisions(pos);
	}

	@Override
	public void save(JSONObject json, boolean unloading)
	{
		json.put("editing", editingCollision != null);

		if (unloading)
		{
			clearButtons();
			removeEditingStuff();
			clearCollisions();
		}

		JSONArray offsetArray = new JSONArray();
		for (Vector offset : offsets)
		{
			JSONObject off = new JSONObject();
			off.put("x", offset.getX());
			off.put("y", offset.getY());
			off.put("z", offset.getZ());
			offsetArray.put(off);
		}
		json.put("offsets", offsetArray);
	}

	private void moveEditing(double dx, double dy, double dz)
	{
		offsetX += dx;
		offsetY += dy;
		offsetZ += dz;

		double x = editingCollision.getLocation().getX() + dx;
		double y = editingCollision.getLocation().getY() + dy;
		double z = editingCollision.getLocation().getZ() + dz;
		ReflectionHacker.callEntityMoveTo(editingCollision, x, y, z, editingCollision
			.getLocation()
			.getYaw(), editingCollision.getLocation().getPitch());

		y += 0.5;

		up.teleport(x, y + 1, z);
		down.teleport(x, y - 1, z);
		left.teleport(x + 1, y, z);
		right.teleport(x - 1, y, z);
		forward.teleport(x, y, z + 1);
		back.teleport(x, y, z - 1);
	}

	private void createEditingStuff(Location location)
	{
		if (editingCollision != null) return;

		World world = location.getWorld();
		if (world == null)
			return;

		editingCollision = world.spawn(location.clone().add(1.5 + offsetX, 0 + offsetY, 2.5 + offsetZ), ArmorStand.class, as ->
		{
			as.setMarker(true);
			as.setGravity(false);
			as.setSmall(true);
			as.setInvisible(true);
			as.setInvulnerable(true);
			as.setGlowing(true);
			assert as.getEquipment() != null;
			as
				.getEquipment()
				.setHelmet(ItemStackBuilder
					.create(Material.COMMAND_BLOCK)
					.setCustomModelData(4)
					.buildItemStack());
			GlowingUtil.setGlowColor(as, ChatColor.GOLD);
		});

		editingCollision_ = world.spawn(location.clone().add(1.5 + offsetX, 0 + offsetY, 2.5 + offsetZ), Shulker.class, s ->
		{
			s.setAI(false);
			s.setInvulnerable(true);
			s.setSilent(true);
			s.setPersistent(true);
			s.setInvisible(true);
		});

		editingCollision.addPassenger(editingCollision_);

		double x = location.getX() + 1.5 + offsetX;
		double y = location.getY() + offsetY + 0.5;
		double z = location.getZ() + 2.5 + offsetZ;

		up = builder
			.icon(horseIcon(0x00cc00, 8))
			.label("Up")
			.clickAction(pc -> moveEditing(0, 1d / 16d, 0))
			.build(new Location(location.getWorld(), x, y + 1, z));

		down = builder
			.icon(horseIcon(0xcc00cc, 9))
			.label("Down")
			.clickAction(pc -> moveEditing(0, -1d / 16d, 0))
			.build(new Location(location.getWorld(), x, y - 1, z));

		left = builder
			.icon(horseIcon(0xcc0000, 10))
			.label("Left")
			.clickAction(pc -> moveEditing(1d / 16d, 0, 0))
			.build(new Location(location.getWorld(), x + 1, y, z));

		right = builder
			.icon(horseIcon(0x00cccc, 11))
			.label("Right")
			.clickAction(pc -> moveEditing(-1d / 16d, 0, 0))
			.build(new Location(location.getWorld(), x - 1, y, z));

		forward = builder
			.icon(horseIcon(0x0000cc, 12))
			.label("Forward")
			.clickAction(pc -> moveEditing(0, 0, 1d / 16d))
			.build(new Location(location.getWorld(), x, y, z + 1));

		back = builder
			.icon(horseIcon(0xcccc00, 13))
			.label("Back")
			.clickAction(pc -> moveEditing(0, 0, -1d / 16d))
			.build(new Location(location.getWorld(), x, y, z - 1));
	}

	public void loadButtons(Location location)
	{
		World world = location.getWorld();
		if (world == null)
			return;

		addCollision = WorldButton
			.builder()
			.label("Collision")
			.icon(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(3).buildItemStack())
			.clickAction(pc -> createEditingStuff(location))
			.build(location.clone().add(1.5, 1.3, 0.5));

		removeCurrentCollision = WorldButton
			.builder()
			.icon(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(5).buildItemStack())
			.label("Remove Collision")
			.clickAction(pc -> removeEditingStuff())
			.build(location.clone().add(1.5, 2.0, 0.5));

		saveCollision = WorldButton
			.builder()
			.label("Save Collision")
			.icon(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setArmorColor(0x77cc00).setCustomModelData(15).buildItemStack())
			.clickAction(pc ->
			{
				if (editingCollision == null) return;

				offsets.add(new Vector(offsetX, offsetY, offsetZ));
				reloadCollisions(location);
				removeEditingStuff();
			})
			.build(location.clone().add(2.5, 1.3, 0.5));
	}

	public void clearCollisions()
	{
		for (ArmorStand collision : collisions)
		{
			for (Entity passenger : collision.getPassengers())
			{
				passenger.remove();
			}
			collision.remove();
		}
	}

	private void createCollision(World world, Vector offset, Location editorLocation)
	{
		ArmorStand armorStand = world.spawn(editorLocation.clone().add(offset).add(1.5, 0.0, 2.5), ArmorStand.class, as ->
		{
			as.setMarker(true);
			as.setGravity(false);
			as.setSmall(true);
			as.setInvisible(true);
			as.setInvulnerable(true);
			as.setGlowing(true);
		});

		Shulker shulker = world.spawn(editorLocation.clone().add(offset).add(1.5, 0.0, 2.5), Shulker.class, s ->
		{
			s.setAI(false);
			s.setInvulnerable(true);
			s.setSilent(true);
			s.setPersistent(true);
			s.setInvisible(true);
		});

		armorStand.addPassenger(shulker);
		collisions.add(armorStand);
	}

	public void createCollisions(Location editorLocation)
	{
		World world = editorLocation.getWorld();
		if (world == null)
			throw new NullPointerException("World is null!");

		for (Vector offset : offsets)
		{
			createCollision(world, offset, editorLocation);
		}
	}

	public void reloadCollisions(Location editorLocation)
	{
		clearCollisions();
		createCollisions(editorLocation);
	}

	public void clearButtons()
	{
		removeCurrentCollision.remove();
		saveCollision.remove();
		addCollision.remove();
	}

	private void removeEditingStuff()
	{
		if (editingCollision == null) return;

		editingCollision.remove();
		editingCollision_.remove();
		editingCollision = null;
		editingCollision_ = null;

		forward.remove();
		back.remove();
		up.remove();
		down.remove();
		left.remove();
		right.remove();

		offsetX = 0;
		offsetY = 0;
		offsetZ = 0;
	}

	public void loadStructure(Location location, @Nullable Player activator)
	{
		List<StructureItem.BlockInfo> blockInfos = StructureItem.toBlocks(structure);
		for (StructureItem.BlockInfo blockInfo : blockInfos)
		{
			location.clone().add(1, 0, 2).add(blockInfo.position()).getBlock().setBlockData(blockInfo.data());
		}
		if (activator != null)
		{
			activator.sendMessage(ChatColor.GREEN + "Loaded!");
		}
	}

	public boolean canEdit()
	{
		return Items.getCustomItem(structure) == FunnyLib.STRUCTURE;
	}

	public JSONObject createElevatorData()
	{
		if (Items.getCustomItem(structure) != FunnyLib.STRUCTURE) return null;

		ItemStackBuilder edit = ItemStackBuilder.edit(structure);

		String s = edit.getCustomTagString("blocks");
		if (s == null)
		{
			return null;
		}
		int lx = edit.getCustomTagInt("lx");
		int ly = edit.getCustomTagInt("ly");
		int lz = edit.getCustomTagInt("lz");

		JSONObject json = new JSONObject();

		JSONObject struct = new JSONObject();
		struct.put("blocks", new JSONObject(s));
		struct.put("lx", lx);
		struct.put("ly", ly);
		struct.put("lz", lz);
		json.put("structure", struct);


		JSONArray offsetArray = new JSONArray();
		for (Vector offset : offsets)
		{
			JSONObject off = new JSONObject();
			off.put("x", offset.getX());
			off.put("y", offset.getY());
			off.put("z", offset.getZ());
			offsetArray.put(off);
		}
		json.put("collisions", offsetArray);

		return json;
	}

	public ItemStack createElevatorDataItem()
	{
		JSONObject data = createElevatorData();
		if (data == null)
		{
			return null;
		}
		ItemStack elevatorData = Interactable.ELEVATOR_DATA_ITEM.newItemStack();
		ItemStackBuilder edit = ItemStackBuilder.editNonStatic(elevatorData);
		edit.customTagString("data", data.toString());
		return edit.buildItemStack();
	}
}