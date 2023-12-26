package steve6472.funnylib.border;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.util.JSONMessage;
import steve6472.funnylib.util.RandomUtil;

import java.util.HashMap;
import java.util.UUID;

/**********************
 * Created by steve6472
 * On date: 6/9/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
@SuppressWarnings({"SameParameterValue", "unused"})
public class Border
{
	public static final Particle.DustOptions BORDER_FAR = new Particle.DustOptions(Color.AQUA, 2.5f);
	public static final Particle.DustOptions BORDER_MID = new Particle.DustOptions(Color.ORANGE, 1.75f);
	public static final Particle.DustOptions BORDER_CLOSE = new Particle.DustOptions(Color.RED, 1f);
	public static final Particle.DustOptions BORDER_CLOSE_SMALL = new Particle.DustOptions(Color.RED, 0.5f);
	public static final BlockData BORDER_DEBUG = Material.LIGHT_BLUE_STAINED_GLASS.createBlockData();

	public static HashMap<UUID, Double> TEMP_DISTANCE = new HashMap<>();

	private double map(double x, double y, double z)
	{
		double mainBox = sdfBox(x, y, z, 8949.5, 85.5, 9717.5, 2.5, 2.5, 2.5);
		double corridorToSphere = sdfBox(x, y, z, 8949.5, 84, 9713.5, 0.5, 1.0, 2.5);
		double mainSphere = sdfSphere(x, y, z, 8949.5, 84.5, 9708.5, 3.0);

		double corridorToNiceCorner = sdfBox(x, y, z, 8954.5, 84.0, 9717.5, 3.5, 1.0, 0.5);
		double cornerCylinder = sdfCylinderY(x, y, z, 8955.5, 84.0, 9715.5, 1.5, 1.1);
		double corridorFromNiceCorner = sdfBox(x, y, z, 8957.5, 84.0, 9713.0, 0.5, 1.0, 2.1);
		double cornerBox = sdfBox(x, y, z, 8956.5, 84.0, 9716.5, 1.5, 1.0, 1.5);

		double curvedCorner = max(cornerBox, -cornerCylinder); //max(a, -b) => cut 'b' from 'a'
		double curvedCornerTo = min(curvedCorner, corridorToNiceCorner);
		double curvedCronerFrom = min(corridorFromNiceCorner, curvedCornerTo);

		double fancyCorridorBox = sdfBox(x, y, z, 8956.5, 84.0, 9709.5, 1.5, 1.0, 1.5);
		double fancyCorridorInner = sdfCylinderY(x, y, z, 8955.0, 84.0, 9711.0, 2.0, 1.1);
		double fancyCorridorOuter = sdfCylinderY(x, y, z, 8955.0, 84.0, 9711.0, 3.0, 1.1);
		double fancyCorridorCutInner = max(fancyCorridorBox, -fancyCorridorInner);
		double fancyCorridorCutOuter = max(fancyCorridorBox, fancyCorridorOuter);
		double fancyCorridor = max(fancyCorridorCutOuter, fancyCorridorCutInner);

		return min(min(min(min(mainBox, corridorToSphere), mainSphere), curvedCronerFrom), fancyCorridor);
	}

	private double mainMap(double x, double y, double z)
	{
		double mainCylinder = sdfCylinderY(x, y, z, 8800.5, 100.0, 10000.5, 130, 80);

		/*
		 * Mine area
		 */

		double amethystGeode = sdfSphere(x, y, z, 8846.5, 101.0, 9876.5, 9.0);
		double tntMine = sdfCylinderZ(x, y, z, 8853.0, 92.0, 9869.0, 9.0, 17.0);

		/*
		 * Desert area
		 */
		double crypt = sdfBoxAbs(x, y, z, 8659.0, 88.0, 9981.0, 8697.0, 72.0, 9932.0);

		/*
		 * Desert underground area
		 */
		double yggdrasilRoom = sdfBoxAbs(x, y, z, 8577.0, 36.0, 9876.0, 8726.0, 72.0, 9947.0);
		double yggdrasilRoomOutCorner = sdfBoxAbs(x, y, z, 8718.0, 72.0, 9869.0, 8786.0, 36.0, 9895.0);

		/*
		 * Mesa area
		 */
		double vaultEntrance = sdfBoxAbs(x, y, z, 8720.0, 97.0, 10100.0, 8696.0, 110.0, 10084.0);
		double vaultStairs = sdfCapsule(x, y, z, 8698.0, 102.0, 10092.0, 8689.0, 93.0, 10092.0, 6.0);
		double vaultHall = sdfBoxAbs(x, y, z, 8690.0, 89.0, 10087.0, 8665.0, 99.0, 10097.0);
		double vaultFire = sdfBoxAbs(x, y, z, 8676.0, 89.0, 10095.5, 8693.0, 110.0, 10126.0);

		/*
		 * Fancy island
		 */
		double islandPassage = sdfCapsule(x, y, z, 8913.5, 102.5, 10077.0, 8971.0, 103.5, 10088.5, 8.0);
		double islandArea = sdfSphere(x, y, z, 8976.0, 109.5, 10089.5, 24);

		return min(
			mainCylinder,

			amethystGeode,
			tntMine,

			crypt,

			yggdrasilRoom,
			yggdrasilRoomOutCorner,

			vaultEntrance,
			vaultStairs,
			vaultHall,
			vaultFire,

			islandPassage,
			islandArea
		);
	}

	private double map(Player player)
	{
//		return map(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
		return mainMap(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
	}

	private void tickPlayer(Player player)
	{


		double t = map(player);

		TEMP_DISTANCE.put(player.getUniqueId(), t);

		double tAbs = abs(t);

/*
		if (player.getName().equals("steve6472") && t <= 3.5)
		{
			renderBorder(player, 3.0, 0.125, 1, BORDER_CLOSE_SMALL);

			if (AkmaEventMain.getUptimeTicks() % (player.hashCode() % 4) == 0)
			{
				TEMP_TIMES.put(player, (float) (System.nanoTime() - start) / 1_000_000_000f);
			}
			return;
		}*/

		if (FunnyLib.getUptimeTicks() % 10 == 0)
		{
//			Tag playerTag = Tag.findTag("player");
//			if (Tag.playerHasTag(player, playerTag))
//			{
//				if (t >= 0)
//				{
//					player.damage(1);
//				}
//
//				if (t < 0 && tAbs < 3)
//				{
//					JSONMessage.create("You are getting close to the border!").color("#ffa500").actionbar(player);
//				}
//
//				if (t > -1)
//				{
//					JSONMessage
//						.create("The border deals half a heart of damage every half a second. You will die. Get out!")
//						.color("#ff0000")
//						.actionbar(player);
//				}
//			}
		}

		if (tAbs <= 3.0)
		{
			renderBorder(player, 3.0, 0.25, 2, 5, Particle.REDSTONE, BORDER_CLOSE);
			renderBorder(player, 8.0, 0.5, 8, 10, Particle.REDSTONE, BORDER_MID);
			renderBorder(player, 16.0, 1.0, 12, 15, Particle.REDSTONE, BORDER_FAR);
		}
		else if (tAbs <= 8.0)
		{
			renderBorder(player, 8.0, 0.5, 4, 5, Particle.REDSTONE, BORDER_MID);
			renderBorder(player, 16.0, 1.0, 8, 10, Particle.REDSTONE, BORDER_FAR);
		}
		else if (tAbs <= 16.0)
		{
			renderBorder(player, 16.0, 1.0, 4, 5, Particle.REDSTONE, BORDER_FAR);
		}
	}

	private <T> void renderBorder(Player player, double scanSize, double change, int hideChance, int everyXTick, Particle particle, T particleOptions)
	{
		if (FunnyLib.getUptimeTicks() % (Math.floorMod(player.hashCode(), everyXTick) + 1) != 0)
			return;

		for (double i = -scanSize; i < scanSize; i += change)
		{
			for (double j = -scanSize; j < scanSize; j += change)
			{
				for (double k = -scanSize; k < scanSize; k += change)
				{
					// 1/x chace to not show
					if (hideChance != 0 && !RandomUtil.decide(hideChance))
						continue;

					double d = mainMap(player.getLocation().getBlockX() + i, player.getLocation().getBlockY() + j + 2.0, player.getLocation().getBlockZ() + k);
					if (abs(d) - change < 0.00001d && d > 0)
					{
						player.spawnParticle(particle, i + player.getLocation().getBlockX(), j + player.getLocation().getBlockY() + 2.0, k + player.getLocation().getBlockZ(), 1, particleOptions);
					}
				}
			}
		}
	}

	public void tick()
	{
		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
		{
			tickPlayer(onlinePlayer);
		}
	}

	/*
	 * SDF functions
	 */

	private double sdfBoxAbs(double px, double py, double pz, double x0, double y0, double z0, double x1, double y1, double z1)
	{
		double cx = (x0 + x1) * 0.5;
		double cy = (y0 + y1) * 0.5;
		double cz = (z0 + z1) * 0.5;
		double w = abs(x0 - cx);
		double h = abs(y0 - cy);
		double d = abs(z0 - cz);
		return sdfBox(px, py, pz, cx, cy, cz, w, h, d);
	}

	private double sdfBox(double px, double py, double pz, double cx, double cy, double cz, double w, double h, double d)
	{
		double x = max(px - cx - w, cx - px - w);
		double y = max(py - cy - h, cy - py - h);

		double z = max(pz - cz - d, cz - pz - d);
		double D = x;
		D = max(D, y);
		D = max(D, z);
		return D;
	}

	private double sdfSphere(double px, double py, double pz, double x, double y, double z, double r)
	{
		return sqrt(pow(px - x, 2) + pow(py - y, 2) + pow(pz - z, 2)) - r;
	}

	private double sdfCylinderY(double px, double py, double pz, double x, double y, double z, double r, double height)
	{
		double d = sqrt(pow(px - x, 2) + pow(pz - z, 2)) - r;
		d = max(d, abs(py - y) - height);
		return d;
	}

	private double sdfCylinderZ(double px, double py, double pz, double x, double y, double z, double r, double height)
	{
		double d = sqrt(pow(px - x, 2) + pow(py - y, 2)) - r;
		d = max(d, abs(pz - z) - height);
		return d;
	}

	private double sdfLineSegment(double px, double py, double pz, double x0, double y0, double z0, double x1, double y1, double z1)
	{
		double x = x1 - x0;
		double y = y1 - y0;
		double z = z1 - z0;
		double t = saturate(dot(px - x0, py - y0, pz - z0, x, y, z) / dot(x, y, z, x, y, z));
		return length((x * t + x0) - px, (y * t + y0) - py, (z * t + z0) - pz);
	}

	private double sdfCapsule(double px, double py, double pz, double x0, double y0, double z0, double x1, double y1, double z1, double r)
	{
		return sdfLineSegment(px, py, pz, x0, y0, z0, x1, y1, z1) - r;
	}

	private double sdfBlend(double d1, double d2, double a)
	{
		return a * d1 + (1.0 - a) * d2;
	}

	private double sdfSmin(double a, double b, double k)
	{
		double res = Math.exp(-k * a) + Math.exp(-k * b);
		return -Math.log(max(0.0001, res)) / k;
	}

	private double min(double a, double b)
	{
		return Math.min(a, b);
	}

	private double min(double... doubles)
	{
		double m = doubles[0];
		for (int i = 1; i < doubles.length; i++)
		{
			double d = doubles[i];
			m = min(m, d);
		}

		return m;
	}

	private double max(double a, double b)
	{
		return Math.max(a, b);
	}

	private double abs(double a)
	{
		return Math.abs(a);
	}

	private double pow(double base, double exponent)
	{
		return Math.pow(base, exponent);
	}

	private double sqrt(double base)
	{
		return Math.sqrt(base);
	}

	private double clamp(double number, double min, double max)
	{
		return min(max(number, min), max);
	}

	private double saturate(double x)
	{
		return clamp(x, 0.0, 1.0);
	}

	private double length(double x, double y, double z)
	{
		return sqrt(pow(x, 2.0) + pow(y, 2.0) + pow(z, 2.0));
	}

	private double dot(double x0, double y0, double z0, double x1, double y1, double z1)
	{
		return x0 * x1 + y0 * y1 + z0 * z1;
	}
}
