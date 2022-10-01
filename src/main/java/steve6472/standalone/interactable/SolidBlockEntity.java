package steve6472.standalone.interactable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Shulker;
import org.bukkit.structure.Palette;
import org.bukkit.structure.Structure;
import org.bukkit.util.Vector;
import steve6472.funnylib.util.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class SolidBlockEntity
{
	private final List<ArmorStand> entities;
	private final List<Entity> seats;
	private final World world;

	public SolidBlockEntity(World world)
	{
		this.entities = new ArrayList<>();
		seats = new ArrayList<>();
		this.world = world;
	}

	public void addCustomCollision(Location origin, double offsetX, double offsetY, double offsetZ)
	{
		ArmorStand armorStand = world.spawn(origin
			.clone()
			.add(offsetX + 0.5, offsetY, offsetZ + 0.5), ArmorStand.class);
		armorStand.setMarker(true);
		armorStand.setGravity(false);
		armorStand.setSmall(true);
		armorStand.setInvisible(true);
		armorStand.setInvulnerable(true);
		armorStand.setPersistent(false);

		Shulker shulker = world.spawn(origin, Shulker.class);
		shulker.setAI(false);
		shulker.setInvisible(true);
		shulker.setInvulnerable(true);
		shulker.setAware(false);
		shulker.setSilent(true);
		shulker.setPersistent(false);
		shulker.setHealth(0.2);
		armorStand.addPassenger(shulker);

		entities.add(armorStand);
	}

	public void addSeat(Location origin, double offsetX, double offsetY, double offsetZ)
	{
		ArmorStand armorStand = world.spawn(origin
			.clone()
			.add(offsetX + 0.5, offsetY, offsetZ + 0.5), ArmorStand.class);
		armorStand.setMarker(true);
		armorStand.setGravity(false);
		armorStand.setSmall(true);
		armorStand.setInvisible(true);
		armorStand.setInvulnerable(true);
		seats.add(armorStand);
	}

	public void sit(int seatIndex, Entity entity)
	{
		Preconditions.checkRange(seats, seatIndex);
		seats.get(seatIndex).addPassenger(entity);
	}

	public void addBlock(BlockData data, Location origin, Vector offset, boolean collision)
	{
		if (data.getMaterial().isAir())
			return;

		Location loc = origin.clone();
		loc.add(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5);
		Location blockLoc = loc.clone();

		ArmorStand armorStand = world.spawn(loc, ArmorStand.class);
		armorStand.setMarker(true);
		armorStand.setGravity(false);
		armorStand.setSmall(true);
		armorStand.setInvisible(true);
		armorStand.setInvulnerable(true);

		if (collision)
		{
			Shulker shulker = world.spawn(blockLoc, Shulker.class);
			shulker.setAI(false);
			shulker.setInvisible(true);
			shulker.setInvulnerable(true);
			shulker.setAware(false);
			shulker.setSilent(true);
			shulker.setPersistent(true);
			shulker.setHealth(0.2);
			armorStand.addPassenger(shulker);
		}

		FallingBlock fallingBlock = world.spawnFallingBlock(blockLoc, data);
		fallingBlock.setTicksLived(2);
		fallingBlock.setDropItem(false);
		fallingBlock.setHurtEntities(false);
		fallingBlock.setGravity(false);
		fallingBlock.setPersistent(false);

		armorStand.addPassenger(fallingBlock);
		entities.add(armorStand);
	}

	public void create(Structure structure, Location origin, boolean collision)
	{
		destroy(0);

		for (Palette palette : structure.getPalettes())
		{
			for (BlockState block : palette.getBlocks())
			{
				if (block.getType().isAir())
					continue;

				Location loc = block.getLocation().clone();
				loc.add(origin.getX() + 0.5, origin.getY(), origin.getZ() + 0.5);
				Location blockLoc = loc.clone();
				blockLoc.setY(world.getMaxHeight() + 3);

				ArmorStand armorStand = world.spawn(loc, ArmorStand.class);
				armorStand.setMarker(true);
				armorStand.setGravity(false);
				armorStand.setSmall(true);
				armorStand.setInvisible(true);
				armorStand.setInvulnerable(true);

				if (collision)
				{
					Shulker shulker = world.spawn(blockLoc, Shulker.class);
					shulker.setAI(false);
					shulker.setInvisible(true);
					shulker.setInvulnerable(true);
					shulker.setAware(false);
					shulker.setSilent(true);
					shulker.setPersistent(true);
					shulker.setHealth(0.2);
					armorStand.addPassenger(shulker);
				}

				FallingBlock fallingBlock = world.spawnFallingBlock(blockLoc, block.getBlockData());
				fallingBlock.setTicksLived(2);
				fallingBlock.setDropItem(false);
				fallingBlock.setHurtEntities(false);
				fallingBlock.setGravity(false);

				armorStand.addPassenger(fallingBlock);
				entities.add(armorStand);
			}
		}
	}

	public void move(Location to)
	{
		if (entities.isEmpty())
			return;

		Location reference = entities.get(0).getLocation();

		for (ArmorStand entity : entities)
		{
			Location loc = entity.getLocation();
			double x = loc.getX() - reference.getX() + to.getX();
			double y = loc.getY() - reference.getY() + to.getY();
			double z = loc.getZ() - reference.getZ() + to.getZ();

			ReflectionHacker.callEntityMoveTo(entity, x, y, z, loc.getYaw(), loc.getPitch());
		}

		for (Entity seat : seats)
		{
			Location loc = seat.getLocation();
			double x = loc.getX() - reference.getX() + to.getX();
			double y = loc.getY() - reference.getY() + to.getY();
			double z = loc.getZ() - reference.getZ() + to.getZ();

			ReflectionHacker.callEntityMoveTo(seat, x, y, z, seat.getLocation().getYaw(), seat.getLocation().getPitch());
		}
	}

	public void moveOffset(double x, double y, double z)
	{
		List<Entity> passangers = new ArrayList<>();

		for (ArmorStand entity : entities)
		{
			Location loc = entity.getLocation();
			loc.add(x, y, z);

			passangers.addAll(entity.getPassengers());
			entity.eject();

			entity.teleport(loc);

			for (Entity passanger : passangers)
			{
				entity.addPassenger(passanger);
			}

			passangers.clear();
		}

		for (Entity seat : seats)
		{
			ReflectionHacker.callEntityMoveTo(seat, seat.getLocation().getX() + x, seat.getLocation().getY() + y, seat
				.getLocation()
				.getZ() + z, seat.getLocation().getYaw(), seat.getLocation().getPitch());
		}
	}

	public void update()
	{
		for (ArmorStand entity : entities)
		{
			for (Entity passenger : entity.getPassengers())
			{
				if (passenger instanceof FallingBlock fallingBlock)
				{
					fallingBlock.setTicksLived(1);
				}
			}
		}
	}

	public void destroy(float yaw)
	{
		for (Entity seat : seats)
		{
			List<Entity> pas = new ArrayList<>(seat.getPassengers());
			seat.eject();

			for (Entity passenger : pas)
			{
				Location location = seat.getLocation();
				location.setY(Math.round(location.getY()) + 0.001);
				location.setYaw(yaw);
				passenger.teleport(location);
			}

			seat.remove();
		}

		for (ArmorStand entity : entities)
		{
			for (Entity passenger : entity.getPassengers())
			{
				passenger.remove();
			}
			entity.remove();
		}
		entities.clear();
	}
}