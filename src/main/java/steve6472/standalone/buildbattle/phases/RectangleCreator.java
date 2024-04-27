package steve6472.standalone.buildbattle.phases;

import org.bukkit.Material;
import org.bukkit.World;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.workdistro.impl.ReplaceBlockWorkload;

public class RectangleCreator
{
	public static void createHollowRectangle(World world, Material toPlace, Material replace, int x, int y, int z, int width, int height, int depth, boolean top, boolean bottom)
	{
		// Draw top and bottom faces
		if (top)
			drawHorizontalFace(world, toPlace, replace, x + 1, y + height - 1, z + 1, width - 2, depth - 2);
		if (bottom)
			drawHorizontalFace(world, toPlace, replace, x + 1, y, z + 1, width - 2, depth - 2);

		// Draw side faces
		drawVerticalFaceX(world, toPlace, replace, x, y, z, height, width);
		drawVerticalFaceX(world, toPlace, replace, x, y, z + depth - 1, height, width);

		drawVerticalFaceZ(world, toPlace, replace, x, y, z, height, depth);
		drawVerticalFaceZ(world, toPlace, replace, x + width - 1, y, z, height, depth);
	}

	private static void drawHorizontalFace(World world, Material material, Material replace, int x, int y, int z, int width, int depth)
	{
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < depth; j++)
			{
				setMaterial(world, material, replace, x + i, y, z + j);
			}
		}
	}

	private static void drawVerticalFaceZ(World world, Material material, Material replace, int x, int y, int z, int height, int depth)
	{
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < depth; j++)
			{
				setMaterial(world, material, replace, x, y + i, z + j);
			}
		}
	}

	private static void drawVerticalFaceX(World world, Material material, Material replace, int x, int y, int z, int height, int depth)
	{
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < depth; j++)
			{
				setMaterial(world, material, replace, x + j, y + i, z);
			}
		}
	}

	private static void setMaterial(World world, Material material, Material replace, int x, int y, int z)
	{
		FunnyLib.getWorkloadRunnable().addWorkload(new ReplaceBlockWorkload(world, x, y, z, replace, material));
	}
}