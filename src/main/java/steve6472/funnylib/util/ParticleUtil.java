package steve6472.funnylib.util;

import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

/**********************
 * Created by steve6472 (Mirek Jozefek)
 * On date: 1/30/2021
 * Project: RBS
 *
 ***********************/
public class ParticleUtil
{
	public static void iterateLine(double startX, double startY, double startZ, double endX, double endY, double endZ, int maxIterations, double spacing, TriConsumer<Double, Double, Double> pos)
	{
		double distance = (endX - startX) * (endX - startX) + (endY - startY) * (endY - startY) + (endZ - startZ) * (endZ - startZ);

		double vx = (endX - startX);
		double vy = (endY - startY);
		double vz = (endZ - startZ);
		double l = Math.sqrt(vx * vx + vy * vy + vz * vz);

		vx = (vx / l) * spacing;
		vy = (vy / l) * spacing;
		vz = (vz / l) * spacing;

//		double vx = (endX - startX) * spacing;
//		double vy = (endY - startY) * spacing;
//		double vz = (endZ - startZ) * spacing;

		for (int iteration = 0; iteration < maxIterations; iteration++)
		{
			pos.accept(startX, startY, startZ);
			startX += vx;
			startY += vy;
			startZ += vz;

			double d = (endX - startX) * (endX - startX) + (endY - startY) * (endY - startY) + (endZ - startZ) * (endZ - startZ);
			if (d > distance)
				return;
			distance = d;
		}
	}

	public static void line(World world, Location start, Vector direction, double length, double particleSpeed, double spacing)
	{
		ParticleUtil.line(world, Particle.END_ROD, start.getX(), start.getY(), start.getZ(), start.getX() + direction.getX() * length, start
			.getY() + direction.getY() * length, start.getZ() + direction.getZ() * length, particleSpeed, spacing);
	}

	public static void line(World world, Particle particle, double x0, double y0, double z0, double x1, double y1, double z1, double particleSpeed, double spacing)
	{
		iterateLine(x0, y0, z0, x1, y1, z1, 1024, spacing, (x, y, z) -> world.spawnParticle(particle, (double) x, (double) y, (double) z, 1, 0, 0, 0, particleSpeed, null, true));
	}

	public static void planeY(World world, Particle particle, double x, double y, double z, double width, double depth, double particleSpeed, double spacing)
	{
		for (double i = x - width; i < x + width; i += spacing)
		{
			for (double j = z - depth; j < z + depth; j += spacing)
			{
				world.spawnParticle(particle, i, y, j, 0, 0, 0, particleSpeed);
			}
		}
	}

	public static void box(World world, Particle particle, double x, double y, double z, double width, double height, double depth, double particleSpeed, double spacing)
	{
		double x0 = x - width;
		double y0 = y - height;
		double z0 = z - depth;
		double x1 = x + width;
		double y1 = y + height;
		double z1 = z + depth;

		// Bottom
		line(world, particle, x0, y0, z0, x1, y0, z0, particleSpeed, spacing);
		line(world, particle, x0, y0, z0, x0, y0, z1, particleSpeed, spacing);
		line(world, particle, x1, y0, z0, x1, y0, z1, particleSpeed, spacing);
		line(world, particle, x0, y0, z1, x1, y0, z1, particleSpeed, spacing);

		// Top
		line(world, particle, x0, y1, z0, x1, y1, z0, particleSpeed, spacing);
		line(world, particle, x0, y1, z0, x0, y1, z1, particleSpeed, spacing);
		line(world, particle, x1, y1, z0, x1, y1, z1, particleSpeed, spacing);
		line(world, particle, x0, y1, z1, x1, y1, z1, particleSpeed, spacing);

		// Sides
		line(world, particle, x0, y0, z0, x0, y1, z0, particleSpeed, spacing);
		line(world, particle, x1, y0, z0, x1, y1, z0, particleSpeed, spacing);
		line(world, particle, x0, y0, z1, x0, y1, z1, particleSpeed, spacing);
		line(world, particle, x1, y0, z1, x1, y1, z1, particleSpeed, spacing);
	}

	public static void boxAbsolute(World world, Particle particle, double x0, double y0, double z0, double x1, double y1, double z1, double particleSpeed, double spacing)
	{
		// Bottom
		line(world, particle, x0, y0, z0, x1, y0, z0, particleSpeed, spacing);
		line(world, particle, x0, y0, z0, x0, y0, z1, particleSpeed, spacing);
		line(world, particle, x1, y0, z0, x1, y0, z1, particleSpeed, spacing);
		line(world, particle, x0, y0, z1, x1, y0, z1, particleSpeed, spacing);

		// Top
		line(world, particle, x0, y1, z0, x1, y1, z0, particleSpeed, spacing);
		line(world, particle, x0, y1, z0, x0, y1, z1, particleSpeed, spacing);
		line(world, particle, x1, y1, z0, x1, y1, z1, particleSpeed, spacing);
		line(world, particle, x0, y1, z1, x1, y1, z1, particleSpeed, spacing);

		// Sides
		line(world, particle, x0, y0, z0, x0, y1, z0, particleSpeed, spacing);
		line(world, particle, x1, y0, z0, x1, y1, z0, particleSpeed, spacing);
		line(world, particle, x0, y0, z1, x0, y1, z1, particleSpeed, spacing);
		line(world, particle, x1, y0, z1, x1, y1, z1, particleSpeed, spacing);
	}

	/*
	 * Player
	 */

	public static void line(Player player, Location start, Vector direction, double length, double particleSpeed, double spacing)
	{
		ParticleUtil.line(player, Particle.END_ROD, start.getX(), start.getY(), start.getZ(), start.getX() + direction.getX() * length, start
			.getY() + direction.getY() * length, start.getZ() + direction.getZ() * length, particleSpeed, spacing);
	}

	public static void line(Player player, Particle particle, double x0, double y0, double z0, double x1, double y1, double z1, double particleSpeed, double spacing)
	{
		iterateLine(x0, y0, z0, x1, y1, z1, 1024, spacing, (x, y, z) -> player.spawnParticle(particle, (double) x, (double) y, (double) z, 1, 0, 0, 0, particleSpeed));
	}

	public static void planeY(Player player, Particle particle, double x, double y, double z, double width, double depth, double particleSpeed, double spacing)
	{
		for (double i = x - width; i < x + width; i += spacing)
		{
			for (double j = z - depth; j < z + depth; j += spacing)
			{
				player.spawnParticle(particle, i, y, j, 0, 0, 0, particleSpeed);
			}
		}
	}

	public static void box(Player player, Particle particle, double x, double y, double z, double width, double height, double depth, double particleSpeed, double spacing)
	{
		double x0 = x - width;
		double y0 = y - height;
		double z0 = z - depth;
		double x1 = x + width;
		double y1 = y + height;
		double z1 = z + depth;

		// Bottom
		line(player, particle, x0, y0, z0, x1, y0, z0, particleSpeed, spacing);
		line(player, particle, x0, y0, z0, x0, y0, z1, particleSpeed, spacing);
		line(player, particle, x1, y0, z0, x1, y0, z1, particleSpeed, spacing);
		line(player, particle, x0, y0, z1, x1, y0, z1, particleSpeed, spacing);

		// Top
		line(player, particle, x0, y1, z0, x1, y1, z0, particleSpeed, spacing);
		line(player, particle, x0, y1, z0, x0, y1, z1, particleSpeed, spacing);
		line(player, particle, x1, y1, z0, x1, y1, z1, particleSpeed, spacing);
		line(player, particle, x0, y1, z1, x1, y1, z1, particleSpeed, spacing);

		// Sides
		line(player, particle, x0, y0, z0, x0, y1, z0, particleSpeed, spacing);
		line(player, particle, x1, y0, z0, x1, y1, z0, particleSpeed, spacing);
		line(player, particle, x0, y0, z1, x0, y1, z1, particleSpeed, spacing);
		line(player, particle, x1, y0, z1, x1, y1, z1, particleSpeed, spacing);
	}

	public static void boxAbsolute(Player player, Particle particle, double x0, double y0, double z0, double x1, double y1, double z1, double particleSpeed, double spacing)
	{
		// Bottom
		line(player, particle, x0, y0, z0, x1, y0, z0, particleSpeed, spacing);
		line(player, particle, x0, y0, z0, x0, y0, z1, particleSpeed, spacing);
		line(player, particle, x1, y0, z0, x1, y0, z1, particleSpeed, spacing);
		line(player, particle, x0, y0, z1, x1, y0, z1, particleSpeed, spacing);

		// Top
		line(player, particle, x0, y1, z0, x1, y1, z0, particleSpeed, spacing);
		line(player, particle, x0, y1, z0, x0, y1, z1, particleSpeed, spacing);
		line(player, particle, x1, y1, z0, x1, y1, z1, particleSpeed, spacing);
		line(player, particle, x0, y1, z1, x1, y1, z1, particleSpeed, spacing);

		// Sides
		line(player, particle, x0, y0, z0, x0, y1, z0, particleSpeed, spacing);
		line(player, particle, x1, y0, z0, x1, y1, z0, particleSpeed, spacing);
		line(player, particle, x0, y0, z1, x0, y1, z1, particleSpeed, spacing);
		line(player, particle, x1, y0, z1, x1, y1, z1, particleSpeed, spacing);
	}

	/*
	 * Player Particle Data
	 */

	public static <T>  void sphere(Player player, Particle particle, Location center, double quality, double radius, @Nullable T data)
	{
		for (double i = 0; i <= Math.PI; i += Math.PI / quality)
		{
			double rad = Math.sin(i) * radius;
			double y = Math.cos(i) * radius;
			for (double a = 0; a < Math.PI * 2; a += Math.PI / quality)
			{
				double x = Math.cos(a) * rad;
				double z = Math.sin(a) * rad;
				center.add(x, y, z);
				if (center.distance(player.getLocation()) < 32)
					player.spawnParticle(particle, center, 1, 0, 0, 0, 0, data);
				center.subtract(x, y, z);
			}
		}
	}

	public static <T> void line(Player player, Location start, Vector direction, double length, double particleSpeed, double spacing, @Nullable T data)
	{
		line(player, Particle.END_ROD, start.getX(), start.getY(), start.getZ(), start.getX() + direction.getX() * length, start
			.getY() + direction.getY() * length, start.getZ() + direction.getZ() * length, particleSpeed, spacing, data);
	}

	public static <T> void line(Player player, Particle particle, double x0, double y0, double z0, double x1, double y1, double z1, double particleSpeed, double spacing, @Nullable T data)
	{
		iterateLine(x0, y0, z0, x1, y1, z1, 1024, spacing, (x, y, z) -> player.spawnParticle(particle, x, y, z, 1, 0, 0, 0, particleSpeed, data));
	}

	public static <T> void line(World world, Particle particle, double x0, double y0, double z0, double x1, double y1, double z1, double particleSpeed, double spacing, @Nullable T data)
	{
		iterateLine(x0, y0, z0, x1, y1, z1, 1024, spacing, (x, y, z) -> world.spawnParticle(particle, x, y, z, 1, 0, 0, 0, particleSpeed, data));
	}

	public static <T> void planeY(Player player, Particle particle, double x, double y, double z, double width, double depth, double particleSpeed, double spacing, @Nullable T data)
	{
		for (double i = x - width; i < x + width; i += spacing)
		{
			for (double j = z - depth; j < z + depth; j += spacing)
			{
				player.spawnParticle(particle, i, y, j, 0, 0, 0, particleSpeed, data);
			}
		}
	}

	public static <T> void box(Player player, Particle particle, double x, double y, double z, double width, double height, double depth, double particleSpeed, double spacing, @Nullable T data)
	{
		double x0 = x - width;
		double y0 = y - height;
		double z0 = z - depth;
		double x1 = x + width;
		double y1 = y + height;
		double z1 = z + depth;

		// Bottom
		line(player, particle, x0, y0, z0, x1, y0, z0, particleSpeed, spacing, data);
		line(player, particle, x0, y0, z0, x0, y0, z1, particleSpeed, spacing, data);
		line(player, particle, x1, y0, z0, x1, y0, z1, particleSpeed, spacing, data);
		line(player, particle, x0, y0, z1, x1, y0, z1, particleSpeed, spacing, data);

		// Top
		line(player, particle, x0, y1, z0, x1, y1, z0, particleSpeed, spacing, data);
		line(player, particle, x0, y1, z0, x0, y1, z1, particleSpeed, spacing, data);
		line(player, particle, x1, y1, z0, x1, y1, z1, particleSpeed, spacing, data);
		line(player, particle, x0, y1, z1, x1, y1, z1, particleSpeed, spacing, data);

		// Sides
		line(player, particle, x0, y0, z0, x0, y1, z0, particleSpeed, spacing, data);
		line(player, particle, x1, y0, z0, x1, y1, z0, particleSpeed, spacing, data);
		line(player, particle, x0, y0, z1, x0, y1, z1, particleSpeed, spacing, data);
		line(player, particle, x1, y0, z1, x1, y1, z1, particleSpeed, spacing, data);
	}

	public static <T> void boxAbsolute(Player player, Particle particle, double x0, double y0, double z0, double x1, double y1, double z1, double particleSpeed, double spacing, @Nullable T data)
	{
		// Bottom
		line(player, particle, x0, y0, z0, x1, y0, z0, particleSpeed, spacing, data);
		line(player, particle, x0, y0, z0, x0, y0, z1, particleSpeed, spacing, data);
		line(player, particle, x1, y0, z0, x1, y0, z1, particleSpeed, spacing, data);
		line(player, particle, x0, y0, z1, x1, y0, z1, particleSpeed, spacing, data);

		// Top
		line(player, particle, x0, y1, z0, x1, y1, z0, particleSpeed, spacing, data);
		line(player, particle, x0, y1, z0, x0, y1, z1, particleSpeed, spacing, data);
		line(player, particle, x1, y1, z0, x1, y1, z1, particleSpeed, spacing, data);
		line(player, particle, x0, y1, z1, x1, y1, z1, particleSpeed, spacing, data);

		// Sides
		line(player, particle, x0, y0, z0, x0, y1, z0, particleSpeed, spacing, data);
		line(player, particle, x1, y0, z0, x1, y1, z0, particleSpeed, spacing, data);
		line(player, particle, x0, y0, z1, x0, y1, z1, particleSpeed, spacing, data);
		line(player, particle, x1, y0, z1, x1, y1, z1, particleSpeed, spacing, data);
	}

	public static <T> void boxAbsolute(World world, Particle particle, double x0, double y0, double z0, double x1, double y1, double z1, double particleSpeed, double spacing, @Nullable T data)
	{
		// Bottom
		line(world, particle, x0, y0, z0, x1, y0, z0, particleSpeed, spacing, data);
		line(world, particle, x0, y0, z0, x0, y0, z1, particleSpeed, spacing, data);
		line(world, particle, x1, y0, z0, x1, y0, z1, particleSpeed, spacing, data);
		line(world, particle, x0, y0, z1, x1, y0, z1, particleSpeed, spacing, data);

		// Top
		line(world, particle, x0, y1, z0, x1, y1, z0, particleSpeed, spacing, data);
		line(world, particle, x0, y1, z0, x0, y1, z1, particleSpeed, spacing, data);
		line(world, particle, x1, y1, z0, x1, y1, z1, particleSpeed, spacing, data);
		line(world, particle, x0, y1, z1, x1, y1, z1, particleSpeed, spacing, data);

		// Sides
		line(world, particle, x0, y0, z0, x0, y1, z0, particleSpeed, spacing, data);
		line(world, particle, x1, y0, z0, x1, y1, z0, particleSpeed, spacing, data);
		line(world, particle, x0, y0, z1, x0, y1, z1, particleSpeed, spacing, data);
		line(world, particle, x1, y0, z1, x1, y1, z1, particleSpeed, spacing, data);
	}
}
