package steve6472.funnylib.menu;

/**
 * Created by steve6472
 * Date: 9/13/2022
 * Project: StevesFunnyLibrary <br>
 */
record SlotLoc(int x, int y)
{
	int toIndex(int ox, int oy, int rows)
	{
		if (x - ox < 0 || x - ox > 8 || y - oy < 0 || y - oy > rows) return -1;

		return (x - ox) + (y - oy) * 9;
	}
}
