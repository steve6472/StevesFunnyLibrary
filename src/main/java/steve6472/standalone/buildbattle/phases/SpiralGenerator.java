package steve6472.standalone.buildbattle.phases;

import org.joml.Vector2i;

public class SpiralGenerator
{
	public static void main(String[] args)
	{
		for (int i = 0; i < 122; i++)
		{
			Vector2i pos = getPos(i);
			System.out.println(pos);
		}
	}

	public static Vector2i getPos(int n)
	{
		if (n == 0)
			return new Vector2i(0, 0);

		int ringIndex = getRingIndex(n);
		int sideLen = getSideLen(ringIndex);
		int shift = sideLen / 2;
		int indexInRing = indexInRing(ringIndex, n, shift);

		int si;
		if (indexInRing < sideLen)
			si = addWithBound(indexInRing, shift + 1, sideLen);
		else if (indexInRing - sideLen < sideLen - 2)
			si = addWithBound(indexInRing - sideLen, 0, sideLen - 2);
		else if (indexInRing - sideLen * 2 + 2 < sideLen)
			si = addWithBound(indexInRing - sideLen * 2 + 2, 0, sideLen);
		else
			si = addWithBound(indexInRing, 0, sideLen - 2 + sideLen * 2);

		Vector2i result;
		if (indexInRing < sideLen)
		{
			result = new Vector2i(ringIndex, si > shift ? -(si - shift * 2) + 1 : -si);
		} else if (indexInRing - sideLen < sideLen - 2)
		{
			result = new Vector2i(-(si - shift + 1), -ringIndex);
		} else if (indexInRing - sideLen * 2 + 2 < sideLen)
		{
			result = new Vector2i(-ringIndex, si - shift);
		} else
		{
			result = new Vector2i(si - shift + 1, ringIndex);
		}

		return result;
	}

	public static int addWithBound(int currentNumber, int numberToAdd, int upperBound)
	{
		return (upperBound <= 0) ? currentNumber + numberToAdd : (currentNumber + numberToAdd) % upperBound;
	}

	private static int indexInRing(int ring, int n, int indexShift)
	{
		if (ring == 0)
			return 0;

		int totalTiles = 0;
		for (int i = 1; i < ring; i++)
			totalTiles += getTilesInRing(i);

		return addWithBound(n - totalTiles - 1, indexShift, getTilesInRing(ring));
	}

	private static int getRingIndex(int n)
	{
		if (n == 0)
			return 0;

		final int MAX_SEARCH = 16;
		int t = 0;

		for (int i = 1; i < MAX_SEARCH; i++)
		{
			t += getTilesInRing(i);
			if (n <= t)
				return i;
		}
		return 0;
	}

	private static int getTilesInRing(int ring)
	{
		return (ring == 0) ? 1 : getSideLen(ring) * 4 - 4;
	}

	private static int getSideLen(int ring)
	{
		return (ring == 0) ? 0 : ring * 2 + 1;
	}
}