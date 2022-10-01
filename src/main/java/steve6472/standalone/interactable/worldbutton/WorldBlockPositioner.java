package steve6472.standalone.interactable.worldbutton;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Shulker;
import org.bukkit.inventory.ItemStack;
import steve6472.funnylib.util.GlowingUtil;
import steve6472.funnylib.util.ItemStackBuilder;
import steve6472.standalone.interactable.ReflectionHacker;

/**
 * Created by steve6472
 * Date: 10/1/2022
 * Project: StevesFunnyLibrary <br>
 */
public class WorldBlockPositioner
{
	private static final WorldButtonBuilder builder = WorldButton
		.builder()
		.activeColor(ChatColor.WHITE)
		.disabledColor(ChatColor.BLACK)
		.remote(true)
		.glowAlways(true)
		.labelSubtitle()
		.size(1);

	public WorldButton forward, back, up, down, left, right;
	public ArmorStand editingCollision;
	public Shulker editingCollision_;
	public double offsetX, offsetY, offsetZ;
	public boolean editing = false;

	private static ItemStack horseIcon(int color, int data)
	{
		return ItemStackBuilder
			.create(Material.LEATHER_HORSE_ARMOR)
			.setArmorColor(color)
			.setCustomModelData(data)
			.buildItemStack();
	}

	public void offset(double dx, double dy, double dz)
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

	public void create(Location location)
	{
		create(location, offsetX, offsetY, offsetZ);
	}

	public void create(Location location, double offsetX, double offsetY, double offsetZ)
	{
		if (editing) return;
		editing = true;

		World world = location.getWorld();
		if (world == null)
			return;

		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;

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
			.clickAction(pc -> offset(0, 1d / 16d, 0))
			.build(new Location(location.getWorld(), x, y + 1, z));

		down = builder
			.icon(horseIcon(0xcc00cc, 9))
			.label("Down")
			.clickAction(pc -> offset(0, -1d / 16d, 0))
			.build(new Location(location.getWorld(), x, y - 1, z));

		left = builder
			.icon(horseIcon(0xcc0000, 10))
			.label("Left")
			.clickAction(pc -> offset(1d / 16d, 0, 0))
			.build(new Location(location.getWorld(), x + 1, y, z));

		right = builder
			.icon(horseIcon(0x00cccc, 11))
			.label("Right")
			.clickAction(pc -> offset(-1d / 16d, 0, 0))
			.build(new Location(location.getWorld(), x - 1, y, z));

		forward = builder
			.icon(horseIcon(0x0000cc, 12))
			.label("Forward")
			.clickAction(pc -> offset(0, 0, 1d / 16d))
			.build(new Location(location.getWorld(), x, y, z + 1));

		back = builder
			.icon(horseIcon(0xcccc00, 13))
			.label("Back")
			.clickAction(pc -> offset(0, 0, -1d / 16d))
			.build(new Location(location.getWorld(), x, y, z - 1));
	}

	public void remove()
	{
		if (!editing) return;

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

		forward = null;
		back = null;
		up = null;
		down = null;
		left = null;
		right = null;

		offsetX = 0;
		offsetY = 0;
		offsetZ = 0;
		editing = false;
	}
}
