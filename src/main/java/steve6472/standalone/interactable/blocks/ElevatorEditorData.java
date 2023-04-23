package steve6472.standalone.interactable.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.item.Items;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveBool;
import steve6472.funnylib.json.codec.codecs.ItemStackCodec;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MiscUtil;
import steve6472.standalone.interactable.Interactable;
import steve6472.standalone.interactable.worldbutton.WorldBlockPositioner;
import steve6472.standalone.interactable.worldbutton.WorldButton;

import java.util.ArrayList;
import java.util.List;

public class ElevatorEditorData extends CustomBlockData
{
	@Save(value = ItemStackCodec.class)
	public ItemStack structure = MiscUtil.AIR;

	@SaveBool
	public boolean showCollisions, collisionsMode = true;

	public WorldButton toggleMode;

	public WorldButton addCollision, removeCurrentCollision, saveCollision, showCollisionsToggle, nextCollision, previousCollision;
	public WorldBlockPositioner editingCollision = new WorldBlockPositioner();

	public WorldButton addSeat, removeCurrentSeat, saveSeat, nextSeat, previousSeat;
	public WorldBlockPositioner editingSeat = new WorldBlockPositioner();

	private final List<Vector> offsets = new ArrayList<>();
	private final List<ArmorStand> collisions = new ArrayList<>();

	private final List<Vector> seats = new ArrayList<>();
	private final List<ArmorStand> seatEntities = new ArrayList<>();

	@Override
	public void onPlace(BlockContext context)
	{
		createMainUi(context.getLocation());
		createCollisionUi(context.getLocation());
	}

	@Override
	public void onRemove(BlockContext context)
	{
		clearMainUi();
		clearCollisionUi();
		clearCollisions();
		clearSeatUi();
		clearSeats();
	}

	// region Saving/Loading
	@Override
	public void load(JSONObject json)
	{
		if (json.optBoolean("type", true))
		{
			createCollisionUi(pos);
		} else
		{
			createSeatUi(pos);
		}

		if (json.optBoolean("editingCollisions", false))
		{
			editingCollision.offsetX = json.optDouble("offsetX", 0.0);
			editingCollision.offsetY = json.optDouble("offsetY", 0.0);
			editingCollision.offsetZ = json.optDouble("offsetZ", 0.0);

			editingCollision.create(pos);
		}

		if (json.optBoolean("editingSeat", false))
		{
			editingSeat.offsetX = json.optDouble("offsetX", 0.0);
			editingSeat.offsetY = json.optDouble("offsetY", 0.0);
			editingSeat.offsetZ = json.optDouble("offsetZ", 0.0);

			editingSeat.create(pos);
		}


		this.offsets.clear();
		JSONArray offsets = json.optJSONArray("offsets");
		if (offsets == null) offsets = new JSONArray();
		for (int i = 0; i < offsets.length(); i++)
		{
			JSONObject off = offsets.getJSONObject(i);
			double x = off.getDouble("x");
			double y = off.getDouble("y");
			double z = off.getDouble("z");
			this.offsets.add(new Vector(x, y, z));
		}

		createMainUi(pos);
		createCollisions(pos);

		this.seats.clear();
		JSONArray seats = json.optJSONArray("seats");
		if (seats == null) seats = new JSONArray();
		for (int i = 0; i < seats.length(); i++)
		{
			JSONObject seat = seats.getJSONObject(i);
			double x = seat.getDouble("x");
			double y = seat.getDouble("y");
			double z = seat.getDouble("z");
			this.seats.add(new Vector(x, y, z));
		}

		createSeats(pos);
	}

	@Override
	public void save(JSONObject json, boolean unloading)
	{
		if (editingCollision.editing)
		{
			json.put("editingCollisions", true);

			json.put("offsetX", editingCollision.offsetX);
			json.put("offsetY", editingCollision.offsetY);
			json.put("offsetZ", editingCollision.offsetZ);
		}

		if (editingSeat.editing)
		{
			json.put("editingSeat", true);

			json.put("offsetX", editingSeat.offsetX);
			json.put("offsetY", editingSeat.offsetY);
			json.put("offsetZ", editingSeat.offsetZ);
		}

		json.put("type", collisionsMode);

		if (unloading)
		{
			clearMainUi();
			clearCollisionUi();
			clearCollisions();
			clearSeatUi();
			clearSeats();
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

		JSONArray seatsArray = new JSONArray();
		for (Vector seat : seats)
		{
			JSONObject s = new JSONObject();
			s.put("x", seat.getX());
			s.put("y", seat.getY());
			s.put("z", seat.getZ());
			seatsArray.put(s);
		}
		json.put("seats", seatsArray);
	}
	// endregion

	// region Main UI

	public void createMainUi(Location location)
	{
		toggleMode = WorldButton
			.builder()
			.label("Toggle Mode")
			.icon(new ItemStack(collisionsMode ? Material.GLASS : Material.OAK_SLAB))
			.clickAction(pc ->
			{
				collisionsMode = !collisionsMode;
				toggleMode.setIcon(new ItemStack(collisionsMode ? Material.GLASS : Material.OAK_SLAB));
				if (collisionsMode)
				{
					clearSeatUi();
					createCollisionUi(location);
				} else
				{
					clearCollisionUi();
					createSeatUi(location);
				}
			})
			.build(location.clone().add(0.5, 1.4, 0.5));
	}

	public void clearMainUi()
	{
		toggleMode.remove();
	}

	// endregion

	// region Seats

	public void clearSeats()
	{
		for (ArmorStand seat : seatEntities)
		{
			for (Entity passenger : seat.getPassengers())
			{
				passenger.remove();
			}
			seat.remove();
		}
	}

	private void createSeat(World world, Vector offset, Location editorLocation)
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

		Zombie zombie = world.spawn(editorLocation.clone().add(offset).add(1.5, 0.0, 2.5), Zombie.class, s ->
		{
			s.setAI(false);
			s.setInvulnerable(true);
			s.setSilent(true);
			s.setPersistent(true);
		});

		armorStand.addPassenger(zombie);
		seatEntities.add(armorStand);
	}

	public void createSeats(Location editorLocation)
	{
		World world = editorLocation.getWorld();
		if (world == null)
			throw new NullPointerException("World is null!");

		for (Vector offset : seats)
		{
			createSeat(world, offset, editorLocation);
		}
	}

	public void reloadSeats(Location editorLocation)
	{
		clearSeats();
		createSeats(editorLocation);
	}

	// region Seat UI

	public void createSeatUi(Location location)
	{
		World world = location.getWorld();
		if (world == null)
			return;

		addSeat = WorldButton
			.builder()
			.label("Seat")
			.icon(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(3).buildItemStack())
			.clickAction(pc -> editingSeat.create(location))
			.build(location.clone().add(1.5, 1.3, 0.5));

		removeCurrentSeat = WorldButton
			.builder()
			.icon(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(5).buildItemStack())
			.label("Remove Seat")
			.clickAction(pc -> editingSeat.remove())
			.build(location.clone().add(1.5, 2.0, 0.5));

		saveSeat = WorldButton
			.builder()
			.label("Save Seat")
			.icon(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setArmorColor(0x77cc00).setCustomModelData(15).buildItemStack())
			.clickAction(pc ->
			{
				if (!editingSeat.editing) return;

				seats.add(new Vector(editingSeat.offsetX, editingSeat.offsetY + 0.5, editingSeat.offsetZ));
				reloadSeats(location);
				editingSeat.remove();
			})
			.build(location.clone().add(1.5, 0.3, 0.5));

		nextSeat = WorldButton
			.builder()
			.label("Next Seat")
			.icon(ItemStackBuilder
				.create(Material.LEATHER_HORSE_ARMOR)
				.setArmorColor(0x808080)
				.setCustomModelData(11)
				.buildItemStack())
			.clickAction(pc ->
			{
				if (seats.size() == 0) return;

				if (editingSeat.editing)
				{
					seats.add(new Vector(editingSeat.offsetX, editingSeat.offsetY + 0.5, editingSeat.offsetZ));
					reloadSeats(location);
					editingSeat.remove();
				}

				Vector first = seats.remove(0);
				reloadSeats(location);
				editingSeat.create(location, first.getX(), first.getY() - 0.5, first.getZ());
			})
			.build(location.clone().add(2.5, 1.3, 0.5));

		previousSeat = WorldButton
			.builder()
			.label("Previous Seat")
			.icon(ItemStackBuilder
				.create(Material.LEATHER_HORSE_ARMOR)
				.setArmorColor(0x808080)
				.setCustomModelData(10)
				.buildItemStack())
			.clickAction(pc ->
			{
				if (seats.size() == 0) return;

				if (editingSeat.editing)
				{
					seats.add(0, new Vector(editingSeat.offsetX, editingSeat.offsetY + 0.5, editingSeat.offsetZ));
					reloadSeats(location);
					editingSeat.remove();
				}

				Vector first = seats.remove(seats.size() - 1);
				reloadSeats(location);
				editingSeat.create(location, first.getX(), first.getY() - 0.5, first.getZ());
			})
			.build(location.clone().add(3.5, 1.3, 0.5));
	}

	public void clearSeatUi()
	{
		if (addSeat == null)
			return;

		addSeat.remove();
		removeCurrentSeat.remove();
		saveSeat.remove();
		nextSeat.remove();
		previousSeat.remove();
		editingSeat.remove();
	}
	// endregion

	// endregion

	// region Collisions
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
			as.setPersistent(false);
		});

		Shulker shulker = world.spawn(editorLocation.clone().add(offset).add(1.5, 0.0, 2.5), Shulker.class, s ->
		{
			s.setAI(false);
			s.setInvulnerable(true);
			s.setSilent(true);
			s.setPersistent(false);
			s.setInvisible(!showCollisions);
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

	// region Collision UI
	public void createCollisionUi(Location location)
	{
		World world = location.getWorld();
		if (world == null)
			return;

		addCollision = WorldButton
			.builder()
			.label("Collision")
			.icon(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(3).buildItemStack())
			.clickAction(pc -> editingCollision.create(location))
			.build(location.clone().add(1.5, 1.3, 0.5));

		removeCurrentCollision = WorldButton
			.builder()
			.icon(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(5).buildItemStack())
			.label("Remove Collision")
			.clickAction(pc -> editingCollision.remove())
			.build(location.clone().add(1.5, 2.0, 0.5));

		saveCollision = WorldButton
			.builder()
			.label("Save Collision")
			.icon(ItemStackBuilder.create(Material.LEATHER_HORSE_ARMOR).setArmorColor(0x77cc00).setCustomModelData(15).buildItemStack())
			.clickAction(pc ->
			{
				if (!editingCollision.editing) return;

				offsets.add(new Vector(editingCollision.offsetX, editingCollision.offsetY, editingCollision.offsetZ));
				reloadCollisions(location);
				editingCollision.remove();
			})
			.build(location.clone().add(1.5, 0.3, 0.5));

		showCollisionsToggle = WorldButton
			.builder()
			.label("Show Collisions")
			.icon(ItemStackBuilder
				.create(showCollisions ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
				.setCustomModelData(1)
				.buildItemStack())
			.clickAction(pc ->
			{
				showCollisions = !showCollisions;
				showCollisionsToggle.setIcon(ItemStackBuilder
					.create(showCollisions ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
					.setCustomModelData(3)
					.buildItemStack());
				collisions
					.stream()
					.flatMap(collision -> collision.getPassengers().stream())
					.filter(passenger -> passenger instanceof Shulker)
					.map(passenger -> (Shulker) passenger)
					.forEach(shulker -> shulker.setInvisible(!showCollisions));
			}).build(location.clone().add(3.0, 0.4, 0.5));


		nextCollision = WorldButton
			.builder()
			.label("Next Collision")
			.icon(ItemStackBuilder
				.create(Material.LEATHER_HORSE_ARMOR)
				.setArmorColor(0x808080)
				.setCustomModelData(11)
				.buildItemStack())
			.clickAction(pc ->
			{
				if (offsets.size() == 0) return;

				if (editingCollision.editing)
				{
					offsets.add(new Vector(editingCollision.offsetX, editingCollision.offsetY, editingCollision.offsetZ));
					reloadCollisions(location);
					editingCollision.remove();
				}

				Vector first = offsets.remove(0);
				reloadCollisions(location);
				editingCollision.create(location, first.getX(), first.getY(), first.getZ());
			})
			.build(location.clone().add(2.5, 1.3, 0.5));

		previousCollision = WorldButton
			.builder()
			.label("Previous Collision")
			.icon(ItemStackBuilder
				.create(Material.LEATHER_HORSE_ARMOR)
				.setArmorColor(0x808080)
				.setCustomModelData(10)
				.buildItemStack())
			.clickAction(pc ->
			{
				if (offsets.size() == 0) return;

				if (editingCollision.editing)
				{
					offsets.add(0, new Vector(editingCollision.offsetX, editingCollision.offsetY, editingCollision.offsetZ));
					reloadCollisions(location);
					editingCollision.remove();
				}

				Vector first = offsets.remove(offsets.size() - 1);
				reloadCollisions(location);
				editingCollision.create(location, first.getX(), first.getY(), first.getZ());
			})
			.build(location.clone().add(3.5, 1.3, 0.5));
	}

	public void clearCollisionUi()
	{
		removeCurrentCollision.remove();
		saveCollision.remove();
		addCollision.remove();
		editingCollision.remove();
		showCollisionsToggle.remove();
		previousCollision.remove();
		nextCollision.remove();
	}
	// endregion

	// endregion

	// region Serialization
	public JSONObject createElevatorData()
	{
		if (Items.getCustomItem(structure) != FunnyLib.STRUCTURE) return null;

		ItemStackBuilder edit = ItemStackBuilder.edit(structure);

		JSONArray s = edit.getCustomJsonArray("blocks");

		int lx = edit.getInt("lx");
		int ly = edit.getInt("ly");
		int lz = edit.getInt("lz");

		JSONObject json = new JSONObject();

		JSONObject struct = new JSONObject();
		struct.put("blocks", s);
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

		JSONArray seatsArray = new JSONArray();
		for (Vector seat : seats)
		{
			JSONObject sea = new JSONObject();
			sea.put("x", seat.getX());
			sea.put("y", seat.getY());
			sea.put("z", seat.getZ());
			seatsArray.put(sea);
		}
		json.put("seats", seatsArray);

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
		edit.customTagJson("data", data);
		return edit.buildItemStack();
	}
	// endregion

	// region Actions
	public void loadStructure(Location location, @Nullable Player activator)
	{/*
		List<StructureCodec.BlockInfo> blockInfos = StructureCodec.toBlocks(NBT.create(structure).getCompound("block_states"));
		for (StructureCodec.BlockInfo blockInfo : blockInfos)
		{
			location.clone().add(1, 0, 2).add(blockInfo.position().x, blockInfo.position().y, blockInfo.position().z).getBlock().setBlockData(blockInfo.data());
		}
		if (activator != null)
		{
			activator.sendMessage(ChatColor.GREEN + "Loaded!");
		}*/
	}
	// endregion
}