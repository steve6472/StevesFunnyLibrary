package steve6472.standalone.interactable.blocks;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Shulker;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;
import steve6472.funnylib.blocks.CustomBlockData;
import steve6472.funnylib.context.BlockContext;
import steve6472.funnylib.item.builtin.StructureItem;
import steve6472.funnylib.json.codec.ann.Save;
import steve6472.funnylib.json.codec.ann.SaveDouble;
import steve6472.funnylib.json.codec.codecs.ItemStackCodec;
import steve6472.funnylib.util.GlowingUtil;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.funnylib.util.MiscUtil;
import steve6472.standalone.interactable.ReflectionHacker;
import steve6472.standalone.interactable.worldbutton.WorldButton;
import steve6472.standalone.interactable.worldbutton.WorldButtonBuilder;

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

	public WorldButton loadButton, addCollision, removeCurrectCollision, forward, back, up, down, left, right;

	public ArmorStand editingCollision;
	public Shulker editingCollision_;

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
	}

	@Override
	public void load(JSONObject json)
	{
		Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
		loadButtons(location);
		if (json.getBoolean("editing"))
		{
			createEditingStuff(location);
		}
	}

	@Override
	public void save(JSONObject json)
	{
		clearButtons();
		json.put("editing", editingCollision != null);
		removeEditingStuff();
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

	private void removeEditingStuff()
	{
		if (editingCollision == null) return;

		editingCollision.remove();
		editingCollision_.remove();

		forward.remove();
		back.remove();
		up.remove();
		down.remove();
		left.remove();
		right.remove();
	}

	public void loadButtons(Location location)
	{
		World world = location.getWorld();
		if (world == null)
			return;

		loadButton = WorldButton
			.builder()
			.label("Load")
			.icon(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(3).buildItemStack())
			.size(1)
			.followClosestPlayer(true)
			.clickAction(pc ->
			{
				List<StructureItem.BlockInfo> blockInfos = StructureItem.toBlocks(structure);
				for (StructureItem.BlockInfo blockInfo : blockInfos)
				{
					location.clone().add(1, 0, 2).add(blockInfo.position()).getBlock().setBlockData(blockInfo.data());
				}
				pc.getPlayer().sendMessage(ChatColor.GREEN + "Loaded!");
			})
			.build(location.clone().add(0.5, 1.3, 0.5));

		addCollision = WorldButton
			.builder()
			.label("Collision")
			.icon(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(3).buildItemStack())
			.clickAction(pc -> createEditingStuff(location))
			.build(location.clone().add(1.5, 1.3, 0.5));

		removeCurrectCollision = WorldButton
			.builder()
			.icon(ItemStackBuilder.create(Material.COMMAND_BLOCK).setCustomModelData(5).buildItemStack())
			.label("Remove Collision")
			.clickAction(pc ->
			{
				removeEditingStuff();
				offsetX = 0;
				offsetY = 0;
				offsetZ = 0;
			})
			.build(location.clone().add(1.5, 2.0, 0.5));
	}

	public void clearButtons()
	{
		removeCurrectCollision.remove();
		loadButton.remove();
		addCollision.remove();
	}
}