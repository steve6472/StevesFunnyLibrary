package steve6472.standalone.buildbattle.phases;

import org.bukkit.Material;
import org.bukkit.World;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.workdistro.impl.ReplaceBlockWorkload;

public class RectangleCreator
{
	public static void createHollowRectangle(World world, Material toPlace, Material replace, int startX, int startY, int startZ, int width, int height, int depth)
	{
		// Draw the edges
		drawEdges(world, toPlace, replace, startX, startY, startZ, width, height, depth);

		// Draw the faces
		drawFaces(world, toPlace, replace, startX, startY, startZ, width, height, depth);
	}

	private static void drawEdges(World world, Material material, Material replace, int startX, int startY, int startZ, int width, int height, int depth)
	{
		// Draw top edges
		drawHorizontalLine(world, material, replace, startX, startY, startZ, width);
		drawHorizontalLine(world, material, replace, startX, startY, startZ + depth - 1, width);

		// Draw bottom edges
		drawHorizontalLine(world, material, replace, startX, startY + height - 1, startZ, width);
		drawHorizontalLine(world, material, replace, startX, startY + height - 1, startZ + depth - 1, width);

		// Draw vertical edges
		drawVerticalLine(world, material, replace, startX, startY, startZ, height);
		drawVerticalLine(world, material, replace, startX, startY, startZ + depth - 1, height);
		drawVerticalLine(world, material, replace, startX + width - 1, startY, startZ, height);
		drawVerticalLine(world, material, replace, startX + width - 1, startY, startZ + depth - 1, height);
	}

	private static void drawFaces(World world, Material material, Material replace, int startX, int startY, int startZ, int width, int height, int depth)
	{
		// Draw top and bottom faces
		drawHorizontalFace(world, material, replace, startX + 1, startY, startZ + 1, width - 2, depth - 2);
		drawHorizontalFace(world, material, replace, startX + 1, startY + height - 1, startZ + 1, width - 2, depth - 2);

		// Draw side faces
		drawVerticalFace(world, material, replace, startX, startY + 1, startZ + 1, height - 2, depth - 2);
		drawVerticalFace(world, material, replace, startX + width - 1, startY + 1, startZ + 1, height - 2, depth - 2);
	}

	private static void drawHorizontalLine(World world, Material material, Material replace, int startX, int y, int startZ, int length)
	{
		for (int i = 0; i < length; i++)
		{
			setMaterial(world, material, replace, startX + i, y, startZ);
		}
	}

	private static void drawVerticalLine(World world, Material material, Material replace, int x, int startY, int startZ, int length)
	{
		for (int i = 0; i < length; i++)
		{
			setMaterial(world, material, replace, x, startY + i, startZ);
		}
	}

	private static void drawHorizontalFace(World world, Material material, Material replace, int startX, int startY, int startZ, int width, int depth)
	{
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < depth; j++)
			{
				setMaterial(world, material, replace, startX + i, startY, startZ + j);
			}
		}
	}

	private static void drawVerticalFace(World world, Material material, Material replace, int startX, int startY, int startZ, int height, int depth)
	{
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < depth; j++)
			{
				setMaterial(world, material, replace, startX, startY + i, startZ + j);
			}
		}
	}

	private static void setMaterial(World world, Material material, Material replace, int x, int y, int z)
	{
		FunnyLib.getWorkloadRunnable().addWorkload(new ReplaceBlockWorkload(world, x, y, z, material, replace));
	}
}